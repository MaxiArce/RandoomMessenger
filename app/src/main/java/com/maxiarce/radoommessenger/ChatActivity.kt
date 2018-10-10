package com.maxiarce.radoommessenger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.maxiarce.radoommessenger.models.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user.username

        setUpData()
    }

    private fun setUpData(){
        val adapter =  GroupAdapter<ViewHolder>()

        adapter.add(ChatItemTo("Hola mi nombre es"))
        adapter.add(ChatItemTo("Hola reply Test"))

        chat_recyclerView_chatlog.adapter = adapter
    }

}

class ChatItemFrom(val text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

    override fun getLayout(): Int {

        return R.layout.chat_from_row_chatlog

    }

}

class ChatItemTo(val text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {



    }

    override fun getLayout(): Int {

        return R.layout.chat_to_row_chatlog

    }

}

