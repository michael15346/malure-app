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


class BirdModel : ViewModel() {
    var id: String = ""

    fun getSearchResults(id: String): LiveData<BirdObject> {
        this.id = id
        Log.d("TEXTTEXTTEXT", "reached birds")
        return bird
    }

    val bird: MutableLiveData<BirdObject> by lazy {
        MutableLiveData<BirdObject>().also{
            Log.d("TEXTTEXTTEXT", "in also")
            loadBird()
            Log.d("TEXTTEXTTEXT", "after let")
        }
    }

    private fun loadBird() {

        viewModelScope.launch(Dispatchers.IO){
            Log.d("RESULT", "reached bird info getter")
            //var resObject : MutableList<BirdResult> = ArrayList<BirdResult>()


                val url = URL("http://$HOST:8080/get_bird/$id")
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
                Log.d("RESULT", res["yearly"].toString())
                //resObject.add(BirdResult(res["name"] as String, res["latin"] as String, res["_id"] as String, 0, "http://$HOST:8080/get_bird_pic/${res["_id"]}"))
                Log.d("RESULT" , res["where_to_find"].toString())

                val bo: BirdObject = BirdObject(res["_id"] as String,
                    res["name"] as String,
                    res["latin"] as String,
                    res["findhere"] as String,
                    res["where_to_find"] as List<Map<String, String>>,
                    res["feeder"] as String,
                    res["nesting"] as String,
                    res["social"] as String,
                    res["area"] as String,
                    res["migration"] as String,
                    res["yearly"] as List<Map<String, String>>)
                //val resObject =

            bird.postValue(bo)
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
