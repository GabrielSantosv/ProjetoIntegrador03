package com.example.projetointegrador3.app2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.projetointegrador3.app2.databinding.ActivityRiskMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class RiskMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityRiskMapBinding
    private lateinit var map: GoogleMap
    private lateinit var firestore: FirebaseFirestore

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiskMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
        loadRiskLocations()
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun loadRiskLocations() {
        firestore.collection("risks")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val latitude = document.getDouble("latitude") ?: continue
                    val longitude = document.getDouble("longitude") ?: continue
                    val description = document.getString("description") ?: "Risco não descrito"
                    val severity = document.getString("severity") ?: "Médio"

                    val location = LatLng(latitude, longitude)
                    map.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title("Risco: $severity")
                            .snippet(description)
                    )
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar riscos: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(this, "Permissão de localização necessária", Toast.LENGTH_LONG).show()
            }
        }
    }
} 