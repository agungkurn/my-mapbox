package com.example.mymapbox

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.*
import com.example.mymapbox.core.PreferenceUtils
import com.example.mymapbox.core.model.DataState
import com.example.mymapbox.core.model.FeaturesItem
import com.example.mymapbox.core.model.SearchResponse
import com.example.mymapbox.core.model.SearchingState
import com.example.mymapbox.search.SearchRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.ConnectException

class MainViewModel(private val repository: SearchRepository, application: Application) :
	AndroidViewModel(application) {
	companion object {
		fun getViewModelFactory(repository: SearchRepository, application: Application) =
			object : ViewModelProvider.Factory {
				override fun <T : ViewModel?> create(modelClass: Class<T>): T {
					if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
						return MainViewModel(repository, application) as T
					}
					throw ClassCastException("invalid viewmodel")
				}
			}
	}

	val context = getApplication<Application>().applicationContext

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

	val foundPlacePosition = MutableLiveData<SearchingState>(SearchingState.NotUsed)

	private val sharedPreferences =
		context.getSharedPreferences(PreferenceUtils.PREF_NAME, Context.MODE_PRIVATE)

	fun setDarkModeEnabled(enabled: Boolean) {
		sharedPreferences.edit {
			putBoolean(PreferenceUtils.DARK_MAP, enabled)
		}
	}

	fun setTrafficFlowsEnabled(enabled: Boolean) {
		sharedPreferences.edit {
			putBoolean(PreferenceUtils.TRAFFIC_MAP, enabled)
		}
	}

	val isDarkModeEnabled = sharedPreferences.getBoolean(PreferenceUtils.DARK_MAP, false)
	val isTrafficFlowsEnabled = sharedPreferences.getBoolean(PreferenceUtils.TRAFFIC_MAP, true)
}