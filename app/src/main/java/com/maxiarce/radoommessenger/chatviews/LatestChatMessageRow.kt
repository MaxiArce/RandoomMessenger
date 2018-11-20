package com.maxiarce.radoommessenger.chatviews

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.maxiarce.radoommessenger.R
import com.maxiarce.radoommessenger.models.ChatMessage
import com.maxiarce.radoommessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_messages_row.view.*

class LatestChatMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>(){

    var chatPartnerUser: User? = null

    override fun getLayout(): Int {
        return R.layout.latest_messages_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        //set textMessage
        viewHolder.itemView.textview_latest_message.text = chatMessage.text

        //set username
        val ref = FirebaseDatabase.getInstance().getReference("/users/${chatMessage.toId}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.textview_latest_message_username.text = chatPartnerUser?.username

                //set avatar
                val profilePicture = viewHolder.itemView.imageView_latest_message_avatar
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(profilePicture)


            }

        })

    }

}