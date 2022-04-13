package com.coolco.malure

import android.media.AudioFormat
import android.media.AudioRecord
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.serialization.*
import java.util.*

@Serializable
data class PlaceCard(val Title: String, val Distance: Double, val ID: String)
