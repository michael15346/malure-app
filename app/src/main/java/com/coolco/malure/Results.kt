package com.coolco.malure

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.coolco.malure.databinding.ActivityResultsBinding

class Results : AppCompatActivity() {

    lateinit var binding: ActivityResultsBinding
    private val adapter = BirdAdapterResults()

    private val model: ResultsViewModel by viewModels()
    private var launcher: ActivityResultLauncher<Intent>? = null
    override fun onCreate(savedInstanceState: Bundle?){
        setTheme(R.style.Theme_Malure)
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.guide -> {
                    startActivity(Intent(this, GuideActivity::class.java))
                }
            }
            true
        }*/
        val attrib = window.attributes
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
            attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter
        val bundle = intent.extras
        //if (bundle != null)
        if (bundle != null) {
                model.getSearchResults(bundle).observe(this, Observer<MutableList<BirdResult>>{ birds ->
                    Log.d("RESULT", birds.toString())
                    for (bird in birds){
                        adapter.addPlace(bird)
                    }

                })
        }
        //}
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.library -> {
                    startActivity(Intent(this, LibraryActivity::class.java))
                }
                R.id.guide -> {
                    startActivity(Intent(this, GuideActivity::class.java))
                }
            }
            true
        }
    }
}