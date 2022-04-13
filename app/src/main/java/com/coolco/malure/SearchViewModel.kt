package com.coolco.malure

import android.R.attr.data
import android.media.AudioRecord
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.max
import kotlin.time.Duration


class SearchViewModel : ViewModel() {
    private var ar: AudioRecord? = null
    private var bufSize: Int? = null
    //private lateinit var temp: MutableMap<String, Double>
    fun getSearchResults(ar: AudioRecord, bufSize: Int): LiveData<MutableMap<String, Double>> {
        this.ar = ar
        this.bufSize = bufSize
        Log.d("TEXTTEXTTEXT", "reached birds")
        return birds
    }

    val birds: MutableLiveData<MutableMap<String, Double>> by lazy {
        MutableLiveData<MutableMap<String,Double>>().also{
            Log.d("TEXTTEXTTEXT", "in also")
            bufSize?.let { it1 -> ar?.let { it2 -> loadSearch(it2, it1) } }
            Log.d("TEXTTEXTTEXT", "after let")
        }
    }

    private fun loadSearch(ar: AudioRecord, bufSize: Int) {

        viewModelScope.launch(Dispatchers.IO){
            var read: Int
            ar.startRecording()
            val buf = ByteArray(bufSize)
            Log.d("RECORDER", "created bytearray")
            val url = URL("http://$HOST:8080/search")
            var urlConnection: HttpURLConnection
            val result = mutableMapOf<String, Double>()
//            val SAMPLE_RATE = Constants.SampleRate._48000()       // samlpe rate of the input audio
//            val CHANNELS = Constants.Channels.mono()            // type of the input audio mono or stereo
//            val APPLICATION = Constants.Application.audio()       // coding mode of the encoder
//            val FRAME_SIZE = Constants.FrameSize._120()           // default frame size for 48000Hz
//            val COMPLEXITY = Constants.Complexity.instance(10)    // encoder's algorithmic complexity
//            val BITRATE = Constants.Bitrate.max()
//            val codec = Opus()            // encoder's bitrate                           // getting an instance of Codec
//            codec.encoderInit(SAMPLE_RATE, CHANNELS, APPLICATION)
//            codec.encoderSetComplexity(COMPLEXITY)                // set the complexity
//            codec.encoderSetBitrate(BITRATE)
            for (i in 1..3){
                //try{
                    //urlConnection.disconnect()
                    read = ar.read(buf, 0, bufSize)

                    Log.d("RECORDER", "read bytes")

                    Log.d("RECORDER", read.toString())
                    urlConnection = url.openConnection() as HttpURLConnection
                    urlConnection.doInput = true
                    urlConnection.doOutput = true
                    urlConnection.requestMethod = "POST"
                    //Log.d("RECORDER", urlConnection.outputStream.toString())
                    urlConnection.setRequestProperty("Content-Type", "audio/vnd.wave")
                    urlConnection.setRequestProperty("Host", HOST)
                    urlConnection.setRequestProperty("Accept", "*/*")
                    urlConnection.setRequestProperty("Connection", "keep-alive")
                    urlConnection.setRequestProperty("Content-Length", read.toString())
                    //val encoded: ByteArray? = codec.encode(buf.take(read).toByteArray(), FRAME_SIZE)
                    urlConnection.outputStream.write(buf,0,read)
                    //Log.d("RECORDER", "encoded")
                    //Log.d("Opus", "encoded chunk size: ${encoded.size}")
                    //var test: ByteArray = byteArrayOf(0x01, 0x02, 0x03, 0x04)
                    /*while (true){
                        Log.d("TEST", test.slice(0..4 step 2).toString())2
                    }*/
                    urlConnection.connect()

                    Log.d("RECORDER", "set request method")
                    val inputStream: InputStream = urlConnection.inputStream

                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    val baos = ByteArrayOutputStream()
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        baos.write(buffer, 0, bytesRead)
                    }
                    val data: ByteArray = baos.toByteArray()
                    Log.d("RECORDER_RESP", data.toString(Charsets.US_ASCII))
                    val res = JSONObject(data.toString(Charsets.US_ASCII)).toMap()
                    Log.d("RECORDER", res["t"].toString())
                    res.forEach { (key, value) -> run {
                        if (!result.containsKey(key)){
                            Log.d("RECORDER", "does not contain ${value.toString()}")
                            result[key] = value as Double

                        }
                        else{

                            Log.d("RECORDER", "contains ${value.toString()}")
                            result[key] = result[key]?.let { max(it, value as Double) }!!
                        }
                    } }
                    //out = BufferedOutputStream(urlConnection.outputStream)
                    //Log.d("RECORDER", read.toString())
                    //out.write(buf, 0, read)
                    //out.flush()
                    //out.close()
                    //Log.d("RECORDER", buf)
                //}
                /*catch (e: Exception){
                    e.message?.let { Log.d("RECORDER_E", it) }
                }*/


                /*val writer = BufferedWriter(OutputStreamWriter(out))
                writer.write(buf.take(read))
                writer.flush()
                writer.close()
                out.close()
                urlConnection.connect();*/
            }
            ar.release()
            //birds = result
            Log.d("RECORDER", result.toString())
            //temp = result
            birds.postValue(result)
        }
    }
    private fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith { iter ->
        when (val value = this[iter])
        {
            is JSONArray ->
            {
                val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
                JSONObject(map).toMap().values.toList()
            }
            is JSONObject -> value.toMap()
            JSONObject.NULL -> null
            else            -> value
        }
    }
}
