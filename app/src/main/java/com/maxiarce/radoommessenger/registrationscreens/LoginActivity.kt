package com.maxiarce.radoommessenger.registrationscreens

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.maxiarce.radoommessenger.LatestMessagesActivity
import com.maxiarce.radoommessenger.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var loginButton: Button
    lateinit var shakeAnimation: Animation
    lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        emailEditText = email_editext_login
        passwordEditText = password_editext_login
        loginButton = button_login
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_animation)

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance()

    }


    fun Login(view: View){


        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        var currentUser = mAuth.currentUser

        if (email.isNotEmpty() && password.isNotEmpty() && currentUser == null){

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if(it.isSuccessful){
                    //start MessageActivity with user data and clear previous activity
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()

                }else{

                }
            }
        }
    }
}
