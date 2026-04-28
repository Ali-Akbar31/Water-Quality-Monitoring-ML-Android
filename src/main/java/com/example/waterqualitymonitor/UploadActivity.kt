package com.example.waterqualitymonitor

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class UploadActivity : AppCompatActivity() {

    private lateinit var ivImage: ImageView
    private lateinit var tvResult: TextView
    private lateinit var tvConfidence: TextView
    private lateinit var btnSelect: Button
    private lateinit var btnSubmit: Button

    // AI & Location
    private lateinit var tflite: Interpreter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val labels = listOf("Unsafe", "Moderate", "Safe")
    // Data Variables
    private var finalResult: String = ""
    private var finalConfidence: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        ivImage = findViewById(R.id.ivSelectedImage)
        tvResult = findViewById(R.id.tvUploadResult)
        tvConfidence = findViewById(R.id.tvUploadConfidence)
        btnSelect = findViewById(R.id.btnSelectImage)
        btnSubmit = findViewById(R.id.btnSubmitReport)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            tflite = Interpreter(loadModelFile())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        btnSelect.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        btnSubmit.setOnClickListener {
            fetchLocationAndSubmit()
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            ivImage.setImageURI(uri)
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                classifyImage(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun classifyImage(bitmap: Bitmap) {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(224 * 224)
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)

        var pixel = 0
        for (i in 0 until 224) {
            for (j in 0 until 224) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value shr 16 and 0xFF) / 255.0f))
                byteBuffer.putFloat(((value shr 8 and 0xFF) / 255.0f))
                byteBuffer.putFloat(((value and 0xFF) / 255.0f))
            }
        }

        val output = Array(1) { FloatArray(3) }
        tflite.run(byteBuffer, output)

        val scores = output[0]
        val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: -1

        finalResult = labels[maxIndex]
        finalConfidence = "%.1f%%".format(scores[maxIndex] * 100)

        tvResult.text = "Quality: $finalResult"
        tvConfidence.text = "Confidence: $finalConfidence"
        btnSubmit.visibility = View.VISIBLE
    }

    private fun fetchLocationAndSubmit() {
        // Permission Check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        Toast.makeText(this, "Fetching GPS Location...", Toast.LENGTH_SHORT).show()

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // Location mil gayi, ab save karo
                saveReportToDatabase(location.latitude, location.longitude)
            } else {
                Toast.makeText(this, "GPS Error! Turn on Location", Toast.LENGTH_LONG).show()
                // Agar location na mile to 0.0 bhej do (Crash bachane ke liye)
                saveReportToDatabase(0.0, 0.0)
            }
        }
    }

    private fun saveReportToDatabase(lat: Double, lng: Double) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val databaseRef = FirebaseDatabase.getInstance().getReference("Reports")
        val reportId = databaseRef.push().key

        val report = Report(
            reportId = reportId,
            userId = user.uid,
            userEmail = user.email,
            waterQuality = finalResult,
            confidence = finalConfidence,
            latitude = lat,   // NEW: Real Location
            longitude = lng   // NEW: Real Location
        )

        if (reportId != null) {
            databaseRef.child(reportId).setValue(report)
                .addOnSuccessListener {
                    Toast.makeText(this, "Report + GPS Location Sent!", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadModelFile(): ByteBuffer {
        val fileDescriptor = assets.openFd("water_quality_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }
}