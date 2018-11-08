package com.maxiarce.radoommessenger

import android.nfc.Tag
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.maxiarce.radoommessenger.models.ChatMessage
import com.maxiarce.radoommessenger.models.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_from_row_chatlog.view.*
import kotlinx.android.synthetic.main.chat_to_row_chatlog.view.*



class ChatActivity : AppCompatActivity() {


    val adapter = GroupAdapter<ViewHolder>()

    // shake animation for buttons
    lateinit var shakeAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //init var
        shakeAnimation = AnimationUtils.loadAnimation(this,R.anim.shake_animation)

        //set recyclerview adapter
        chat_recyclerView_chatlog.adapter = adapter


        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user.username

        messagesListener()
    }


    private fun messagesListener(){
        val reference = FirebaseDatabase.getInstance().getReference("/messages")

        reference.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if(chatMessage != null){

                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatItemTo(chatMessage.text))
                    }else{
                        if(chatMessage.toId == intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY).uid){
                            adapter.add(ChatItemFrom(chatMessage.text))
                        }
                    }
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


    fun sendMesssage(view: View){
        // perform message sending to firebase
        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        //obtain uid of current user
        val fromId = FirebaseAuth.getInstance().uid
        //get the uid from previous activity
        val toId = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY).uid

        //check if editText if not empt
        val textMessage = entermessage_edittext_chatlog.text.toString()
        if(entermessage_edittext_chatlog.text.isNotBlank()){
            val chatMessage = ChatMessage(reference.key!!, textMessage, fromId!!, toId, System.currentTimeMillis())

                //push the message to firebase
                reference.setValue(chatMessage).addOnSuccessListener {
                Log.d("ChatActivity","Saved Sucessfully")

                //clear editText
                entermessage_edittext_chatlog.text.clear()

            }

        }
        if (entermessage_edittext_chatlog.text.isBlank()){
                // implement shake animation for buttonSendMesage
                send_button_chatlog.startAnimation(shakeAnimation)
        }
    }

}





class ChatItemFrom(val text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.textView_from_row.text = text

    }

    override fun getLayout(): Int {

        return R.layout.chat_from_row_chatlog

    }

}

class ChatItemTo(val text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

    viewHolder.itemView.textView_to_row.text = text

    }

    override fun getLayout(): Int {

        return R.layout.chat_to_row_chatlog

    }

}

