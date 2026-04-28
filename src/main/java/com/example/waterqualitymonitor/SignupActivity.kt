package com.example.waterqualitymonitor

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etSignupEmail)
        val etPassword = findViewById<EditText>(R.id.etSignupPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupRole)

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val pass = etPassword.text.toString()

            // 1. Role Check Karo (Button dbane ke waqt)
            val selectedId = radioGroup.checkedRadioButtonId
            val role = if (selectedId == R.id.rbAdmin) "Admin" else "Analyst"

            if (email.isNotEmpty() && pass.isNotEmpty() && name.isNotEmpty()) {

                // 2. Firebase Account Banao
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = auth.currentUser?.uid
                            val userMap = mapOf("name" to name, "email" to email, "role" to role)

                            // 3. Database mein Role Save Karo
                            FirebaseDatabase.getInstance().getReference("Users").child(uid!!)
                                .setValue(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show()

                                    // --- YAHAN CHANGE KIYA HAI ---
                                    // Agar Admin select kiya tha, to Admin page par bhejo
                                    if (role == "Admin") {
                                        startActivity(Intent(this, AdminDashboardActivity::class.java))
                                    } else {
                                        // Warna Analyst page par bhejo
                                        startActivity(Intent(this, DashboardActivity::class.java))
                                    }
                                    finish() // Back button wapis signup par na laye
                                }
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}