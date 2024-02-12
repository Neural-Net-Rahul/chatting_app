package com.example.chattingapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.chattingapp.Class.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class VisitUserProfileActivity : AppCompatActivity() {

    var usersRef : DatabaseReference? = null
    var userIdVisit : String? = null
    var users:User? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_profile)

        intent = intent
        userIdVisit = intent.getStringExtra("visitId").toString()

        usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(userIdVisit !!)

        usersRef !!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot : DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                    users = user
                    Log.d("settingsFragment","Reached")
                    findViewById<TextView>(R.id.userNameSettingsUser).text = user?.getUsername()
                    Picasso.get().load(user?.getProfile()).into(findViewById<CircleImageView>(R.id.profileImageUser))
                    Picasso.get().load(user?.getCover()).into(findViewById<ImageView>(R.id.coverImageUser))

                }
            }
            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        findViewById<ImageView>(R.id.linkedinProfileLinkUser).setOnClickListener {
            val uri = Uri.parse(users!!.getLinkedin())
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.githubProfileLinkUser).setOnClickListener {
            val uri = Uri.parse(users!!.getGithub())
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.websiteProfileLinkUser).setOnClickListener {
            val uri = Uri.parse(users!!.getWebsite())
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.sendMessageUser).setOnClickListener {
            val intent = Intent(this@VisitUserProfileActivity,MessageChatActivity::class.java)
            intent.putExtra("visitId",users!!.getUid())
            startActivity(intent)
        }
    }
}