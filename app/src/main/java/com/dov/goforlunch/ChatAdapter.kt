package com.dov.goforlunch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import com.bumptech.glide.RequestManager
import com.dov.goforlunch.model.Message
import com.firebase.ui.firestore.FirestoreRecyclerAdapter

import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ChatAdapter(
    @NonNull options: FirestoreRecyclerOptions<Message>, //FOR DATA
    private val glide: RequestManager, //FOR COMMUNICATION
    private val callback: Listener, private val idCurrentUser: String
) : FirestoreRecyclerAdapter<Message, MessageViewHolder>(options) {

    interface Listener {
        fun onDataChanged()
    }

    override fun onBindViewHolder(@NonNull holder: MessageViewHolder, position: Int, @NonNull model: Message) {
        holder.updateWithMessage(model, this.idCurrentUser, this.glide)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_chat_item, parent, false)
        )
    }



     override fun onDataChanged() {
        super.onDataChanged()
        this.callback.onDataChanged()
    }
}
