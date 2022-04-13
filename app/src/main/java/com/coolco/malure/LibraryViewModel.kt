package com.coolco.malure

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class LibraryViewModel : ViewModel() {
    var ids: List<Bird>? = null

    fun getSearchResults(id: List<Bird>): LiveData<MutableList<BirdResult>> {
        this.ids = id
        Log.d("TEXTTEXTTEXT", "reached birds")
        return bird
    }

    val bird: MutableLiveData<MutableList<BirdResult>> by lazy {
        MutableLiveData<MutableList<BirdResult>>().also{
            Log.d("TEXTTEXTTEXT", "in also")
            loadBird()
            Log.d("TEXTTEXTTEXT", "after let")
        }
    }

    private fun loadBird() {

        viewModelScope.launch(Dispatchers.IO){
            Log.d("RESULT", "reached bird info getter")
            val resObject : MutableList<BirdResult> = ArrayList<BirdResult>()
            for (key in ids!!){
                val url = URL("http://$HOST:8080/get_bird/${key.id}")
                val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                urlConnection.doInput = true
                //urlConnection.doOutput = true
                urlConnection.requestMethod = "GET"
                urlConnection.setRequestProperty("Host", HOST)
                urlConnection.setRequestProperty("Accept", "*/*")
                urlConnection.setRequestProperty("Connection", "keep-alive")
                urlConnection.connect()

                val inputStream: InputStream = urlConnection.inputStream

                val buffer = ByteArray(65535)
                var bytesRead: Int
                val baos = ByteArrayOutputStream()
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    baos.write(buffer, 0, bytesRead)
                }
                val data: ByteArray = baos.toByteArray()
                Log.d("RESULT_RESP", data.toString(Charsets.UTF_8))
                val res = JSONObject(data.toString(Charsets.UTF_8)).toMap()
                resObject.add(BirdResult(res["name"] as String, res["latin"] as String, res["_id"] as String,
                    "100%", "http://$HOST:8080/get_bird_pic/${res["_id"]}"))

                //val resObject =
            }

            bird.postValue(resObject)
            ids = null
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
