package com.example.projetointegrador3.app2

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projetointegrador3.app2.databinding.ActivityReportGenerationBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import java.io.File
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

        binding.btnGenerateReport.setOnClickListener {
            generateReport()
        }
    }

    private fun generateReport() {
        firestore.collection("risks")
            .get()
            .addOnSuccessListener { documents ->
                val reportFile = createReportFile()
                val pdfWriter = PdfWriter(reportFile)
                val pdfDoc = PdfDocument(pdfWriter)
                val document = Document(pdfDoc)

                // Adiciona título
                val title = Paragraph("Relatório de Riscos")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20f)
                document.add(title)

                // Adiciona data
                val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(Date())
                document.add(Paragraph("Data: $date"))

                // Cria tabela
                val table = Table(4)
                table.addCell("Descrição")
                table.addCell("Severidade")
                table.addCell("Localização")
                table.addCell("Data")

                // Adiciona dados
                for (document in documents) {
                    val description = document.getString("description") ?: "Não especificado"
                    val severity = document.getString("severity") ?: "Médio"
                    val latitude = document.getDouble("latitude") ?: 0.0
                    val longitude = document.getDouble("longitude") ?: 0.0
                    val timestamp = document.getTimestamp("timestamp")?.toDate()
                    val dateStr = if (timestamp != null) {
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestamp)
                    } else "Não especificado"

                    table.addCell(description)
                    table.addCell(severity)
                    table.addCell("$latitude, $longitude")
                    table.addCell(dateStr)
                }

                document.add(table)
                document.close()

                Toast.makeText(
                    this,
                    "Relatório gerado com sucesso: ${reportFile.absolutePath}",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Erro ao gerar relatório: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun createReportFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "relatorio_riscos_$timestamp.pdf"
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "RelatoriosRiscos"
        )
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return File(directory, fileName)
    }
} 