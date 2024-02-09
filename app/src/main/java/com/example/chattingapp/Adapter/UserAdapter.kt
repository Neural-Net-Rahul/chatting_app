package com.example.chattingapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.Class.User
import com.example.chattingapp.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(mContext:Context, mUsers:MutableList<User>,isChatCheck:Boolean):
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
    }
}