package com.example.maplink.data.repository

import android.util.Log
import com.example.maplink.data.repository.model.Friendship
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class FriendsRepository {

    companion object {
        private const val TAG = "FriendsRepository"
    }

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val userListeners = mutableListOf<ListenerRegistration>()

    fun observeFriends(
        onFriendsChanged: (List<Friend>) -> Unit
    ) {

        val currentUid = auth.currentUser?.uid

        if (currentUid == null) {
            Log.e(TAG, "Current user is null")
            onFriendsChanged(emptyList())
            return
        }

        db.collection("friendships")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e(TAG, "Friendship listener failed", error)
                    return@addSnapshotListener
                }

                userListeners.forEach { it.remove() }
                userListeners.clear()

                if (snapshot == null) {
                    Log.d(TAG, "Friendship snapshot is null")
                    onFriendsChanged(emptyList())
                    return@addSnapshotListener
                }

                val friendIds = mutableSetOf<String>()

                snapshot.documents.forEach { doc ->

                    val friendship =
                        doc.toObject(Friendship::class.java) ?: return@forEach

                    when (currentUid) {
                        friendship.user1 -> friendIds.add(friendship.user2)
                        friendship.user2 -> friendIds.add(friendship.user1)
                    }
                }

                Log.d(TAG, "Friend IDs = $friendIds")

                if (friendIds.isEmpty()) {
                    Log.d(TAG, "No friends found")
                    onFriendsChanged(emptyList())
                    return@addSnapshotListener
                }

                val friendsMap = mutableMapOf<String, Friend>()

                friendIds.forEach { friendUid ->

                    val registration = db.collection("users")
                        .document(friendUid)
                        .addSnapshotListener { userDoc, userError ->

                            if (userError != null) {
                                Log.e(TAG, "User listener failed for $friendUid", userError)
                                return@addSnapshotListener
                            }

                            if (userDoc == null || !userDoc.exists()) {
                                Log.d(TAG, "User document missing: $friendUid")
                                friendsMap.remove(friendUid)
                                onFriendsChanged(friendsMap.values.toList())
                                return@addSnapshotListener
                            }

                            val friend = Friend(
                                uid = friendUid,
                                name = userDoc.getString("name") ?: "",
                                username = userDoc.getString("username") ?: "",
                                online = userDoc.getBoolean("online") ?: false,
                                latitude = userDoc.getDouble("latitude") ?: 0.0,
                                longitude = userDoc.getDouble("longitude") ?: 0.0
                            )

                            Log.d(
                                TAG,
                                "Friend Loaded -> " +
                                        "uid=${friend.uid}, " +
                                        "name=${friend.name}, " +
                                        "lat=${friend.latitude}, " +
                                        "lng=${friend.longitude}, " +
                                        "online=${friend.online}"
                            )

                            friendsMap[friendUid] = friend

                            Log.d(
                                TAG,
                                "Emitting ${friendsMap.size} friends: $friendsMap"
                            )

                            onFriendsChanged(friendsMap.values.toList())
                        }

                    userListeners.add(registration)
                }
            }
    }
}