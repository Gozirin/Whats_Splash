package com.example.whatsappapp.whatsapp.chats

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappapp.MenuActivity
import com.example.whatsappapp.R
import com.example.whatsappapp.selectcontact.contacts.ContactsAdapter
import com.squareup.picasso.Picasso

class ChatsAdapter(val context: Context, private val chatList: ArrayList<ChatModal>) : RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {
    class ChatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtReceiverName)
        val message: TextView = view.findViewById(R.id.txtMessage)
        val image: TextView = view.findViewById(R.id.imgChatImage)
        val content:TextView = view.findViewById(R.id.chatContent)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsAdapter.ChatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_contact, parent, false)
        return ChatsViewHolder(view)
    }

    override fun onBindViewHolder(holder:ChatsViewHolder, position: Int) {
        val list = chatList[position]
        holder.name.text = list.receiver
        holder.message.text = list.message
       // Picasso.get().load(list.receiverImage).error(R.drawable.passport).into(holder.image)
        holder.content.setOnClickListener{
            val intent = Intent(context,MenuActivity::class.java).also {
                it.putExtra("OptionName","chatMessaging")
                it.putExtra("chatroom",list.receiverImage)
                it.putExtra("receiverName",list.receiver)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size

    }
}

