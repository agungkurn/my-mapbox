package com.example.mymapbox

import androidx.lifecycle.*
import com.example.mymapbox.core.model.DataState
import com.example.mymapbox.core.model.FeaturesItem
import com.example.mymapbox.core.model.SearchResponse
import com.example.mymapbox.search.SearchRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.ConnectException

class MainViewModel(private val repository: SearchRepository) : ViewModel() {
	companion object {
		fun getViewModelFactory(repository: SearchRepository) = object : ViewModelProvider.Factory {
			override fun <T : ViewModel?> create(modelClass: Class<T>): T {
				if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
					return MainViewModel(repository) as T
				}
				throw ClassCastException("invalid viewmodel")
			}
		}
	}

	private var searchRequest: Deferred<SearchResponse>? = null
	private val mSearchResult = MutableLiveData<DataState<List<FeaturesItem>>>()
	val searchResult: LiveData<DataState<List<FeaturesItem>>> = mSearchResult

	fun searchLocation(keyword: String) {
		mSearchResult.value = DataState.Loading

		viewModelScope.launch(Dispatchers.IO) {
			try {
				searchRequest?.cancel()
				searchRequest = async { repository.searchLocation(keyword) }
				searchRequest?.await()?.let {
					mSearchResult.postValue(DataState.Success(it.features))
				}
			} catch (e: ConnectException) {
				mSearchResult.postValue(DataState.Failed(errorMessage = "Connection error"))
			} catch (e: Exception) {
				mSearchResult.postValue(DataState.Failed(exception = e))
			}
		}
	}

	val foundPlacePosition = MutableLiveData<FeaturesItem?>(null)
}