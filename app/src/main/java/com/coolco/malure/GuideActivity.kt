package com.coolco.malure

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.coolco.malure.databinding.ActivityMainBinding


class GuideActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val adapter = PlaceCardAdapter()
    private var guideViewModel = GuideViewModel()
    private fun placeCardInit(){
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        /*adapter.addPlace(PlaceCard(R.drawable.img_2, "Сергиевка", 2200, "sergievka"))
        adapter.addPlace(PlaceCard(R.drawable.img_3, "Знаменка", 5500, "znamenka"))
        adapter.addPlace(PlaceCard(R.drawable.img_4, "Большеижорский пляж", 18300, "izhora"))
        adapter.addPlace(PlaceCard(R.drawable.img_1, "Гостилицкий заказник", 24300, "gostil"))

        adapter.addPlace(PlaceCard(R.drawable.img_5, "Дворцовый парк (Гатчина)", 38200, "gatchina"))*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Malure)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigation.selectedItemId = R.id.guide
        placeCardInit()

        val attrib = window.attributes
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
            attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            /*launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        }*/

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.search -> {
                    startActivity(Intent(this, Search::class.java))
                }
                R.id.library -> {
                    startActivity(Intent(this, LibraryActivity::class.java))
                }
            }
            true
        }
        val locationManager: LocationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {
            }
        val locationListenerGPS: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude
                val msg = "New Latitude: " + latitude + "New Longitude: " + longitude
                Log.d("LOCATION_GPS", msg)
            //Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show()
            }

            //@Deprecated("Deprecated in Java")
            //override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        isLocationEnabled(locationManager)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1.0.toFloat(), locationListenerGPS)
        val loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val lat = loc?.latitude
        val lon = loc?.longitude
        Log.d("GPS_COOLTHING", locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).toString())
        if (lat != null) {
            if (lon != null) {
                guideViewModel.getSearchResults(lat, lon).observe(this, Observer { birds ->
                    for (i in birds){
                        Log.d("LIBRARY_ACTIVITY", i.toString())
                        adapter.addPlace(i)
                    }
                })
            }
        }
    }
}