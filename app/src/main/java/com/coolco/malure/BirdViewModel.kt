package com.coolco.malure

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class BirdViewModel(private val repository: BirdRepository) : ViewModel() {

    val allWords: LiveData<List<Bird>> = repository.allWords.asLiveData()
    fun insert(bird: Bird) = viewModelScope.launch {
        repository.insert(bird)
    }
}

class BirdViewModelFactory(private val repository: BirdRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BirdViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BirdViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
