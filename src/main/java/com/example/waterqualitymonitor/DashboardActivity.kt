package com.example.waterqualitymonitor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // 1. Saaray Buttons ko dhoondo
        val btnCamera = findViewById<Button>(R.id.btnLiveCamera)
        val btnUpload = findViewById<Button>(R.id.btnUploadImage)
        val btnMap = findViewById<Button>(R.id.btnViewMap)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnHelp = findViewById<Button>(R.id.btnHelp) // New Button

        // 2. Camera Button Logic
        btnCamera.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 3. Upload Button Logic
        btnUpload.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

        // 4. Map Button Logic
        btnMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        // 5. Help Button Logic (New)
        btnHelp.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }

        // 6. Logout Button Logic
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}