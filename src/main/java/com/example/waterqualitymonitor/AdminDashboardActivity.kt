package com.example.waterqualitymonitor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // 1. View Reports Button
        val btnViewReports = findViewById<Button>(R.id.btnViewReports)
        btnViewReports.setOnClickListener {
            val intent = Intent(this, ViewReportsActivity::class.java)
            startActivity(intent)
        }

        // 2. Manage Users Button (YEH NAYA ADD KIYA HAI)
        val btnManageUsers = findViewById<Button>(R.id.btnManageUsers)
        btnManageUsers.setOnClickListener {
            // Manage Users wali screen par jao
            val intent = Intent(this, ManageUsersActivity::class.java)
            startActivity(intent)
        }

        // 3. Logout Button
        val btnLogout = findViewById<Button>(R.id.btnAdminLogout)
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}