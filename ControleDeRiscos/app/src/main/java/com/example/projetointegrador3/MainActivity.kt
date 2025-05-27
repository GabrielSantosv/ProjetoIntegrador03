package com.example.projetointegrador3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.projetointegrador3.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var storage: FirebaseStorage

    private lateinit var riskImageView: ImageView

    private var capturedImage: Bitmap? = null

    private val REQUEST_LOCATION_PERMISSION = 1001
    private val REQUEST_CAMERA_PERMISSION = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        riskImageView = binding.riskImageView

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            getLocation()
        }

        binding.registerRiskButton.setOnClickListener {
            val title = binding.riskTitleEditText.text.toString()
            val description = binding.riskDescriptionEditText.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                registerRisk(title, description)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.takePhotoButton.setOnClickListener {
            checkCameraPermission()
        }
    }

    private fun registerRisk(title: String, description: String) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener(this, OnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    val userId = auth.currentUser?.uid ?: "desconhecido"
                    val riskId = database.child("riscos").push().key

                    if (riskId != null) {
                        // Se houver imagem capturada, primeiro fazemos o upload dela
                        capturedImage?.let { bitmap ->
                            val imageRef = storage.reference.child("imagensRisco/$riskId.jpg")

                            val baos = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            val data = baos.toByteArray()

                            val uploadTask = imageRef.putBytes(data)
                            uploadTask.addOnSuccessListener {
                                imageRef.downloadUrl.addOnSuccessListener { uri ->
                                    val risk = mapOf(
                                        "titulo" to title,
                                        "descricao" to description,
                                        "usuarioId" to userId,
                                        "latitude" to latitude,
                                        "longitude" to longitude,
                                        "imagemUrl" to uri.toString()
                                    )

                                    salvarNoBanco(riskId, risk)
                                }
                            }.addOnFailureListener {
                                Toast.makeText(this, "Erro ao enviar imagem!", Toast.LENGTH_SHORT).show()
                            }
                        } ?: run {
                            // Sem imagem
                            val risk = mapOf(
                                "titulo" to title,
                                "descricao" to description,
                                "usuarioId" to userId,
                                "latitude" to latitude,
                                "longitude" to longitude
                            )
                            salvarNoBanco(riskId, risk)
                        }
                    }
                } else {
                    Toast.makeText(this, "Não foi possível obter a localização.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun salvarNoBanco(riskId: String, risk: Map<String, Any>) {
        database.child("riscos").child(riskId).setValue(risk)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Risco registrado com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erro ao registrar risco. Tente novamente.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener(this, OnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Toast.makeText(this, "Localização: $latitude, $longitude", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResult.launch(intent)
    }

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                capturedImage = imageBitmap
                displayImage(imageBitmap)
            }
        }

    private fun displayImage(bitmap: Bitmap) {
        riskImageView.setImageBitmap(bitmap)
    }
}
