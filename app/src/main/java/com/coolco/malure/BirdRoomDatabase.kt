package com.coolco.malure

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = [Bird::class], version = 1, exportSchema = false)
public abstract class BirdRoomDatabase : RoomDatabase() {

    abstract fun birdDao(): LibraryDao.BirdDao

    companion object {
        @Volatile
        private var INSTANCE: BirdRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BirdRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BirdRoomDatabase::class.java,
                    "library"
                ).addCallback(BirdDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
    private class BirdDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    addBird(database.birdDao())
                }
            }
        }

        suspend fun addBird(birdDao: LibraryDao.BirdDao){

        }
    }

}
