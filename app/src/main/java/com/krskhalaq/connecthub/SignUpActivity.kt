package com.krskhalaq.connecthub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class SignUpActivity : AppCompatActivity() {

    companion object {
        var auth: FirebaseAuth = FirebaseAuth.getInstance()
        var dbFirebase: FirebaseDatabase = FirebaseDatabase.getInstance()
        var storage = FirebaseStorage.getInstance()
        var uId: String = ""
    }

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var login: Button
    private lateinit var signUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        signUp = findViewById(R.id.signUp)

        signUp.setOnClickListener {
            if (name.text.toString().isEmpty())
                Toast.makeText(this, "Name is required!", Toast.LENGTH_SHORT).show()
            else if (email.text.toString().isEmpty())
                Toast.makeText(this, "Email is required!", Toast.LENGTH_SHORT).show()
            else if (password.text.toString().isEmpty())
                Toast.makeText(this, "Password is required!", Toast.LENGTH_SHORT).show()
            else
                registerUser(name.text.toString(), email.text.toString(), password.text.toString())
        }

        login.setOnClickListener {
            if (email.text.toString().isEmpty())
                Toast.makeText(this, "Email is required!", Toast.LENGTH_SHORT).show()
            else if (password.text.toString().isEmpty())
                Toast.makeText(this, "Password is required!", Toast.LENGTH_SHORT).show()
            else {
                auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Login successful! Welcome to ConnectHub.", Toast.LENGTH_SHORT).show()
                            name.setText("")
                            email.setText("")
                            password.setText("")
                            val user = auth.currentUser
                            uId = user?.uid.toString()
                            finish()
                        }
                        else
                            Toast.makeText(this, "Entered Email or Password does not exist!", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun registerUser(userName: String, userEmail: String, userPassword: String) {
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val userId = user!!.uid
                    uId = userId

                    val dbReference = dbFirebase.getReference("Users").child(userId)

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap.put("userId", userId)
                    hashMap.put("userName", userName)
                    hashMap.put("profileImage", "")

                    dbReference.setValue(hashMap).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Sign Up successful! Welcome to ConnectHub.", Toast.LENGTH_SHORT).show()
                            name.setText("")
                            email.setText("")
                            password.setText("")
                            finish()
                        }
                        else
                            Toast.makeText(this, "Sign Up failed! Please check your Internet connection and retry.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}