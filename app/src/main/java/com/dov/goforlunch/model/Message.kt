package com.dov.goforlunch.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*


data class Message(var message: String? = null,var urlImage: String? = null,var  userSender: User? = null,   var dateCreated: Date? = null) {



}