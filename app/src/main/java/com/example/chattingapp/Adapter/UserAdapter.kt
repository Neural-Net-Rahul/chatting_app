package com.example.chattingapp.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.Class.User
import com.example.chattingapp.MessageChatActivity
import com.example.chattingapp.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(mContext:Context , mUsers:MutableList<User> , isChatCheck:Boolean):
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

        private val mContext : Context
        private val mUsers : MutableList<User>
        private var isChatCheck : Boolean

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

                }
            }
            builder.show()
        }
    }
}