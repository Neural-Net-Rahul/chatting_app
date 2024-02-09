package com.example.chattingapp

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.chattingapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private var firebaseUser : FirebaseUser? = null
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarLogin)
        supportActionBar!!.title = "Login Page"

        binding.loginPageRegisterBtn.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        }

        binding.loginBtn.setOnClickListener {
            when {
                binding.emailLogin.text.toString() == "" || binding.passwordLogin.text.toString() == "" -> {
                    Toast.makeText(this , "Fill all details" , Toast.LENGTH_SHORT).show()
                }
                else -> {
                    mAuth.signInWithEmailAndPassword(binding.emailLogin.text.toString(),binding.passwordLogin.text.toString())
                        .addOnCompleteListener{
                            task ->
                            if(task.isSuccessful){
                                val intent = Intent(this,MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }
                            else{
                                Toast.makeText(this , "Login Failed" , Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
        }

        binding.forgotPassword.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Forgot Password")
            val view = layoutInflater.inflate(R.layout.dialog_forgot_password,null)
            val email = view.findViewById<EditText>(R.id.forgotPasswordEmail)
            builder.setView(view)
            builder.setPositiveButton("Reset"){_, _ ->
                if(email.text.toString() == ""){
                    Toast.makeText(this , "Fill your email id" , Toast.LENGTH_SHORT).show()
                }
                else {
                    forgottenPassword(email.text.toString())
                }
            }
            builder.setNegativeButton("close"){ _, _ ->
                // just close it
            }
            builder.show()
        }

    }

    private fun forgottenPassword(email:String) {
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    Toast.makeText(this , "Email Sent" , Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()

        firebaseUser = FirebaseAuth.getInstance().currentUser
        mAuth = FirebaseAuth.getInstance()

        if(firebaseUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }
}