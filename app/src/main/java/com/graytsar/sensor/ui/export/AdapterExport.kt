package com.graytsar.sensor.ui.export

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.graytsar.sensor.R
import com.graytsar.sensor.databinding.ItemExportBinding
import com.graytsar.sensor.repository.data.Record
import com.graytsar.sensor.utils.Globals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

/**
 * Adapter for recording sessions.
 */
class AdapterExport(
    private val fragment: ExportFragment,
    private val viewModel: ExportViewModel
) : PagingDataAdapter<Record, AdapterExport.ViewHolderExport>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderExport {
        val binding = ItemExportBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderExport(binding)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolderExport, position: Int) {
        getItem(position)?.let { item ->
            val model = Globals.sensors.find {
                it.sensorType == item.sensorType
            }!!

            holder.title.text = holder.itemView.context.getString(model.name)

            //date and time when the recording was started
            val date = if (Build.VERSION.SDK_INT >= 26) {
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(item.timestamp),
                    ZoneId.systemDefault()
                ).format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss"))
            } else {
                SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format(Date(item.timestamp))
            }
            //amount of sensor events recorded
            val count = item.count
            //display date-time and amount of sensor events recorded.
            holder.message.text =
                holder.itemView.context.getString(R.string.export_item_detail, date, count)

            holder.menu.setOnClickListener {
                val popupMenu = PopupMenu(holder.itemView.context, holder.menu)
                popupMenu.inflate(R.menu.menu_export_pop)
                popupMenu.setOnMenuItemClickListener { itemMenu ->
                    when (itemMenu.itemId) {
                        R.id.menuExportOpen -> {
                            saveToFile(item)
                            true
                        }

                        R.id.menuExportDelete -> {
                            deleteRecord(item)
                            true
                        }

                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
    }

    /**
     * Save a recording session to a file in the Downloads folder.
     * Show a [Snackbar] with a button to open the file directly.
     */
    private fun saveToFile(item: Record) {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            val uri = if (Build.VERSION.SDK_INT >= 29) {
                viewModel.saveFileToDownloads(
                    context = fragment.requireContext(),
                    sensorType = item.sensorType,
                    recordingId = item.id,
                    fileName = "${item.timestamp}-export.txt"
                )
            } else {
                viewModel.saveFileToDownloads(
                    sensorType = item.sensorType,
                    recordingId = item.id,
                    fileName = "${item.timestamp}-export.txt"
                )
            }

            withContext(Dispatchers.Main) {
                Snackbar.make(
                    fragment.requireView(),
                    R.string.export_write_complete,
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.all_open) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "text/plain")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    fragment.startActivity(intent)
                }.show()
            }
        }
    }

    /**
     * Delete a recording session from the database.
     */
    private fun deleteRecord(item: Record) {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(R.string.all_warning)
            .setMessage(R.string.export_item_delete_warning)
            .setPositiveButton(android.R.string.ok) { dialogInterface: DialogInterface, _: Int ->
                fragment.lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.sessionRepository.deleteById(item.id)
                }
                dialogInterface.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    /**
     * ViewHolder for recording sessions.
     */
    inner class ViewHolderExport(binding: ItemExportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.title
        val message = binding.message
        val menu = binding.menu
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Record>() {
            override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
                return oldItem == newItem
            }
        }
    }
}