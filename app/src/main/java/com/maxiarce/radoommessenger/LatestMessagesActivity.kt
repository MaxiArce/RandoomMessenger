package com.maxiarce.radoommessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.maxiarce.radoommessenger.NewMessageActivity.Companion.USER_KEY
import com.maxiarce.radoommessenger.chatviews.LatestChatMessageRow
import com.maxiarce.radoommessenger.models.ChatMessage
import com.maxiarce.radoommessenger.models.User
import com.maxiarce.radoommessenger.registrationscreens.RegisterActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessagesActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<ViewHolder>()
    private val latestChatMessagesMap = HashMap<String,ChatMessage>()
    companion object {
        val TAG = "LatestMessagesActiviy"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)



        recyclerView_latest_messages.adapter = adapter
        recyclerView_latest_messages.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

        //setonclicklistener for the recyclerView
        adapter.setOnItemClickListener { item, view ->

            val intent = Intent(this,ChatActivity::class.java)

            val row = item as LatestChatMessageRow
            Log.d(TAG,row.chatPartnerUser?.username.toString())

            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }
        verifyUserIsLoggedIn()

        listenForLatestMessages()

    }

    private fun verifyUserIsLoggedIn(){

        //check if user is already logged in, and send to register activity
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            Log.d("LatestMessagesActivity","User not logged in")
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

        }else{
            Log.d("LatestMessagesActivity","User already logged in")
        }
    }

    private fun  listenForLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener{

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage =  p0.getValue(ChatMessage::class.java) ?:return
                latestChatMessagesMap[p0.key!!] = chatMessage
                refreshLatestMessagesRecyclerView()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage =  p0.getValue(ChatMessage::class.java) ?:return
                latestChatMessagesMap[p0.key!!] = chatMessage
                refreshLatestMessagesRecyclerView()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }


        })

    }

    private fun refreshLatestMessagesRecyclerView(){
        adapter.clear()
        latestChatMessagesMap.values.forEach{
            adapter.add(LatestChatMessageRow(it))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.menu_new_message ->{
                val intent = Intent(this,NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)

                Log.d("LatestMessagesActivity","User logged out")
            }
        }

        return super.onOptionsItemSelected(item)
    }
}

