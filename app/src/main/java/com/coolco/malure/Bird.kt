package com.coolco.malure

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library")
class Bird(@PrimaryKey @ColumnInfo(name = "id") val id: String)
