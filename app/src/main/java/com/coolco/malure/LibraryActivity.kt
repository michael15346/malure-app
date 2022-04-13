package com.coolco.malure

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.coolco.malure.databinding.ActivityLibraryBinding


class LibraryActivity : AppCompatActivity() {
    lateinit var binding: ActivityLibraryBinding
    private val adapter = BirdAdapter()
    private val birdViewModel: BirdViewModel by viewModels {
        BirdViewModelFactory((application as BirdApplication).repository)
    }
    private val libraryViewModel: LibraryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Malure)
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.libraryBirds.layoutManager = GridLayoutManager(this, 2)
        binding.libraryBirds.adapter = adapter
        binding.bottomNavigation.selectedItemId = R.id.library
        val attrib = window.attributes
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
            attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            //launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        birdViewModel.allWords.observe(this, Observer { birds ->
            adapter.clear()
            for (i in birds){
                Log.d("LIBRARY_ACTIVITY", i.id)
            }
            libraryViewModel.getSearchResults(birds).observe(this, Observer{fullBirds ->
                for (bird in fullBirds){
                    adapter.addBird(bird)
                }
            })
        })

        /*for (i: Int in 0..4) {
            adapter.addBird(BirdResult(
                "Cool bird i found",
                "Coolius Birdius",
                "coolius_birdus",
                80
            ))
        }*/

       binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.search -> {
                    startActivity(Intent(this, Search::class.java))
                }
                R.id.guide -> {
                    startActivity(Intent(this, GuideActivity::class.java))
                }
            }
            true
        }
        //}*/
        /*binding.whereToFindList.layoutManager = GridLayoutManager(this, 2)
        binding.whereToFindList.adapter = adapter
        val localIntent = intent.getIntExtra("id", 0)
        Log.d("BIRDA", localIntent.toString())
        /*binding.greenMic.setOnClickListener{
            startActivity(Intent(this, Search::class.java))
        }*/
        for (i: Int in 0..1) {
            adapter.addDossierPoint(DossierPoint("Cool bird i found", R.drawable.ic_outline_bookmarks_24, UUID.randomUUID()))
        }
        binding.yearlyList.layoutManager = GridLayoutManager(this, 2)
        binding.yearlyList.adapter = adapter2
        for (i: Int in 0..1) {
            adapter.addDossierPoint(DossierPoint("Cool bird i found", R.drawable.ic_outline_bookmarks_24, UUID.randomUUID()))
        }
        for (i: Int in 0..1) {
            adapter2.addDossierPoint(DossierPoint("Cool bird i did not find", R.drawable.ic_outline_mic_none_24, UUID.randomUUID()))
        }
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.guide -> {
                    startActivity(Intent(this, GuideActivity::class.java))
                }
            }
            true
        }*/
    }

}