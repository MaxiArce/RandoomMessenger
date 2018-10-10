package com.maxiarce.radoommessenger.registrationscreens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.maxiarce.radoommessenger.MessagesActivity
import com.maxiarce.radoommessenger.R
import com.maxiarce.radoommessenger.models.User
import java.util.*


class RegisterActivity : AppCompatActivity() {

    lateinit var usernameEditText: EditText
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var registerButton: Button
    lateinit var shakeAnimation: Animation
    lateinit var mAuth: FirebaseAuth
    lateinit var mStorageRef: StorageReference
    lateinit var selectedPhotoUri: Uri



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameEditText = username_editext_register
        emailEditText = email_editext_login
        passwordEditText =  password_editext_login
        registerButton = button_register
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_animation)

        //Firebase Authentication
        mAuth = FirebaseAuth.getInstance()



    }

    fun Register(view: View){

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()){
            Log.d("RegisterActivity", emailEditText.text.toString())

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){
                    Log.d("RegisterActivity","Registered ok, uid:" + it.result.user.uid)
                    uploadImageToFirebaseStorage()

                }else{
                    Log.d("RegisterActivity","Fail")
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
            mStorageRef.putFile(selectedPhotoUri).addOnSuccessListener {

                Log.d("RegisterActivity","Image uploaded Successfully")
                mStorageRef.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())

                    Log.d("RegisterActivity","Url File Location:${it}")
                }
            }
        }

    }

    private fun saveUserToFirebaseDatabase(profileUrl: String){

        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, usernameEditText.text.toString(), profileUrl)

        ref.setValue(user).addOnSuccessListener {
            Log.d("RegisterActivity","User saved to Firebase DB")

            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }

    }

}



