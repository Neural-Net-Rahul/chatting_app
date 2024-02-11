package com.example.chattingapp.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.Adapter.UserAdapter
import com.example.chattingapp.Class.ChatList
import com.example.chattingapp.Class.User
import com.example.chattingapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {
    private var userAdapter : UserAdapter? = null
    private var mUsers : List<User>? = null
    private var mChatListUsers : List<ChatList>? = null
    private var recyclerView:RecyclerView? = null
    private var firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerView = view.findViewById(R.id.searchListRecyclerViewChatFrag)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mUsers = ArrayList()
        mChatListUsers = ArrayList()

        FirebaseDatabase.getInstance().reference.child("ChatList")
            .child(firebaseUser.uid)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot : DataSnapshot) {
                    if(snapshot.exists()){
                        for(snap in snapshot.children){
                            val id = snap.getValue(ChatList::class.java)
                            (mChatListUsers as ArrayList<ChatList>).add(id!!)
                        }
                        retrieveAllUsers()
                    }
                }

                override fun onCancelled(error : DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        // Add TextWatcher to search bar
        view.findViewById<EditText>(R.id.searchUsersBarChatFrag)
            .addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    searchForUsers(s.toString().lowercase())
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        return view
    }

    private fun retrieveAllUsers() {
        FirebaseDatabase.getInstance().reference.child("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    (mUsers as ArrayList<User>).clear()
                    for (snap in snapshot.children) {
                        val user = snap.getValue(User::class.java)
                        for(eachChatList in mChatListUsers!!){
                            if(user!!.getUid() == eachChatList.getId()) {
                                (mUsers as ArrayList<User>).add(user)
                                break;
                            }
                        }
                    }
                    userAdapter = UserAdapter(context!!, mUsers as ArrayList<User>, true)
                    recyclerView?.adapter = userAdapter
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun searchForUsers(str: String) {
        FirebaseDatabase.getInstance().reference.child("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    (mUsers as ArrayList<User>).clear()
                    for (snap in snapshot.children) {
                        val user = snap.getValue(User::class.java)
                        for(eachChatList in mChatListUsers!!){
                            if(user!!.getUid() == eachChatList.getId()) {
                                val username = user.getUsername() ?: ""
                                // Perform partial search on the username
                                if (username.contains(str, ignoreCase = true)) {
                                    (mUsers as ArrayList<User>).add(user)
                                }
                            }
                        }
                    }
                    userAdapter = UserAdapter(context!!, mUsers as ArrayList<User>, true)
                    recyclerView?.adapter = userAdapter
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}