package com.maxiarce.radoommessenger.registrationscreens

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.maxiarce.radoommessenger.LatestMessagesActivity
import com.maxiarce.radoommessenger.R
import kotlinx.android.synthetic.main.abc_activity_chooser_view.view.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var loginButton: Button
    lateinit var shakeAnimation: Animation
    lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        imageView_logo_login.setImageResource(R.mipmap.ic_launcher)
        progressBar = progressBar_login
        emailEditText = email_editext_login
        passwordEditText = password_editext_login
        loginButton = button_login
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_animation)

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        passwordEditText.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId){
                EditorInfo.IME_ACTION_DONE -> {
                    Login(v)
                    true
                }else -> false
            }
        }

    }


    fun Login(view: View){


        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        var currentUser = mAuth.currentUser

        if (email.isNotEmpty() && password.isNotEmpty() && currentUser == null){
            progressBar.visibility = View.VISIBLE
            loginButton.visibility = View.GONE

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if(it.isSuccessful){
                    //start MessageActivity with user data and clear previous activity
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()

                } else {
                    progressBar.visibility = View.GONE
                    loginButton.visibility = View.VISIBLE
                    loginButton.startAnimation(shakeAnimation)
                    Toast.makeText(this,"Wrong user or password",Toast.LENGTH_LONG).show()
                }

            }
        }
    }
}
