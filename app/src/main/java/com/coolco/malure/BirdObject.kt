package com.coolco.malure

data class BirdObject(
    val id: String,
    val name: String,
    val latin: String,
    val whenToFind: String,
    val find: List<Map<String, String>>,
    val feeder: String,
    val nesting: String,
    val social: String,
    val area: String,
    val migration: String,
    val yearly: List<Map<String, String>>
        ){
}
