package com.example.chattingapp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.Class.Chat
import com.example.chattingapp.Class.User
import com.example.chattingapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(private var mContext : Context , private var mChatList:MutableList<Chat> , private var imageUrl:String):
    RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

        var firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var profileImage:CircleImageView? = null
        var textMessage:TextView? = null
        var leftImageView:ImageView? = null
        var textSeen:TextView? = null
        var rightImageView:ImageView? = null

        init{
            profileImage = itemView.findViewById(R.id.profileImageMIL)
            textMessage = itemView.findViewById(R.id.showTextMessage)
            leftImageView = itemView.findViewById(R.id.leftImageView)
            rightImageView = itemView.findViewById(R.id.rightImageView)
            textSeen = itemView.findViewById(R.id.textSeen)
        }

    }

    override fun onCreateViewHolder(parent : ViewGroup , position : Int) : ViewHolder {
        return if(position==1){
            val view : View = LayoutInflater.from(mContext).inflate(R.layout.message_item_right,parent,false)
            ViewHolder(view)
        }
        else{
            val view : View = LayoutInflater.from(mContext).inflate(R.layout.message_item_left,parent,false)
            ViewHolder(view)
        }
    }

    override fun getItemCount() : Int {
        return mChatList.size
    }

    override fun onBindViewHolder(holder : ViewHolder , position : Int) {
        val chat = mChatList[position]
        if(chat.getMessage().equals("sent you an image") && chat.getUrl()!=""){
            if(chat.getSender()==firebaseUser!!.uid){
                holder.textMessage!!.visibility = View.GONE
                holder.rightImageView!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.rightImageView)
            }
            else{
                holder.textMessage!!.visibility = View.GONE
                holder.leftImageView!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.leftImageView)
            }
        }
        else{
            holder.textMessage!!.text = chat.getMessage()
        }

        // sent and seen message
        if(position == mChatList.size - 1){
            // chat sender == firebase user id
            // show sent
            // and when it is seen -> replace it with seen
//            Log.d("Chatting12","${chat.isSeen()}")
            if(chat.getSender() == firebaseUser!!.uid){
//                Log.d("Chatting12","Entered")
                if(!chat.isSeen()) {
                    holder.textSeen!!.text = "Sent"
                }
                else{
                    holder.textSeen!!.text = "Seen"
                }
            }
            else{
                holder.textSeen!!.visibility = View.GONE
            }
        }
        else{
            holder.textSeen!!.visibility = View.GONE
        }

        if(chat.getSender()!=firebaseUser!!.uid){
            Picasso.get().load(imageUrl).into(holder.profileImage)
        }
    }

    override fun getItemViewType(position : Int) : Int {
        return if(mChatList[position].getSender() == firebaseUser!!.uid){
            1;
        }
        else{
            0;
        }
    }
}