package com.coolco.malure

import kotlinx.serialization.Serializable


@Serializable
data class SearchResult(val res: Map<String, String>){

}