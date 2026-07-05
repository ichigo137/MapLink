package com.example.maplink.data.repository
import com.example.maplink.data.repository.model.Friendship
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.atomic.AtomicInteger
class FriendsRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun observeFriends(
        onFriendsChanged: (List<Friend>) -> Unit
    ) {

        val currentUid = auth.currentUser?.uid ?: return

        db.collection("friendships")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot == null) return@addSnapshotListener

                val friendshipDocs = snapshot.documents
                val friends = mutableListOf<Friend>()

                if (friendshipDocs.isEmpty()) {
                    onFriendsChanged(emptyList())
                    return@addSnapshotListener
                }

                val pending = AtomicInteger(friendshipDocs.size)

                for (doc in friendshipDocs) {

                    val friendship =
                        doc.toObject(Friendship::class.java) ?: run {
                            if (pending.decrementAndGet() == 0) {
                                onFriendsChanged(friends)
                            }
                            continue
                        }

                    val friendUid = when (currentUid) {
                        friendship.user1 -> friendship.user2
                        friendship.user2 -> friendship.user1
                        else -> null
                    }

                    if (friendUid == null) {
                        if (pending.decrementAndGet() == 0) {
                            onFriendsChanged(friends)
                        }
                        continue
                    }

                    db.collection("users")
                        .document(friendUid)
                        .get()
                        .addOnSuccessListener { userDoc ->

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

                            if (pending.decrementAndGet() == 0) {
                                onFriendsChanged(friends)
                            }
                        }
                        .addOnFailureListener {
                            if (pending.decrementAndGet() == 0) {
                                onFriendsChanged(friends)
                            }
                        }
                }
                        }
                }

}