package com.coolco.malure

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

class LibraryDao {
    @Dao
    interface BirdDao {

        @Query("SELECT * FROM library")
        fun getAlphabetizedWords(): Flow<List<Bird>>

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insert(bird: Bird)

    }

}