package com.example.waterqualitymonitor

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // 1. Agar user pehle se login hai, to direct check karo wo kon hai
        if (auth.currentUser != null) {
            checkRoleAndRedirect(auth.currentUser!!.uid)
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToSignup = findViewById<TextView>(R.id.tvGoToSignup)

        // 2. Login Button Click
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val pass = etPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                // Firebase Login Function
                auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Login kamiyab! Ab Role check karo
                            val uid = auth.currentUser?.uid
                            if (uid != null) {
                                checkRoleAndRedirect(uid)
                            }
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Email aur Password likhein", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. Signup Page par jane ka raasta
        tvGoToSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    // --- YEH FUNCTION DATABASE CHECK KAREGA ---
    private fun checkRoleAndRedirect(uid: String) {
        val database = FirebaseDatabase.getInstance().getReference("Users").child(uid)

        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Database se "role" nikalo
                val role = snapshot.child("role").value.toString()

                if (role == "Admin") {
                    // Agar Admin hai to Green Screen par bhejo
                    Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                } else {
                    // Agar Analyst hai to Blue Screen par bhejo
                    Toast.makeText(this, "Welcome Analyst!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, DashboardActivity::class.java))
                }
                finish() // Login activity khatam karo
            } else {
                Toast.makeText(this, "User ka data nahi mila", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Internet Check Karein", Toast.LENGTH_SHORT).show()
        }
    }
}