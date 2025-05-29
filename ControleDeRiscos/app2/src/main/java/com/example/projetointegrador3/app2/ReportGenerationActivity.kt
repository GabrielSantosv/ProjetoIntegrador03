package com.example.projetointegrador3.app2

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projetointegrador3.app2.databinding.ActivityReportGenerationBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportGenerationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportGenerationBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportGenerationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        setupCharts()
        loadData()
    }

    private fun setupCharts() {
        // Configuração do gráfico de barras
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setScaleEnabled(true)
            setPinchZoom(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
            }

            axisRight.isEnabled = false
            legend.isEnabled = true
        }

        // Configuração do gráfico de pizza
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleRadius(30f)
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            legend.isEnabled = true
        }
    }

    private fun loadData() {
        firestore.collection("risks")
            .get()
            .addOnSuccessListener { documents ->
                val severityCount = mutableMapOf<String, Int>()
                val dateCount = mutableMapOf<String, Int>()

                documents.forEach { document ->
                    // Contagem por severidade
                    val severity = document.getString("severity") ?: "Médio"
                    severityCount[severity] = (severityCount[severity] ?: 0) + 1

                    // Contagem por data
                    val timestamp = document.getTimestamp("timestamp")?.toDate()
                    val dateStr = if (timestamp != null) {
                        SimpleDateFormat("dd/MM", Locale.getDefault()).format(timestamp)
                    } else "Não especificado"
                    dateCount[dateStr] = (dateCount[dateStr] ?: 0) + 1
                }

                updatePieChart(severityCount)
                updateBarChart(dateCount)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Erro ao carregar dados: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun updatePieChart(severityCount: Map<String, Int>) {
        val entries = severityCount.map { PieEntry(it.value.toFloat(), it.key) }
        val dataSet = PieDataSet(entries, "Severidade dos Riscos")
        dataSet.colors = listOf(
            Color.rgb(255, 99, 132),  // Vermelho
            Color.rgb(255, 159, 64),  // Laranja
            Color.rgb(255, 205, 86)   // Amarelo
        )

        binding.pieChart.data = PieData(dataSet)
        binding.pieChart.invalidate()
    }

    private fun updateBarChart(dateCount: Map<String, Int>) {
        val entries = dateCount.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val dataSet = BarDataSet(entries, "Riscos por Data")
        dataSet.color = Color.rgb(54, 162, 235)

        binding.barChart.data = BarData(dataSet)
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateCount.keys.toList())
        binding.barChart.invalidate()
    }
} 