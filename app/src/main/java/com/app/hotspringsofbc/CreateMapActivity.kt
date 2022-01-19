package com.app.hotspringsofbc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.hotspringsofbc.databinding.ActivityCreateMapBinding
import com.app.hotspringsofbc.models.Place
import com.app.hotspringsofbc.models.UserMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

private const val TAG = "CreateMapActivity"
class CreateMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityCreateMapBinding
    private var markers: MutableList<Marker> = mutableListOf()
    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST)
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                mMap.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "User has not granted location access permission",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }

        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = intent.getStringExtra(EXTRA_MAP_TITLE)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapFragment.view?.let {
            Snackbar.make(it, "Long press to add a marker,also tap the info window to remove marker", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", {})
                .setActionTextColor(ContextCompat.getColor(this, android.R.color.white))
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_map, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Check that the item is the save menu option
        if (item.itemId == R.id.miSave) {
            Log.i(TAG, "Tapped on save!")
            if (markers.isEmpty()) {
                Toast.makeText(
                    this,
                    "There must be at least one marker on the map",
                    Toast.LENGTH_LONG
                ).show()
                return true
            }
        }
        val places = markers.map { marker -> Place(marker.title, marker.snippet, marker.position.latitude, marker.position.longitude ) }
        val userMap = UserMap(intent.getStringExtra(EXTRA_MAP_TITLE), places )
        val data = Intent()
        data.putExtra(EXTRA_USER_MAP, userMap)
            setResult(Activity.RESULT_OK, data)
            finish()
        return true

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocationAccess()

        mMap.setOnInfoWindowClickListener { markerToDelete ->
            Log.i(TAG, "onWindowClickListener- delete this marker")
            markers.remove(markerToDelete)
            markerToDelete.remove()
            Toast.makeText(this, "Marker deleted", Toast.LENGTH_SHORT).show()

        }


        mMap.setOnMapLongClickListener { latlng ->
            Log.i(TAG, "onMapLongClickListener")
            showAlertDialog(latlng)
        }


        val BritishColumbia = LatLng(51.2, -120.6)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BritishColumbia, 7f))
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
    }

    private fun showAlertDialog(latlng: LatLng) {
        val placeFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_place, null)
        val dialog =
            AlertDialog.Builder(this)
            .setTitle("Create a marker")
            .setView(placeFormView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Ok", null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = placeFormView.findViewById<EditText>(R.id.etTitle).text.toString()
            val description = placeFormView.findViewById<EditText>(R.id.etDescription).text.toString()
            if (title.trim().isEmpty() || description.trim().isEmpty()) {
                Toast.makeText(this, "Place must have non-empty title and description", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val marker = mMap.addMarker(MarkerOptions().position(latlng).title(title).snippet("description").draggable(true))
            if (marker != null) {
                markers.add(marker)
                dialog.dismiss()
        }

        }
    }
}