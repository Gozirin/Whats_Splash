package com.example.whatsappapp.messages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappapp.R
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context?, private val messageList: ArrayList<MessageModal>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private val left = 0
    private val right = 1

    // RECYCLERVIEW ADAPTER IMPLEMENTED FUNCTIONS
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageAdapter.MessageViewHolder {
        return if (viewType == right) {
            val messageView = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_recylerview_sender, parent, false)
            return MessageViewHolder(messageView)
        } else {
            val messageView = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_recycler_receiver, parent, false)
            return MessageViewHolder(messageView)
        }
    }

    // GET ITEM VIEW FOR LEFT & RIGHT
    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].sender == FirebaseAuth.getInstance().currentUser?.uid.toString()) {
            left
        } else {
            right
        }
    }

    override fun onBindViewHolder(holder: MessageAdapter.MessageViewHolder, position: Int) {
        val list = messageList[position]
        holder.message.text = list.message
        holder.message.text = list.timeStamp
    }
    override fun getItemCount(): Int {
        return messageList.size
    }

    // MESSAGE FOR RECYCLERVIEW SENDER/RECEIVER
    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.txtMessage)
        val time: TextView = view.findViewById(R.id.txtTime)
    }
}
