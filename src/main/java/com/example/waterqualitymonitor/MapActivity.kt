package com.example.waterqualitymonitor

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. OSM Settings Load (Zaroori hai)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_map)

        // 2. Map Setup
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK) // Map ka style
        map.setMultiTouchControls(true) // Pinch to Zoom on

        // Default Zoom aur Location (Pakistan)
        val mapController = map.controller
        mapController.setZoom(10.0)
        val startPoint = GeoPoint(31.5204, 74.3587) // Lahore Coordinates
        mapController.setCenter(startPoint)

        dbRef = FirebaseDatabase.getInstance().getReference("Reports")

        // 3. Database se Reports uthao aur Pins lagao
        loadMapMarkers()
    }

    private fun loadMapMarkers() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (reportSnap in snapshot.children) {
                        val report = reportSnap.getValue(Report::class.java)

                        // Sirf wahi dikhao jinki location Sahi hai (0.0 nahi hai)
                        if (report != null && report.latitude != 0.0) {
                            val location = GeoPoint(report.latitude, report.longitude)

                            val marker = Marker(map)
                            marker.position = location
                            marker.title = "Quality: ${report.waterQuality}"
                            marker.snippet = "Conf: ${report.confidence}"
                            marker.subDescription = "Reported by: ${report.userEmail}"

                            map.overlays.add(marker)
                        }
                    }
                    map.invalidate() // Map refresh karo
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MapActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}