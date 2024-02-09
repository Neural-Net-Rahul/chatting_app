package com.example.chattingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chattingapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var firebaseUserId : String

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarRegister)
        supportActionBar?.apply{
            setDisplayHomeAsUpEnabled(true)
            title = "Register Page"
        }

        binding.toolbarRegister.setNavigationOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        binding.registerPageLoginBtn.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        mAuth = FirebaseAuth.getInstance()
        binding.registerBtn.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        if(binding.usernameRegister.text.toString() == "" || binding.emailRegister.text.toString() == "" || binding.passwordRegister.text.toString() == ""){
            Toast.makeText(this , "Please Fill all details" , Toast.LENGTH_SHORT).show()
        }
        else{
            mAuth.createUserWithEmailAndPassword(binding.emailRegister.text.toString() ,
                binding.passwordRegister.text.toString())
                    .addOnCompleteListener{
                        task ->
                        if(task.isSuccessful){
                            firebaseUserId = mAuth.currentUser!!.uid
                            putDetailsInsideDatabase(binding.usernameRegister.text.toString())
                        }
                        else{
                            Toast.makeText(this , "Try to register again" , Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }

    private fun putDetailsInsideDatabase(username:String) {
        val userMap =  HashMap<String,Any>()
        userMap["uid"] = firebaseUserId
        userMap["username"] = username
        userMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/chatting-app-62c9b.appspot.com/o/profile_icon.webp?alt=media&token=c7cdd78e-4649-4c7b-acd2-007138dd30d9"
        userMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/chatting-app-62c9b.appspot.com/o/cover_page.jpg?alt=media&token=07793666-6b20-4f2b-b318-757ec41d31c5"
        userMap["status"] = "offline"
        userMap["search"] = username.lowercase()
        userMap["github"] = ""
        userMap["linkedin"] = ""
        userMap["website"] = ""

        FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserId).setValue(userMap)
            .addOnCompleteListener{
                task ->
                if(task.isSuccessful){
                    val intent = Intent(this,MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else{
                    Toast.makeText(this , "Try again" , Toast.LENGTH_SHORT).show()
                }
            }
    }
}