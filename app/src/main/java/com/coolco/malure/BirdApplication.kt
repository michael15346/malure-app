package com.coolco.malure

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class BirdApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { BirdRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { BirdRepository(database.birdDao()) }
}
