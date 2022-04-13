package com.coolco.malure

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


class GuideViewModel : ViewModel() {
    var lat = 0.0
    var lon = 0.0

    fun getSearchResults(lat: Double, lon: Double): LiveData<MutableList<PlaceCard>> {
        this.lat = lat
        this.lon = lon
        Log.d("TEXTTEXTTEXT", "reached birds")
        return bird
    }

    val bird: MutableLiveData<MutableList<PlaceCard>> by lazy {
        MutableLiveData<MutableList<PlaceCard>>().also{
            Log.d("TEXTTEXTTEXT", "in also")
            loadBird()
            Log.d("TEXTTEXTTEXT", "after let")
        }
    }

    private fun loadBird() {

        viewModelScope.launch(Dispatchers.IO){

            Log.d("RESULT", "reached bird info getter")
            var resObject : MutableList<PlaceCard> = ArrayList<PlaceCard>()
                val url = URL("http://$HOST:8080/get_place_near_me/$lat, $lon")
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
                val res = JSONArray(data.toString(Charsets.UTF_8))
                //resObject.add(BirdResult(res["name"] as String, res["latin"] as String, res["_id"] as String,
                //    "${id!!.getDouble(key)}%", "http://$HOST:8080/get_bird_pic/${res["_id"]}"))
                for (i in 0 until res.length()) {
                    val tmpObject = res.get(i) as JSONObject
                    val t = tmpObject.toMap()
                    resObject.add(PlaceCard(t["name"] as String, t["dist"] as Double, t["_id"] as String))
                }
                //val resObject =

            bird.postValue(resObject)
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
