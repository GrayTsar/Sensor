package com.graytsar.sensor.ui.export

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.graytsar.sensor.databinding.ItemExportBinding
import com.graytsar.sensor.repository.data.Record
import com.graytsar.sensor.utils.Globals
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class AdapterExport(
    val viewModel: ExportViewModel
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

            holder.title.text = holder.itemView.context.getString(model.title)

            val date = if (Build.VERSION.SDK_INT >= 26) {
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(item.timestamp),
                    ZoneId.systemDefault()
                ).format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss"))
            } else {
                SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format(Date(item.timestamp))
            }
            val count = item.count

            holder.message.text = "Recorded on $date with $count events"
        }
    }

    inner class ViewHolderExport(binding: ItemExportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.title
        val message = binding.message
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