package com.maxiarce.radoommessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.maxiarce.radoommessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    companion object {
        val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Contacts"

        fetchUsers()

    }

    //get list of all users on database
    private fun fetchUsers(){

        val ref = FirebaseDatabase.getInstance().getReference("/users/")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach{
                    Log.d("NewMessageActivity",it.toString())
                    val user = it.getValue(User::class.java)
                    if(user != null && user.uid != FirebaseAuth.getInstance().uid){
                        adapter.add(UserItem(user))
                    }
                }

                //set onClick for every row
                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem
                    val intent = Intent(this@NewMessageActivity,ChatActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish()
                }

                recyclerview_newmessage.adapter = adapter
            }
        })

    }
}

// bind every row of user
class UserItem(val user: User): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.username_textview_new_message.text = user.username

        if(user.profileImageUrl.isNotEmpty()) {
            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.username_imageview_new_message)
        }
    }

    override fun getLayout(): Int {

        return R.layout.user_row_new_message

    }

}
