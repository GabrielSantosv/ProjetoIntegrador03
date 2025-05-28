package com.example.projetointegrador3.app2

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetointegrador3.app2.databinding.ActivityDashboardBinding
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var map: GoogleMap
    private lateinit var alertsAdapter: RecentAlertsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        setupCharts()
        setupRecyclerView()
        setupMap()
        loadData()
    }

    private fun setupCharts() {
        // Configuração do gráfico de pizza
        binding.pieChartRiskTypes.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            legend.textSize = 12f
            legend.isEnabled = true
        }

        // Configuração do gráfico de barras
        binding.barChartRisksByArea.apply {
            description.isEnabled = false
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
            legend.isEnabled = true
            setFitBars(true)
        }
    }

    private fun setupRecyclerView() {
        alertsAdapter = RecentAlertsAdapter()
        binding.recentAlertsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = alertsAdapter
        }
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapPreview) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun loadData() {
        // Carregar total de riscos
        db.collection("riscos")
            .get()
            .addOnSuccessListener { documents ->
                binding.totalRisksTextView.text = documents.size().toString()
                
                // Processar dados para os gráficos
                processRiskTypesData(documents)
                processRisksByAreaData(documents)
                
                // Atualizar mapa
                documents.forEach { doc ->
                    val lat = doc.getDouble("latitude") ?: return@forEach
                    val lng = doc.getDouble("longitude") ?: return@forEach
                    if (::map.isInitialized) {
                        val position = LatLng(lat, lng)
                        map.addMarker(MarkerOptions().position(position))
                    }
                }

                // Carregar alertas recentes
                val recentAlerts = documents
                    .sortedByDescending { it.getTimestamp("timestamp")?.toDate() }
                    .take(5)
                    .map { doc ->
                        RecentAlert(
                            title = doc.getString("titulo") ?: "",
                            location = "Lat: ${doc.getDouble("latitude")}, Lng: ${doc.getDouble("longitude")}",
                            date = doc.getTimestamp("timestamp")?.toDate()?.let { 
                                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)
                            } ?: "",
                            priority = doc.getString("prioridade") ?: "ALTA"
                        )
                    }
                alertsAdapter.submitList(recentAlerts)
            }
    }

    private fun processRiskTypesData(documents: QuerySnapshot) {
        val riskTypes = documents
            .mapNotNull { it.getString("tipo") }
            .groupingBy { it }
            .eachCount()

        val entries = riskTypes.map { (type, count) ->
            PieEntry(count.toFloat(), type)
        }

        val dataSet = PieDataSet(entries, "Tipos de Risco")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.BLACK

        binding.pieChartRiskTypes.data = PieData(dataSet)
        binding.pieChartRiskTypes.invalidate()
    }

    private fun processRisksByAreaData(documents: QuerySnapshot) {
        val risksByArea = documents
            .mapNotNull { it.getString("area") }
            .groupingBy { it }
            .eachCount()

        val entries = risksByArea.map { (area, count) ->
            BarEntry(risksByArea.keys.indexOf(area).toFloat(), count.toFloat())
        }

        val dataSet = BarDataSet(entries, "Riscos por Área")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 12f

        val barData = BarData(dataSet)
        binding.barChartRisksByArea.data = barData
        binding.barChartRisksByArea.xAxis.valueFormatter = IndexAxisValueFormatter(risksByArea.keys.toList())
        binding.barChartRisksByArea.invalidate()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Configurar zoom inicial para o Brasil
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-15.7801, -47.9292), 4f))
    }
}

data class RecentAlert(
    val title: String,
    val location: String,
    val date: String,
    val priority: String
) 