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


class PlaceViewModel : ViewModel() {
    var id = ""

    fun getSearchResults(id: String): LiveData<BirdPageData> {
        this.id = id
        Log.d("TEXTTEXTTEXT", "reached birds")
        return bird
    }

    val bird: MutableLiveData<BirdPageData> by lazy {
        MutableLiveData<BirdPageData>().also{
            Log.d("TEXTTEXTTEXT", "in also")
            loadBird()
            Log.d("TEXTTEXTTEXT", "after let")
        }
    }

    private fun loadBird() {

        viewModelScope.launch(Dispatchers.IO){

            Log.d("RESULT", "reached bird info getter")
            var resObject : BirdPageData
            val url = URL("http://$HOST:8080/get_place/$id")
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
            //resObject.add(BirdResult(res["name"] as String, res["latin"] as String, res["_id"] as String,
            //    "${id!!.getDouble(key)}%", "http://$HOST:8080/get_bird_pic/${res["_id"]}"))
            //val resObject =
            val popular: MutableList<BirdResult> = mutableListOf<BirdResult>()
            val rare: MutableList<BirdResult> = mutableListOf<BirdResult>()
            val season: MutableList<BirdResult> = mutableListOf<BirdResult>()
            var list = res["popular"] as List<String>
            Log.d("PLACE", res["popular"].toString())
            for (p in list){

                var resObject : MutableList<PlaceCard> = ArrayList<PlaceCard>()
                val url = URL("http://$HOST:8080/get_bird/${p}")
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
                popular.add(BirdResult(res["name"] as String, res["latin"] as String, res["_id"] as String, "", "http://$HOST:8080/get_bird_pic/${res["_id"]}"), )
            }
            list = res["rare"] as List<String>
            for (p in list){

                var resObject : MutableList<PlaceCard> = ArrayList<PlaceCard>()
                val url = URL("http://$HOST:8080/get_bird/$p")
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
                rare.add(BirdResult(res["name"] as String, res["latin"] as String, res["_id"] as String, "", "http://$HOST:8080/get_bird_pic/${res["_id"]}"), )
            }
            list = res["notnow"] as List<String>
            for (p in list){

                var resObject : MutableList<PlaceCard> = ArrayList<PlaceCard>()
                val url = URL("http://$HOST:8080/get_bird/$p")
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
                season.add(BirdResult(res["name"] as String, res["latin"] as String, res["_id"] as String, "", "http://$HOST:8080/get_bird_pic/${res["_id"]}"), )
            }
            resObject = BirdPageData(res["name"] as String,
                0.0,
                res["_id"] as String,
                res["type"] as String,
                res["desc"] as String,
                popular,
                rare,
                season)
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
