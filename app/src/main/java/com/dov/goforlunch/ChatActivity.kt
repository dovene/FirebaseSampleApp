package com.dov.goforlunch

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dov.goforlunch.api.ChatHelper
import com.dov.goforlunch.api.UserHelper
import com.dov.goforlunch.model.Message
import com.dov.goforlunch.model.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions

import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_mentor_chat.*

class ChatActivity : BaseActivity(), ChatAdapter.Listener {

    // FOR DESIGN

    private lateinit var recyclerView: RecyclerView
    @BindView(R.id.empty_chat_list)
    internal var textViewRecyclerViewEmpty: TextView? = null
    @BindView(R.id.activity_mentor_chat_message_edit_text)
    internal var editTextMessage: AppCompatEditText? = null
    @BindView(R.id.activity_mentor_chat_image_chosen_preview)
    internal var imageViewPreview: AppCompatImageView? = null

    // FOR DATA
    private var mentorChatAdapter: ChatAdapter? = null
    private var modelCurrentUser: User? = null
    private var currentChatName: String? = null
    private var uriImageSelected: Uri? = null


    // STATIC DATA FOR PICTURE
    private val PERMS = Manifest.permission.READ_EXTERNAL_STORAGE
    private val RC_IMAGE_PERMS = 100
    private val RC_CHOOSE_PHOTO = 200



    private lateinit var button: Button

    companion object {
        val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentor_chat)
        setViewItems()
        configureRecyclerView()
    }

    private fun setViewItems() {
        activity_mentor_chat_send_button.setOnClickListener { onClickSendMessage() }

        // 5 - Get additional data from Firestore
        UserHelper.getUser(currentUser?.getUid()!!)
            .addOnSuccessListener { documentSnapshot ->
                modelCurrentUser = documentSnapshot.toObject<User>(User::class.java!!)
                name.setText(modelCurrentUser?.username)


                Glide.with(this)
                    .load(modelCurrentUser?.urlPicture)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageV)


            }.addOnFailureListener(onFailureListener())


    }


    fun onClickSendMessage() {
        if (!TextUtils.isEmpty(activity_mentor_chat_message_edit_text?.text.toString()) && modelCurrentUser != null) {
            // Check if the ImageView is set
            if (this.imageViewPreview?.getDrawable() == null) {
                // SEND A TEXT MESSAGE
                ChatHelper.createMessageForChat(
                    activity_mentor_chat_message_edit_text?.text.toString(),

                    modelCurrentUser!!
                ).addOnFailureListener(this.onFailureListener())
                this.editTextMessage?.setText("")
            } else {
                // SEND A IMAGE + TEXT IMAGE
               // this.uploadPhotoInFirebaseAndSendMessage(editTextMessage.getText().toString())
               // this.editTextMessage.setText("")
               // this.imageViewPreview.setImageDrawable(null)
            }
        }
    }

    // --------------------
    // UI
    // --------------------

    private fun configureRecyclerView() {
        recyclerView = findViewById(R.id.activity_mentor_chat_recycler_view)
        //Configure Adapter & RecyclerView
        this.mentorChatAdapter = ChatAdapter(
            generateOptionsForAdapter(ChatHelper.getAllMessage()),
            Glide.with(this),
            this,
            currentUser?.uid!!
        )
        mentorChatAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                recyclerView?.smoothScrollToPosition(mentorChatAdapter?.getItemCount()!!) // Scroll to bottom on new messages
            }
        })
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = this.mentorChatAdapter
    }

    private fun generateOptionsForAdapter(query: Query): FirestoreRecyclerOptions<Message> {
        return FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(query, Message::class.java!!)
            .setLifecycleOwner(this)
            .build()
    }



    // --------------------
    // CALLBACK
    // --------------------

    override fun onDataChanged() {
        empty_chat_list?.setVisibility(if (mentorChatAdapter?.getItemCount() === 0) View.VISIBLE else View.GONE)
    }


    override fun onStart() {
        super.onStart()
        mentorChatAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        mentorChatAdapter?.stopListening()
    }
}