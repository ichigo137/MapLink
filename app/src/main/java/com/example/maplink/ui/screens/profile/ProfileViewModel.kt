package com.example.maplink.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.maplink.data.repository.LocationSharingRepository
import com.example.maplink.service.LocationServiceManager
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = LocationSharingRepository()

    private val _locationSharingEnabled =
        MutableStateFlow(true)

    val locationSharingEnabled: StateFlow<Boolean> =
        _locationSharingEnabled

    private val _isUpdating =
        MutableStateFlow(false)

    val isUpdating: StateFlow<Boolean> =
        _isUpdating

    private var listenerRegistration: ListenerRegistration? = null

    init {
        observeLocationSharing()
    }

    private fun observeLocationSharing() {

        listenerRegistration =
            repository.observeLocationSharing { enabled ->

                _locationSharingEnabled.value = enabled
            }
    }

    fun setLocationSharingEnabled(enabled: Boolean) {

        if (_isUpdating.value) {
            return
        }

        if (enabled == _locationSharingEnabled.value) {
            return
        }

        _isUpdating.value = true

        if (enabled) {
            enableLocationSharing()
        } else {
            disableLocationSharing()
        }
    }

    private fun enableLocationSharing() {

        repository.setLocationSharingEnabled(true) { success ->

            if (success) {

                LocationServiceManager.startIfAllowed(
                    getApplication()
                )
            }

            _isUpdating.value = false
        }
    }

    private fun disableLocationSharing() {

        LocationServiceManager.stop(
            getApplication()
        )

        repository.setOffline {

            repository.setLocationSharingEnabled(false) {
                _isUpdating.value = false
            }
        }
    }

    override fun onCleared() {

        listenerRegistration?.remove()
        listenerRegistration = null

        super.onCleared()
    }
}