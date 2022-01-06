package com.app.hotspringsofbritishcolumbia

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.app.hotspringsofbritishcolumbia.databinding.ActivityDisplayMapBinding
import com.app.hotspringsofbritishcolumbia.models.UserMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds

private const val TAG = "DisplayMapActivity"
class DisplayMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var mMap: GoogleMap
    private lateinit var userMap: UserMap
    private lateinit var binding: ActivityDisplayMapBinding
    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        else
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
    }
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
                if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                    mMap.isMyLocationEnabled = true
                }
                else {
                    Toast.makeText(this, "User has not granted location access permission", Toast.LENGTH_LONG).show()
                    finish()
             }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDisplayMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userMap = intent.getSerializableExtra(EXTRA_USER_MAP) as UserMap

        supportActionBar?.title =userMap.title
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocationAccess()

        Log.i(TAG, "user map to render: ${userMap.title}")

        val boundsBuilder = LatLngBounds.Builder()
        for (place in userMap.places) {
            val latLng = LatLng(place.latitude, place.longitude)
            boundsBuilder.include(latLng)
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.addMarker(MarkerOptions().position(latLng).title(place.title).snippet(place.description).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )

        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 1000, 1000, 0))
    }
}