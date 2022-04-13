package com.coolco.malure

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.media.*
import android.media.AudioFormat.*
import android.media.AudioRecord.getMinBufferSize
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
//import androidx.compose.ui.viewinterop.AndroidView
import android.os.SystemClock.sleep
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.coolco.malure.databinding.ActivitySearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.lang.Exception

class Search : AppCompatActivity() {

    lateinit var binding: ActivitySearchBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private val model: SearchViewModel by viewModels()
    private val birdViewModel: BirdViewModel by viewModels{
        BirdViewModelFactory((application as BirdApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?){
        setTheme(R.style.Theme_Malure)
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {
            }
        var a = View.OnClickListener(){}
        a = View.OnClickListener() {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.RECORD_AUDIO
                    ) -> {
                        val bufSize = 480000
                        binding.searchImage.setOnClickListener(null)
                        var ar: AudioRecord
                        if (SDK_INT >= android.os.Build.VERSION_CODES.R){
                            try{

                                ar = AudioRecord(MediaRecorder.AudioSource.MIC, 48000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_OPUS, bufSize)
                            }
                            catch(e: Exception){ Log.d("RECORDER", "Failed to record in Opus at 48kHz")
                                try{
                                    ar = AudioRecord(MediaRecorder.AudioSource.MIC, 48000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_AAC_LC, bufSize)
                                }
                                catch(ex: Exception){
                                    Log.d("RECORDER", "Failed to record in AAC at 48kHz")
                                    ar = AudioRecord(MediaRecorder.AudioSource.MIC, 48000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufSize)
                                }
                            }
                        }
                        else if (SDK_INT >= android.os.Build.VERSION_CODES.P){
                            try{

                                ar = AudioRecord(MediaRecorder.AudioSource.MIC, 48000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_AAC_LC, bufSize)
                            }
                            catch(e: Exception){
                                Log.d("RECORDER", "Failed to record in AAC LC at 48kHz")
                                ar = AudioRecord(MediaRecorder.AudioSource.MIC, 48000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufSize)
                            }
                        }
                        else {
                            ar = AudioRecord(MediaRecorder.AudioSource.MIC, 48000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufSize)
                        }
                        binding.searchProgressText.setText(R.string.bird_search_in_progress)
                        model.getSearchResults(ar, bufSize).observe(this, Observer<MutableMap<String, Double>>{birds ->
                            Log.d("MainThread", birds.toString())
                            binding.searchProgressText.text = ""
                            if (!birds.isNullOrEmpty()){
                                val intent = Intent(this, Results::class.java)
                                birds.forEach { (key, value) ->
                                    birdViewModel.insert(Bird(key))
                                    intent.putExtra(
                                        key,
                                        value
                                    )
                                }
                                startActivity(intent)

                            }
                            else
                            {
                                binding.searchProgressText.text = "Птицы не найдены!"
                            }

                            binding.searchImage.setOnClickListener(a)
                        })
                    }
                    else -> {

                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
        }
        binding.searchImage.setOnClickListener(a)
        val attrib = window.attributes
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
            attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

    }
}