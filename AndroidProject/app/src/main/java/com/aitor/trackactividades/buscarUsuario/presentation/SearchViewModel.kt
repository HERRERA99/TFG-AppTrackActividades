package com.aitor.trackactividades.buscarUsuario.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aitor.trackactividades.buscarUsuario.domain.GetUserSearchList
import com.aitor.trackactividades.buscarUsuario.presentation.model.UserSearchModel
import com.aitor.trackactividades.core.userPreferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getUserSearchList: GetUserSearchList,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val users: Flow<PagingData<UserSearchModel>> = _searchQuery
        .debounce(300)
        .filter { it.length > 1 }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            getUserSearchList.execute(query)
        }
        .cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}