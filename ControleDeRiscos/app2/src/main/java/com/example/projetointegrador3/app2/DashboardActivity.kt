package com.example.projetointegrador3.app2

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetointegrador3.app2.databinding.ActivityDashboardBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var recentAlertsAdapter: RecentAlertsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        setupRecyclerView()
        setupCharts()
        loadData()
    }

    private fun setupRecyclerView() {
        recentAlertsAdapter = RecentAlertsAdapter()
        binding.recentAlertsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = recentAlertsAdapter
        }
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
        db.collection("riscos")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val riscos = snapshot?.documents?.mapNotNull { it.toObject(Risco::class.java) } ?: emptyList()
                updateDashboard(riscos)
            }
    }

    private fun updateDashboard(riscos: List<Risco>) {
        // Atualizar contador total
        binding.totalRiscosText.text = "Total de Riscos: ${riscos.size}"

        // Atualizar gráfico de pizza (distribuição por tipo)
        updatePieChart(riscos)

        // Atualizar gráfico de barras (riscos por área)
        updateBarChart(riscos)

        // Atualizar lista de alertas recentes
        updateRecentAlerts(riscos)
    }

    private fun updatePieChart(riscos: List<Risco>) {
        val tipoCount = riscos.groupBy { it.tipo }.mapValues { it.value.size }
        val entries = tipoCount.map { PieEntry(it.value.toFloat(), it.key) }

        val dataSet = PieDataSet(entries, "Tipos de Risco")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val data = PieData(dataSet)
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.WHITE)

        binding.pieChart.apply {
            this.data = data
            description.isEnabled = false
            legend.isEnabled = true
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            animateY(1000)
            invalidate()
        }
    }

    private fun updateBarChart(riscos: List<Risco>) {
        val areaCount = riscos.groupBy { it.area }.mapValues { it.value.size }
        val entries = areaCount.map { BarEntry(areaCount.keys.indexOf(it.key).toFloat(), it.value.toFloat()) }

        val dataSet = BarDataSet(entries, "Riscos por Área")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val data = BarData(dataSet)
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)

        binding.barChart.apply {
            this.data = data
            description.isEnabled = false
            legend.isEnabled = true
            xAxis.valueFormatter = IndexAxisValueFormatter(areaCount.keys.toList())
            xAxis.granularity = 1f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.isEnabled = false
            animateY(1000)
            invalidate()
        }
    }

    private fun updateRecentAlerts(riscos: List<Risco>) {
        val recentRiscos = riscos.sortedByDescending { it.timestamp }.take(5)
        recentAlertsAdapter.submitList(recentRiscos)
    }
}

data class Risco(
    val titulo: String = "",
    val descricao: String = "",
    val tipo: String = "",
    val area: String = "",
    val prioridade: String = "",
    val timestamp: Date = Date()
) 