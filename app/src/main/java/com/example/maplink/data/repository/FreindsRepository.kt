package com.example.maplink.data.repository
import com.example.maplink.data.repository.model.Friendship
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FriendsRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getFriends(): List<Friend> {

        val currentUid = auth.currentUser?.uid ?: return emptyList()

        val friendships = db.collection("friendships")
            .get()
            .await()

        val friends = mutableListOf<Friend>()

        for (doc in friendships.documents) {

            val friendship = doc.toObject(Friendship::class.java) ?: continue

            val friendUid = when (currentUid) {
                friendship.user1 -> friendship.user2
                friendship.user2 -> friendship.user1
                else -> null
            } ?: continue

            val userDoc = db.collection("users")
                .document(friendUid)
                .get()
                .await()

            friends.add(
                Friend(
                    uid = friendUid,
                    name = userDoc.getString("name") ?: "",
                    username = userDoc.getString("username") ?: "",
                    online = userDoc.getBoolean("online") ?: false,
                    latitude = userDoc.getDouble("latitude") ?: 0.0,
                    longitude = userDoc.getDouble("longitude") ?: 0.0
                )
            )
        }

        return friends
    }
}