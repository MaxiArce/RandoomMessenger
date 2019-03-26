package com.maxiarce.radoommessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
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
    private val fromId = FirebaseAuth.getInstance().uid


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_latest_messages)

        //set ToolBar
        val toolbar: Toolbar  = findViewById(R.id.toolbar_latest_messages)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar!!
        actionBar.title = "Radoom Messenger"
        actionBar.setIcon(R.drawable.ic_main_icon)


        recyclerView_latest_messages.adapter = adapter
        recyclerView_latest_messages.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

        //set onclicklistener for the recyclerView
        adapter.setOnItemClickListener { item, view ->

            val intent = Intent(this,ChatActivity::class.java)

            val row = item as LatestChatMessageRow

            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        verifyUserIsLoggedIn()

        listenForLatestMessages()

    }

    //check if user is already logged in, if is not then open register activity
    private fun verifyUserIsLoggedIn(){
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

    //Lister for latest Messages and add to recyclerView
    private fun  listenForLatestMessages(){

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

    fun newMessage(view: View){
        val intent = Intent(this,NewMessageActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){

            R.id.menu_sign_out ->{
                //delete token from database to avoid notifications
                val ref = FirebaseDatabase.getInstance().getReference("/users/$fromId/")
                ref.child("token").setValue("")

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

