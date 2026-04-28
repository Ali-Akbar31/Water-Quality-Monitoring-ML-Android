package com.example.waterqualitymonitor

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ViewReportsActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var reportRecyclerView: RecyclerView
    private lateinit var reportArrayList: ArrayList<Report>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_reports)

        reportRecyclerView = findViewById(R.id.recyclerViewReports)
        reportRecyclerView.layoutManager = LinearLayoutManager(this)
        reportRecyclerView.setHasFixedSize(true)

        reportArrayList = arrayListOf<Report>()

        // Reports node se data uthao
        getReportsData()
    }

    private fun getReportsData() {
        dbRef = FirebaseDatabase.getInstance().getReference("Reports")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reportArrayList.clear()
                if (snapshot.exists()){
                    for (reportSnapshot in snapshot.children){
                        val report = reportSnapshot.getValue(Report::class.java)
                        if (report != null) {
                            reportArrayList.add(report)
                        }
                    }
                    reportRecyclerView.adapter = ReportAdapter(reportArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewReportsActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}