package com.example.chattingapp.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.tasks.Continuation
import com.google.firebase.storage.UploadTask.TaskSnapshot
import android.widget.Toast
import com.example.chattingapp.Class.User
import com.example.chattingapp.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class SettingsFragment : Fragment() {

    var usersRef : DatabaseReference? = null
    var firebaseUser : FirebaseUser? = null
    private val RequestCode = 438;
    private var imageUri : Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker:String? = null
    private var socialChecker:String? = null

    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? ,
        savedInstanceState : Bundle?
    ) : View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings , container , false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        usersRef !!.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot : DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                    Log.d("settingsFragment","Reached")
                    view.findViewById<TextView>(R.id.userNameSettings).text = user?.getUsername()
                    Picasso.get().load(user?.getProfile()).into(view.findViewById<CircleImageView>(R.id.profileImageFS))
                    Picasso.get().load(user?.getCover()).into(view.findViewById<ImageView>(R.id.coverImage))

                }
            }

            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        view.findViewById<CircleImageView>(R.id.profileImageFS).setOnClickListener{
            pickImage()
        }

        view.findViewById<ImageView>(R.id.coverImage).setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }

        view.findViewById<ImageView>(R.id.linkedinProfileLink).setOnClickListener {
            socialChecker = "Linkedin"
            setSocialLinks()
        }

        view.findViewById<ImageView>(R.id.githubProfileLink).setOnClickListener {
            socialChecker = "Github"
            setSocialLinks()
        }

        view.findViewById<ImageView>(R.id.websiteProfileLink).setOnClickListener {
            socialChecker = "Website"
            setSocialLinks()
        }

        return view
    }

    private fun setSocialLinks() {
        val builder:AlertDialog.Builder =
            AlertDialog.Builder(context, androidx.appcompat.R.style.Theme_AppCompat_CompactMenu)

        val title: String = when (socialChecker) {
            "Website" -> "Write Url:"
            else -> "Write Username:"
        }

        builder.setTitle(title)

        val editText = EditText(context)
        if(socialChecker=="Website"){
            editText.hint = "e.g. www.google.com"
        }
        else{
            editText.hint = "e.g. rahul769311"
        }
        builder.setView(editText)

        builder.setPositiveButton("Create"){
            dialog,which ->
            val str = editText.text.toString()
            if(str==""){
                Toast.makeText(context , "Write Something" , Toast.LENGTH_SHORT).show()
            }
            else{
                saveSocialLink(str)
            }
        }

        builder.setNegativeButton("Cancel"){
                dialog,which ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun saveSocialLink(str : String) {
        val mapSocial = HashMap<String,Any>()
        when(socialChecker){
            "Linkedin" -> {
                mapSocial["linkedin"] = "https://www.linkedin.com/in/${str}/"
            }
            "Github" -> {
                mapSocial["github"] = "https://github.com/${str}"
            }
            "Website" ->{
                mapSocial["website"] = "https://$str"
            }
        }
        usersRef!!.updateChildren(mapSocial).addOnCompleteListener{
            task ->
            if(task.isSuccessful){
                Toast.makeText(context , "Saved Successfully" , Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImage() {
        // send user to gallery
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,RequestCode)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        if(requestCode==RequestCode && resultCode == Activity.RESULT_OK && data?.data!=null){
            imageUri = data.data
            Toast.makeText(context, "ImageUploading" , Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Image is Uploading, please wait ... ")
        progressBar.show()

        if(imageUri!=null) {
            val fileRef = storageRef !!.child(System.currentTimeMillis().toString() + ".jpg")
            fileRef.putFile(imageUri !!)
                .continueWithTask(Continuation<UploadTask.TaskSnapshot? , Task<Uri?>?> { task ->
                    if (! task.isSuccessful) {
                        throw task.exception !!
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val myUrl = downloadUri.toString()
                        val userMap = HashMap<String , Any>()
                        if(coverChecker=="cover"){
                            // cover image
                            userMap["cover"] = myUrl
                        }
                        else{
                            // profile image
                            userMap["profile"] = myUrl
                        }

                        usersRef?.updateChildren(userMap)
                        coverChecker = ""
                        progressBar.dismiss()
                    } else {
                        progressBar.dismiss()
                    }
                }
        }

    }

}