package com.dov.goforlunch

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.dov.goforlunch.model.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

 var rootView: RelativeLayout? = null

    //PROFILE CONTAINER

    //PROFILE CONTAINER
     var profileContainer: LinearLayout? = null

    internal var imageViewProfile: AppCompatImageView? = null

    //MESSAGE CONTAINER
    internal var messageContainer: RelativeLayout? = null
    //IMAGE SENDED CONTAINER
    internal var cardViewImageSent: CardView? = null


     var imageViewSent: AppCompatImageView? = null



     var textMessageContainer: LinearLayout? = null

    var textViewMessage: AppCompatTextView? = null

    var textViewDate: AppCompatTextView? = null


    //FOR DATA
    private val colorCurrentUser: Int
    private val colorRemoteUser: Int

    init {

        ButterKnife.bind(this, itemView)
        colorCurrentUser = ContextCompat.getColor(itemView.context, R.color.colorAccent)
        colorRemoteUser = ContextCompat.getColor(itemView.context, R.color.colorPrimary)

        textViewMessage = itemView.findViewById(R.id.activity_mentor_chat_item_message_container_text_message_container_text_view)

        textViewDate = itemView.findViewById(R.id.activity_mentor_chat_item_message_container_text_view_date)

        textMessageContainer = itemView.findViewById(R.id.activity_mentor_chat_item_message_container_text_message_container)

        imageViewSent = itemView.findViewById(R.id.activity_mentor_chat_item_message_container_image_sent_cardview_image)


        profileContainer = itemView.findViewById(R.id.profile_container)

        imageViewProfile = itemView.findViewById(R.id.profile_image)

        messageContainer = itemView.findViewById(R.id.activity_mentor_chat_item_message_container)

        cardViewImageSent = itemView.findViewById(R.id.activity_mentor_chat_item_message_container_image_sent_cardview)

        rootView = itemView.findViewById(R.id.activity_mentor_chat_item_root_view)



    }

    fun updateWithMessage(message: Message, currentUserId: String, glide: RequestManager) {

        // Check if current user is the sender
        val isCurrentUser = message.userSender?.uid.equals(currentUserId)

        // Update message TextView
        this.textViewMessage?.setText(message.message)
        this.textViewMessage?.textAlignment =
            if (isCurrentUser) View.TEXT_ALIGNMENT_TEXT_END else View.TEXT_ALIGNMENT_TEXT_START



        // Update date TextView
        if (message.dateCreated != null) this.textViewDate!!.text =
            this.convertDateToHour(message.dateCreated!!)

        // Update isMentor ImageView


        // Update profile picture ImageView
        if (message.userSender?.urlPicture != null)
            glide.load(message.userSender?.urlPicture)
                .apply(RequestOptions.circleCropTransform())
                .into(imageViewProfile!!)

        // Update image sent ImageView
        if (message.urlImage != null) {
            glide.load(message.urlImage)
                .into(imageViewSent!!)
            this.imageViewSent!!.visibility = View.VISIBLE
        } else {
            this.imageViewSent!!.visibility = View.GONE
        }

        //Update Message Bubble Color Background
        (textMessageContainer!!.background as GradientDrawable).setColor(if (isCurrentUser) colorCurrentUser else colorRemoteUser)

        // Update all views alignment depending is current user or not
        this.updateDesignDependingUser(isCurrentUser)
    }

    private fun updateDesignDependingUser(isSender: Boolean?) {

        // PROFILE CONTAINER
        val paramsLayoutHeader = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        paramsLayoutHeader.addRule(if (isSender!!) RelativeLayout.ALIGN_PARENT_RIGHT else RelativeLayout.ALIGN_PARENT_LEFT)
        this.profileContainer!!.layoutParams = paramsLayoutHeader

        // MESSAGE CONTAINER
        val paramsLayoutContent = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )


        this.messageContainer!!.layoutParams = paramsLayoutContent

        // CARDVIEW IMAGE SEND
        val paramsImageView = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        paramsImageView.addRule(
            if (isSender) RelativeLayout.ALIGN_LEFT else RelativeLayout.ALIGN_RIGHT,
            R.id.activity_mentor_chat_item_message_container_text_message_container
        )
        this.cardViewImageSent!!.setLayoutParams(paramsImageView)

        this.rootView!!.requestLayout()
    }

    // ---

    private fun convertDateToHour(date: Date): String {
        val dfTime = SimpleDateFormat("HH:mm")
        return dfTime.format(date)
    }
}