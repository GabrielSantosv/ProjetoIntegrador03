package com.example.projetointegrador3.app2

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetointegrador3.app2.databinding.ItemRecentAlertBinding

class RecentAlertsAdapter : ListAdapter<RecentAlert, RecentAlertsAdapter.ViewHolder>(AlertDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentAlertBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemRecentAlertBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(alert: RecentAlert) {
            binding.apply {
                alertTitleTextView.text = alert.title
                alertLocationTextView.text = alert.location
                alertDateTextView.text = alert.date

                // Configurar cor do indicador de prioridade
                val color = when (alert.priority.uppercase()) {
                    "ALTA" -> Color.RED
                    "MÉDIA" -> Color.YELLOW
                    "BAIXA" -> Color.GREEN
                    else -> Color.GRAY
                }
                priorityIndicator.setBackgroundColor(color)
                statusIcon.setColorFilter(color)
            }
        }
    }

    private class AlertDiffCallback : DiffUtil.ItemCallback<RecentAlert>() {
        override fun areItemsTheSame(oldItem: RecentAlert, newItem: RecentAlert): Boolean {
            return oldItem.title == newItem.title && oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: RecentAlert, newItem: RecentAlert): Boolean {
            return oldItem == newItem
        }
    }
} 