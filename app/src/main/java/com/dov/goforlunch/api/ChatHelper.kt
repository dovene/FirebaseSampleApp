package com.dov.goforlunch.api

import com.dov.goforlunch.model.Message
import com.dov.goforlunch.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import java.util.*

class ChatHelper {

    companion object {
        private val COLLECTION_NAME = "chats"

        val chatsCollection: CollectionReference
            get() = FirebaseFirestore.getInstance().collection(COLLECTION_NAME)

        // --- GET ---

        fun getAllMessage(): Query {
            return chatsCollection
                .orderBy("dateCreated").limit(50)
        }

        // --- CREATE ---

        fun createMessageForChat(
            textMessage: String,
            userSender: User
        ): Task<DocumentReference> {
            val message = Message(textMessage, null, userSender, Date())
            return chatsCollection
                .add(message)
        }

        fun createMessageWithImageForChat(
            urlImage: String,
            textMessage: String,
            chat: String,
            userSender: User
        ): Task<DocumentReference> {
            val message = Message(textMessage, urlImage, userSender)
            return chatsCollection.document(chat).collection(COLLECTION_NAME)
                .add(message)
        }

    }

}