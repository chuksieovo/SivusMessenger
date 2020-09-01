package com.example.sivusmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.internal.InternalAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        register_button_register.setOnClickListener {
            performRegister()
        }

        alreadyhaveanaccount.setOnClickListener {
            Log.d("MainActivity","Try to show login activity")

            // launch the login page activity

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        select_photo_register.setOnClickListener {
            Log.d("MainActivity","TRY TO SHOW PHOTO SELECTOR")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //proceed and check what the selected image was...
            Log.d("MainActivity","Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)
            select_image_button.alpha = 0f

            //val bitmapDrawable = BitmapDrawable(bitmap)
           // select_photo_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister(){
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter e-mail or password",Toast.LENGTH_SHORT).show()
            return}

        Log.d("MainActivity","The E-mail is : $email")
        Log.d("MainActivity","The Password is : $password")

        // Firebase Authentication to create user with e-mail and password
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //Sign in success
                    Log.d("MainActivity","createUserWithEmail: success")
                    val user = auth.currentUser
                } else {
                    // Sign in failure
                    Log.w("MainActivity","createUserWithEmail : failure", task.exception)
                    Toast.makeText(baseContext,"Authentication Failed.",
                        Toast.LENGTH_SHORT).show()
                }
                 uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d("MainActivity","Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user",Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage (){
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("MainActivity","SUCCESSFULLY UPLOADED IMAGE WITH ${it.metadata?.path}")

                saveUserToFirebaseDatabase(it.toString())
            }
    }

    private fun saveUserToFirebaseDatabase (profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("users/ $uid")

        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("MainActivity","FINALLY WE HAVE SAVED THE USER TO THE FIREBASE DATABASE")
            }
    }
}

class User (val uid: String, val username: String, val profileImageUrl: String)