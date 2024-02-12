package com.example.chattingapp.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.Class.Chat
import com.example.chattingapp.Class.User
import com.example.chattingapp.MessageChatActivity
import com.example.chattingapp.R
import com.example.chattingapp.VisitUserProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.protobuf.Value
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(mContext:Context , mUsers:MutableList<User> , isChatCheck:Boolean):
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

        private val mContext : Context
        private val mUsers : MutableList<User>
        private var isChatCheck : Boolean
        var lastMsg:String? = null

        init{
            this.mContext = mContext
            this.mUsers = mUsers
            this.isChatCheck = isChatCheck
        }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var userName: TextView = itemView.findViewById(R.id.userName)
        var profileImage : CircleImageView = itemView.findViewById(R.id.profileImage)
        var onlineImage : CircleImageView = itemView.findViewById(R.id.imageOnline)
        var offlineImage : CircleImageView = itemView.findViewById(R.id.imageOffline)
        var lastMessage : TextView = itemView.findViewById(R.id.lastMessage)
    }

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : ViewHolder {
        val view:View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() : Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder : ViewHolder , position : Int) {
        val user = mUsers[position]
        holder.userName.text = user.getUsername()
        Picasso.get().load(user.getProfile()).into(holder.profileImage)

        if(isChatCheck){
            holder.lastMessage.visibility = View.VISIBLE
            retrieveLastMessage(user.getUid(),holder.lastMessage)
        }
        else{
            holder.lastMessage.visibility = View.GONE
        }

        if(isChatCheck){
            if(user.getStatus() == "online"){
                holder.onlineImage.visibility = View.VISIBLE
                holder.offlineImage.visibility = View.GONE
            }
            else{
                holder.offlineImage.visibility = View.VISIBLE
                holder.onlineImage.visibility = View.GONE
            }
        }
        else{
            holder.onlineImage.visibility = View.GONE
            holder.offlineImage.visibility = View.GONE
        }

        holder.itemView.setOnClickListener{
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want")
            builder.setItems(options){
                dialog, position ->
                if(position == 0){
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visitId",user.getUid())
                    mContext.startActivity(intent)
                }
                else{
                    val intent = Intent(mContext, VisitUserProfileActivity::class.java)
                    intent.putExtra("visitId",user.getUid())
                    mContext.startActivity(intent)
                }
            }
            builder.show()
        }
    }

    private fun retrieveLastMessage(userId : String , lastMessage : TextView) {
        lastMsg = "defaultMsg"
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot : DataSnapshot) {
                for(snap in snapshot.children){
                    val chat = snap.getValue(Chat::class.java)
                    if(firebaseUser!= null && chat!=null){
                        if(chat.getReceiver() == firebaseUser.uid && chat.getSender()==userId
                            || chat.getSender() == firebaseUser.uid && chat.getReceiver() == userId){
                            lastMsg = chat.getMessage()
                        }
                    }
                }
                when(lastMsg ){
                    "defaultMsg" -> lastMessage.text = ""
                    "sent you an image" -> lastMessage.text = "image Sent"
                    else -> {
                        lastMessage.text = lastMsg
                    }
                }
                lastMsg = "defaultMsg"
            }

            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}