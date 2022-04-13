package com.coolco.malure


data class BirdPageData(val Title: String, val Distance: Double, val ID: String, val type: String, val compText: String, val popular: MutableList<BirdResult>, val rare: MutableList<BirdResult>, val season: MutableList<BirdResult>)
