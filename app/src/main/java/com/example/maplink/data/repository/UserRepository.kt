package com.example.maplink.data.repository

import com.example.maplink.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun searchUsers(
        query: String,
        onResult: (List<User>) -> Unit,
        onFailure: (String) -> Unit
    ) {

        db.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->

                val currentUid = auth.currentUser?.uid

                val users = snapshot.documents.mapNotNull {
                    it.toObject(User::class.java)
                }.filter {

                    it.uid != currentUid &&
                            (
                                    it.username.contains(query, ignoreCase = true) ||
                                            it.name.contains(query, ignoreCase = true)
                                    )
                }

                onResult(users)
            }
            .addOnFailureListener {
                onFailure(it.message ?: "Search failed")
            }
    }
}