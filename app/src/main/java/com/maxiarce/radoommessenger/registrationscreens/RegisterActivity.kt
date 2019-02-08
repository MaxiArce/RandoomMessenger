package com.maxiarce.radoommessenger.registrationscreens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.maxiarce.radoommessenger.LatestMessagesActivity
import com.maxiarce.radoommessenger.R
import com.maxiarce.radoommessenger.models.User
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class RegisterActivity : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var usernameEditText: EditText
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var registerButton: Button
    lateinit var shakeAnimation: Animation
    lateinit var mAuth: FirebaseAuth
    lateinit var mStorageRef: StorageReference
    lateinit var tokenUser : String
    private var selectedPhotoUri: Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        progressBar = progressBar_register
        usernameEditText = username_editext_register
        emailEditText = email_editext_login
        passwordEditText =  password_editext_login
        registerButton = button_register
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_animation)
        selectphoto_imageview_register.setImageResource(R.mipmap.ic_camera_picture)

        //Firebase Authentication
        mAuth = FirebaseAuth.getInstance()
        getToken()

        //setlistener for enter key
        passwordEditText.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId){
                EditorInfo.IME_ACTION_DONE -> {
                    Register(v)
                    true
                }else -> false
            }
        }



    }

    fun Register(view: View){

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            progressBar.visibility = View.VISIBLE
            registerButton.visibility = View.GONE

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){
                    Log.d("RegisterActivity","Registered ok, uid:" + it.result!!.user.uid)
                    uploadImageToFirebaseStorage()

                }else{
                    progressBar.visibility = View.GONE
                    registerButton.visibility = View.VISIBLE
                    registerButton.startAnimation(shakeAnimation)
                    Toast.makeText(this,"Check the fields, something is wrong😱",Toast.LENGTH_LONG).show()

                }
            }

        }else{
            registerButton.startAnimation(shakeAnimation)

        }

    }

    fun Login(view: View){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun SelectPhoto(view: View){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,0)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)


            selectphoto_imageview_register.setImageBitmap(bitmap)
            selectphoto_button_register.alpha = 0f

        }
    }

    private fun uploadImageToFirebaseStorage(){

        if(selectedPhotoUri != null){
            val filename = UUID.randomUUID().toString()

            mStorageRef = FirebaseStorage.getInstance().getReference("/images/$filename")
            mStorageRef.putFile(selectedPhotoUri!!).addOnSuccessListener {

                Log.d("RegisterActivity","Image uploaded Successfully")
                mStorageRef.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
        }
        else{
            //set empty profile pic url
            saveUserToFirebaseDatabase("https://firebasestorage.googleapis.com/v0/b/messenger-app-58dce.appspot.com/o/empty_profile_pic.png?alt=media&token=ee169717-a272-445a-86ce-1afb45f541b5")
        }

    }

    private fun saveUserToFirebaseDatabase(profileUrl: String){

        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, usernameEditText.text.toString(), profileUrl,tokenUser)

        ref.setValue(user).addOnSuccessListener {
            Log.d("RegisterActivity","User saved to Firebase DB")

            val intent = Intent(this, LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

    }

    fun getToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener{
            Log.d("TOKEN",it.token)
            tokenUser = it.token
        }
    }



}



