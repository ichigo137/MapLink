package com.example.maplink.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class LocationSharingRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun observeLocationSharing(
        onChanged: (Boolean) -> Unit
    ): ListenerRegistration? {

        val uid = auth.currentUser?.uid ?: run {
            onChanged(false)
            return null
        }

        return firestore.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }

                val enabled =
                    snapshot.getBoolean("locationSharingEnabled")
                        ?: true

                onChanged(enabled)
            }
    }

    fun setLocationSharingEnabled(
        enabled: Boolean,
        onComplete: (Boolean) -> Unit
    ) {

        val uid = auth.currentUser?.uid ?: run {
            onComplete(false)
            return
        }

        firestore.collection("users")
            .document(uid)
            .update(
                "locationSharingEnabled",
                enabled
            )
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun setOffline(
        onComplete: () -> Unit = {}
    ) {

        val uid = auth.currentUser?.uid ?: run {
            onComplete()
            return
        }

        firestore.collection("users")
            .document(uid)
            .update("online", false)
            .addOnCompleteListener {
                onComplete()
            }
    }
}