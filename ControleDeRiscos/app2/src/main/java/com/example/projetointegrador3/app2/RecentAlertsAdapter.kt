package com.example.projetointegrador3.app2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetointegrador3.app2.databinding.ItemRecentAlertBinding
import java.text.SimpleDateFormat
import java.util.*

class RecentAlertsAdapter : ListAdapter<Risco, RecentAlertsAdapter.AlertViewHolder>(AlertDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding = ItemRecentAlertBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AlertViewHolder(
        private val binding: ItemRecentAlertBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        fun bind(risco: Risco) {
            binding.apply {
                alertTitle.text = risco.titulo
                alertDescription.text = risco.descricao
                alertTimestamp.text = dateFormat.format(risco.timestamp)
            }
        }
    }

    private class AlertDiffCallback : DiffUtil.ItemCallback<Risco>() {
        override fun areItemsTheSame(oldItem: Risco, newItem: Risco): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: Risco, newItem: Risco): Boolean {
            return oldItem == newItem
        }
    }
} 