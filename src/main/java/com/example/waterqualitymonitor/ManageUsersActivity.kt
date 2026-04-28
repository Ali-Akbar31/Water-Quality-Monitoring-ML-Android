package com.example.waterqualitymonitor

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ManageUsersActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_users)

        // List ko setup karo
        userRecyclerView = findViewById(R.id.recyclerViewUsers)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf<User>()

        // Data lane wala function chalao
        getUsersData()
    }

    private fun getUsersData() {
        // Firebase ke "Users" folder se data mango
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userArrayList.clear() // Purana data saaf karo

                if (snapshot.exists()){
                    // Har user ko bari bari check karo
                    for (userSnap in snapshot.children){
                        val user = userSnap.getValue(User::class.java)
                        if (user != null) {
                            userArrayList.add(user) // List mein dalo
                        }
                    }
                    // List dikhane wale Adapter ko batao ke data aa gaya
                    userRecyclerView.adapter = UserAdapter(userArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageUsersActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}