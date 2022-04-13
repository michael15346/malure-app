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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.coolco.malure.databinding.ActivityPlacePageBinding

class PlacePage : AppCompatActivity() {

    lateinit var binding: ActivityPlacePageBinding
    private val adapterPB = BirdAdapter()

    private val adapterRS = BirdAdapter()
    private val placeViewModel = PlaceViewModel()
    private val adapterWS = BirdAdapter()
    override fun onCreate(savedInstanceState: Bundle?){
        setTheme(R.style.Theme_Malure)
        super.onCreate(savedInstanceState)
        binding = ActivityPlacePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setScrollEnabled(true)

        /*binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.guide -> {
                    launcher?.launch(Intent(this, GuideActivity::class.java))
                }
            }
            true
        }
        binding.searchImage.setOnClickListener {
            binding.searchProgressText.setText(R.string.bird_search_in_progress)
            Log.d("SEARCH", "Button pressed")
        }*/
        val attrib = window.attributes
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
            attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        binding.popularBirds.layoutManager = GridLayoutManager(this, 2)
        binding.popularBirds.adapter = adapterPB

        binding.rareSights.layoutManager = GridLayoutManager(this, 2)
        binding.rareSights.adapter = adapterRS

        binding.wrongSeason.layoutManager = GridLayoutManager(this, 2)
        binding.wrongSeason.adapter = adapterWS

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
        LocationManagerCompat.isLocationEnabled(locationManager)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1.0.toFloat(), locationListenerGPS)
        val loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val lat = loc?.latitude
        val lon = loc?.longitude
        Log.d("GPS_COOLTHING", locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).toString())
        //binding.distance = intent.getExtra
        binding.greenMic.setOnClickListener{
            startActivity(Intent(this, Search::class.java))
        }
        intent.getStringExtra("id")?.let {
            placeViewModel.getSearchResults(it).observe(this, Observer { birds ->
                Log.d("GPS","http://$HOST:8080/get_place_pic/${birds.ID}" )
                binding.description.text = birds.compText
                binding.distance.text = birds.Distance.toString()
                binding.placeType.text = birds.type
                binding.placeName.text = birds.Title
                for (bird in birds.popular){
                    adapterPB.addBird(bird)
                }
                for (bird in birds.rare){
                    adapterRS.addBird(bird)
                }
                for (bird in birds.season){
                    adapterWS.addBird(bird)
                }
                Glide.with(this).load("http://$HOST:8080/get_place_pic/${birds.ID}").into(binding.placeImage)
            })
        }
        //when()

    }
}