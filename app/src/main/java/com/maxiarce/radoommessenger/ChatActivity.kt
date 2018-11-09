package com.maxiarce.radoommessenger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.chat_from_row.view.*



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

        //set recyclerview adapter
        chat_recyclerView_chatlog.adapter = adapter

        // set user name and image on the actionBar
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser.username

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
            }

            //push the message to toUser path on firebase
            toReference.setValue(chatMessage).addOnSuccessListener {
                Log.d("ChatActivity","Saved Sucessfully to toUser")
            }

            //clear editText
            entermessage_edittext_chatlog.text.clear()

        }else{
            if (entermessage_edittext_chatlog.text.isBlank()){
                // implement shake animation for buttonSendMesage
                send_button_chatlog.startAnimation(shakeAnimation)
            }
        }

    }

}





class ChatItemTo(val text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.textView_from_row.text = text

    }

    override fun getLayout(): Int {

        return R.layout.chat_to_row

    }

}

class ChatItemFrom(val text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.textView_to_row.text = text

    }

    override fun getLayout(): Int {

        return R.layout.chat_from_row

    }

}

