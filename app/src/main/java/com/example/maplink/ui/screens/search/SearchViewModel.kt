package com.example.maplink.ui.search

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.maplink.data.model.User
import com.example.maplink.data.repository.UserRepository

class SearchViewModel : ViewModel() {

    private val repository = UserRepository()

    val users = mutableStateListOf<User>()

    fun search(query: String) {

        repository.searchUsers(
            query = query,

            onResult = {

                users.clear()
                users.addAll(it)

            },

            onFailure = {

            }
        )
    }
}