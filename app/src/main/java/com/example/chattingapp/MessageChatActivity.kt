package com.example.chattingapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.firebase.storage.UploadTask.TaskSnapshot
import com.example.chattingapp.Class.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

class MessageChatActivity : AppCompatActivity() {

    var userIdVisit : String = ""
    var firebaseUser: FirebaseUser? = null

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        intent = intent
        userIdVisit = intent.getStringExtra("visitId").toString()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)
        reference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot : DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                findViewById<TextView>(R.id.usernameMessageChat).text = user?.getUsername()
                Picasso.get().load(user?.getProfile()).into(findViewById<CircleImageView>(R.id.profileImageMessageChat))
            }

            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        findViewById<ImageView>(R.id.sendMessageButton).setOnClickListener {
            val message = findViewById<EditText>(R.id.textMessage).text.toString()
            if(message==""){
                Toast.makeText(this , "Write something..." , Toast.LENGTH_SHORT).show()
            }
            else{
                sendMessageToUser(firebaseUser?.uid, userIdVisit,message)
            }
            findViewById<EditText>(R.id.textMessage).setText("")
        }

        findViewById<ImageView>(R.id.attachImageFile).setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "images/*"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"),438)
        }
    }

    private fun sendMessageToUser(senderId : String? , receiverId : String , message : String) {
        val ref = FirebaseDatabase.getInstance().reference
        val messageKey = ref.push().key

        val messageHashMap = HashMap<String,Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isSeen"] = false
        messageHashMap["url"] = "" // for images
        messageHashMap["messageId"] = messageKey

        ref.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener{
                task ->
                if(task.isSuccessful){
                    val chatsListRef = FirebaseDatabase.getInstance().reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)

                    chatsListRef.addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onDataChange(snapshot : DataSnapshot) {
                            if(!snapshot.exists()){
                                chatsListRef.child("id").child(userIdVisit)
                            }
                            val chatsListRecRef = FirebaseDatabase.getInstance().reference
                                .child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)
                                .child("id")
                                .setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(error : DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

                    val reference = FirebaseDatabase.getInstance().reference
                        .child("Users").child(firebaseUser!!.uid)
                }
            }

    }

    override fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        if(requestCode == 438 && resultCode == RESULT_OK && data!=null && data.data!=null){
            val loadingBar = ProgressDialog(this)
            loadingBar.setMessage("Please wait, image is sending")
            loadingBar.show()

            val fileUri = data.data
            val storageRef = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageRef.child("$messageId.jpg")

            filePath.putFile(fileUri !!)
                .continueWithTask(Continuation<UploadTask.TaskSnapshot? , Task<Uri?>?> { task ->
                    if (! task.isSuccessful) {
                        throw task.exception !!
                    }
                    return@Continuation filePath.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val myUrl = downloadUri.toString()

                        val messageHashMap = HashMap<String,Any?>()
                        messageHashMap["sender"] = firebaseUser?.uid
                        messageHashMap["message"] = "sent you an image"
                        messageHashMap["receiver"] = userIdVisit
                        messageHashMap["isseen"] = false
                        messageHashMap["url"] = myUrl
                        messageHashMap["messageId"] = messageId

                        ref.child("Chats")
                            .child(messageId!!)
                            .setValue(messageHashMap)

                        loadingBar.dismiss()
                    } else {
                        loadingBar.dismiss()
                    }
                }
        }
    }
}