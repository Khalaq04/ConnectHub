package com.krskhalaq.connecthub

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.ByteArrayOutputStream


class PostActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var postImg: ImageView
    private var imgUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingInflatedId", "WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        toolbar = findViewById(R.id.toolbarPost)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        postImg = findViewById(R.id.postImg)

        val imageUriString = intent.getStringExtra("ImageUri")
        var imageUri: Uri? = null
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString)
        }

        if (imageUri != null) {
            postImg.setImageURI(imageUri)
            imgUri = imageUri
        }
        else
            finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        if (item.itemId == R.id.post_toolbar) {
            saveImage()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.post_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun saveImage() {
        Log.i("PostActivity", "Saving Data to Firebase!")

        val imageByteArray = imgUri?.let { getImageByteArray(it) }
        val filePath = "posts/${SignUpActivity.uId}/${System.currentTimeMillis()}.jpg"
        val photoReference = SignUpActivity.storage.reference.child(filePath)
        if (imageByteArray != null) {
            photoReference.putBytes(imageByteArray)
                .continueWithTask { photoUploadTask ->
                    Log.i("PostActivity", "Uploaded bytes: ${photoUploadTask.result?.bytesTransferred}")
                    photoReference.downloadUrl
                }.addOnCompleteListener { downloadUrlTask ->
                    if (!downloadUrlTask.isSuccessful) {
                        Log.e("PostActivity", "Exception with Firebase Storage", downloadUrlTask.exception)
                        Toast.makeText(this, "Failed to Upload Image", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                    val downloadUrl = downloadUrlTask.result.toString()
                    Log.i("PostActivity", "Finished uploading $imgUri")

                    val dbReference = SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("Posts")

                    dbReference.push().setValue(downloadUrl).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Post Successful!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else
                            Toast.makeText(this, "Post Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun getImageByteArray(photoUri: Uri): ByteArray {
        val originalBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, photoUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
        }
        Log.i("PostActivity", "Original width ${originalBitmap.width} and height ${originalBitmap.height}")
        val scaledBitmap = BitmapScalar.scaleToFitHeight(originalBitmap, 250)
        Log.i("PostActivity", "Scaled width ${scaledBitmap.width} and height ${scaledBitmap.height}")
        val byteOutputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteOutputStream)
        return byteOutputStream.toByteArray()
    }
}