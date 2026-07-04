package com.example.maplink.data.repository

import com.example.maplink.data.model.FriendRequest
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.maplink.data.model.FriendRequestItem

class FriendRequestRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun sendFriendRequest(
        senderUid: String,
        receiverUid: String
    ): Result<String> {

        return try {

            if (senderUid == receiverUid) {
                return Result.failure(Exception("You can't add yourself"))
            }

            val requests = db.collection("friend_requests")

            // A -> B
            val existing = requests
                .whereEqualTo("senderUid", senderUid)
                .whereEqualTo("receiverUid", receiverUid)
                .get()
                .await()

            if (!existing.isEmpty) {
                return Result.failure(Exception("Request already sent"))
            }

            // B -> A
            val reverse = requests
                .whereEqualTo("senderUid", receiverUid)
                .whereEqualTo("receiverUid", senderUid)
                .get()
                .await()

            if (!reverse.isEmpty) {
                return Result.failure(Exception("This user already sent you a request"))
            }

            val doc = requests.document()

            val request = FriendRequest(
                id = doc.id,
                senderUid = senderUid,
                receiverUid = receiverUid,
                status = "pending",
                createdAt = Timestamp.now()
            )

            doc.set(request).await()

            Result.success("Friend request sent")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getIncomingRequests(
        currentUid: String
    ): List<FriendRequestItem> {

        android.util.Log.d("MapLink", "Current UID = $currentUid")

        val result = mutableListOf<FriendRequestItem>()

        val requests = db.collection("friend_requests")
            .whereEqualTo("receiverUid", currentUid)
            .whereEqualTo("status", "pending")
            .get()
            .await()

        android.util.Log.d("MapLink", "Requests found = ${requests.documents.size}")

        for (doc in requests.documents) {

            val senderUid = doc.getString("senderUid") ?: continue

            android.util.Log.d("MapLink", "Sender UID = $senderUid")

            val sender = db.collection("users")
                .document(senderUid)
                .get()
                .await()

            android.util.Log.d(
                "MapLink",
                "Sender name = ${sender.getString("name")}"
            )

            result.add(
                FriendRequestItem(
                    requestId = doc.id,
                    senderUid = senderUid,
                    senderName = sender.getString("name") ?: "",
                    senderUsername = sender.getString("username") ?: ""
                )
            )
        }

        android.util.Log.d("MapLink", "Final list size = ${result.size}")

        return result
    }
}