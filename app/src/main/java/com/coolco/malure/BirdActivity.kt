package com.coolco.malure

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.coolco.malure.databinding.ActivityBirdBinding
import java.net.URL
import java.util.*


class BirdActivity : AppCompatActivity() {
    lateinit var binding: ActivityBirdBinding
    private val adapter = DossierAdapter()
    private val adapter2 = DossierAdapter()
    private val model: BirdModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Malure)
        super.onCreate(savedInstanceState)
        binding = ActivityBirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val attrib = window.attributes
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
            attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        binding.whereToFindList.layoutManager = GridLayoutManager(this, 2)
        binding.whereToFindList.adapter = adapter
        val localIntent = intent.getStringExtra("id")
        Log.d("BIRDA", localIntent.toString())
        /*binding.greenMic.setOnClickListener{
            startActivity(Intent(this, Search::class.java))
        }*/

        binding.yearlyList.layoutManager = GridLayoutManager(this, 2)
        binding.yearlyList.adapter = adapter2
        if (localIntent != null) {
            model.getSearchResults(localIntent).observe(this, Observer<BirdObject> { bird ->
                Glide.with(this)
                    .load("http://$HOST:8080/get_bird_pic/${bird.id}")
                    .into(binding.birdImage)
                binding.birdName.text = bird.name
                binding.latinName.text = bird.latin
                for (i in bird.find){
                    Log.d("BIRD", i.toString())
                    i["text"]?.let { DossierPoint(it, resources.getIdentifier(i["ic"], "drawable", this.packageName) ) }
                        ?.let { adapter.addDossierPoint(it) }
                }

                binding.whenToFindVal.text = bird.whenToFind
                binding.feederVal.text = bird.feeder
                binding.nestingVal.text = bird.nesting
                binding.socialVal.text = bird.social
                binding.habitatVal.text = bird.area
                binding.migrationVal.text = bird.migration
                for (i in bird.yearly){
                    Log.d("BIRD", i.toString())
                    i["text"]?.let { DossierPoint(it, resources.getIdentifier(i["ic"], "drawable", this.packageName) ) }
                        ?.let { adapter2.addDossierPoint(it) }
                }

            })
        }
        /*for (i: Int in 0..1) {
            adapter.addDossierPoint(DossierPoint("Cool bird i found", R.drawable.ic_outline_bookmarks_24, UUID.randomUUID()))
        }
        for (i: Int in 0..1) {
            adapter.addDossierPoint(DossierPoint("Cool bird i found", R.drawable.ic_outline_bookmarks_24, UUID.randomUUID()))
        }
        for (i: Int in 0..1) {
            adapter2.addDossierPoint(DossierPoint("Cool bird i did not find", R.drawable.ic_outline_mic_none_24, UUID.randomUUID()))
        }*/
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.guide -> {
                    startActivity(Intent(this, GuideActivity::class.java))
                }
                R.id.library -> {
                    startActivity(Intent(this, LibraryActivity::class.java))
                }
            }
            true
        }
    }

}