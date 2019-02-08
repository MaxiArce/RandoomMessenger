package com.maxiarce.radoommessenger.chatviews

import com.maxiarce.radoommessenger.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

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