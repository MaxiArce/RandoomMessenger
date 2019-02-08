package com.maxiarce.radoommessenger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.maxiarce.radoommessenger.chatviews.ChatItemFrom
import com.maxiarce.radoommessenger.chatviews.ChatItemTo
import com.maxiarce.radoommessenger.models.ChatMessage
import com.maxiarce.radoommessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_messages_row.view.*


class ChatActivity : AppCompatActivity() {


    val adapter = GroupAdapter<ViewHolder>()

    lateinit var toUser: User

    // shake animation for buttons
    lateinit var shakeAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //init var
        shakeAnimation = AnimationUtils.loadAnimation(this,R.anim.shake_animation)

        //get toUser from prev activity
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        //set recyclerview adapter
        chat_recyclerView_chatlog.adapter = adapter


        // set toolBar
        val toolbar: Toolbar = findViewById(R.id.toolbar_chat_activity)
        setSupportActionBar(toolbar)
        val ActionBar = supportActionBar!!
        ActionBar.setTitle(toUser.username)

        //set profile image on toolbar
        val profilePicture = imageView_profile_chat_toolbar
        Picasso.get().load(toUser?.profileImageUrl).into(profilePicture)

        // set listener for keyboard send button
        val editText = entermessage_edittext_chatlog
        editText.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId){
                EditorInfo.IME_ACTION_SEND -> {
                    sendMesssage(v)
                    true
                }else -> false
            }
        }

//        val uri = toUser.profileImageUrl
//        Picasso.get().load(uri).into(??)

        messagesListener()


    }

    private fun messagesListener(){

        val toId = toUser.uid
        val fromId = FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/messages/$fromId/$toId")

        reference.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if(chatMessage != null){

                    if(chatMessage.fromId == fromId){
                        adapter.add(ChatItemFrom(chatMessage.text))
                    }else{
                        adapter.add(ChatItemTo(chatMessage.text))
                    }
                    chat_recyclerView_chatlog.smoothScrollToPosition(adapter.itemCount - 1)
                }

            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }



            override fun onChildRemoved(p0: DataSnapshot) {
            }


        })
    }

    fun sendMesssage(v: View){

        val textMessage = entermessage_edittext_chatlog.text.toString()

        //obtain uid of current user
        val fromId = FirebaseAuth.getInstance().uid

        //get the uid of the user that message must be send
        val toId = toUser.uid

        val reference = FirebaseDatabase.getInstance().getReference("/messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/messages/$toId/$fromId").push()

        if(entermessage_edittext_chatlog.text.isNotBlank()){
            val chatMessage = ChatMessage(reference.key!!, textMessage, fromId!!, toId, System.currentTimeMillis())

            //push the message to fromUser path on firebase
            reference.setValue(chatMessage).addOnSuccessListener {
                Log.d("ChatActivity","Saved Sucessfully to fromUser")

                //clear editText and scroll to last message
                entermessage_edittext_chatlog.text.clear()
                chat_recyclerView_chatlog.smoothScrollToPosition(adapter.itemCount - 1)


            }

            //push the message to toUser path on firebase
            toReference.setValue(chatMessage).addOnSuccessListener {
                Log.d("ChatActivity","Saved Sucessfully to toUser")
            }

            //save last message on from and to user
            val lastMessageFromRef =  FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
            lastMessageFromRef.setValue(chatMessage)
            val lastMessageToRef =  FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
            lastMessageToRef.setValue(chatMessage)


        }else{
                // activate shake animation for buttonSendMesage
                send_button_chatlog.startAnimation(shakeAnimation)

        }

    }



}






