package com.example.waterqualitymonitor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReportAdapter(private val reportList: ArrayList<Report>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val currentItem = reportList[position]

        holder.tvQuality.text = "Quality: ${currentItem.waterQuality}"
        holder.tvConfidence.text = "Confidence: ${currentItem.confidence}"
        holder.tvEmail.text = "User: ${currentItem.userEmail}"
        holder.tvStatus.text = "Status: ${currentItem.status}"

        // --- YEH NEW LINE HAI (Location wali) ---
        holder.tvLocation.text = "GPS: ${currentItem.latitude}, ${currentItem.longitude}"
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuality: TextView = itemView.findViewById(R.id.tvReportQuality)
        val tvConfidence: TextView = itemView.findViewById(R.id.tvReportConfidence)
        val tvEmail: TextView = itemView.findViewById(R.id.tvReportEmail)
        val tvStatus: TextView = itemView.findViewById(R.id.tvReportStatus)
        // --- YEH BHI NEW HAI ---
        val tvLocation: TextView = itemView.findViewById(R.id.tvReportLocation)
    }
}