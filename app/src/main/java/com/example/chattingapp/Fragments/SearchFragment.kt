package com.example.chattingapp.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.Adapter.UserAdapter
import com.example.chattingapp.Class.User
import com.example.chattingapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {
    private var userAdapter : UserAdapter? = null
    private var mUsers : List<User>? = null
    private var recyclerView:RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.searchListRecyclerView)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mUsers = ArrayList()

        // Retrieve all users
        retrieveAllUsers()

        // Add TextWatcher to search bar
        view.findViewById<EditText>(R.id.searchUsersBar)
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
                        if (user != null && user.getUid() != FirebaseAuth.getInstance().currentUser?.uid) {
                            (mUsers as ArrayList<User>).add(user)
                        }
                    }
                    userAdapter = UserAdapter(context!!, mUsers as ArrayList<User>, false)
                    recyclerView?.adapter = userAdapter
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // does partial search with on text changed
    private fun searchForUsers(str: String) {
        FirebaseDatabase.getInstance().reference.child("Users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    (mUsers as ArrayList<User>).clear()
                    for (snap in snapshot.children) {
                        val user = snap.getValue(User::class.java)
                        if (user != null && user.getUid() != FirebaseAuth.getInstance().currentUser?.uid) {
                            val username = user.getUsername() ?: ""
                            // Perform partial search on the username
                            if (username.contains(str, ignoreCase = true)) {
                                (mUsers as ArrayList<User>).add(user)
                            }
                        }
                    }
                    userAdapter = UserAdapter(context!!, mUsers as ArrayList<User>, false)
                    recyclerView?.adapter = userAdapter
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}