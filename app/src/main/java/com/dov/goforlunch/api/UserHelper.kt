package com.dov.goforlunch.api

import com.google.android.gms.tasks.Task
import android.provider.SyncStateContract.Helpers.update
import com.dov.goforlunch.model.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference


class  UserHelper {


    companion object {
        private val COLLECTION_NAME = "users"

        val usersCollection: CollectionReference
            get() = FirebaseFirestore.getInstance().collection(COLLECTION_NAME)

        // --- CREATE ---

        fun createUser(uid: String, username: String?, urlPicture: String?): Task<Void> {

            val userToCreate = User(uid, username, urlPicture)
            return usersCollection.document(uid).set(userToCreate)
        }

        // --- GET ---

        fun getUser(uid: String): Task<DocumentSnapshot> {
            return UserHelper.usersCollection.document(uid).get()
        }

        // --- UPDATE ---

        fun updateUsername(username: String, uid: String): Task<Void> {
            return UserHelper.usersCollection.document(uid).update("username", username)
        }

        fun updateIsMentor(uid: String, isMentor: Boolean?): Task<Void> {
            return UserHelper.usersCollection.document(uid).update("isMentor", isMentor)
        }

        fun updateUserPicture(uid: String, urlPicture: String): Task<Void> {
            return UserHelper.usersCollection.document(uid).update("urlPicture", urlPicture)
        }


        fun updateUserPictureAndName(uid: String, urlPicture: String, username: String): Task<Void> {

            val updateMap : java.util.HashMap<String,Any> = HashMap()
            updateMap.put("urlPicture",urlPicture)
            updateMap.put("username",username)
            return usersCollection.document(uid).update(updateMap)
        }


        // --- DELETE ---

        fun deleteUser(uid: String): Task<Void> {
            return UserHelper.usersCollection.document(uid).delete()
        }

    }



}