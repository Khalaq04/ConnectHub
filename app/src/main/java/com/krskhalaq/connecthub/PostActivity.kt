package com.krskhalaq.connecthub

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import java.io.ByteArrayOutputStream

class PostActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var postImg: ImageView

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

//        val imageBitmap = intent.getParcelableExtra("ImageBitmap", Bitmap::class.java)

        if (imageUri != null)
            postImg.setImageURI(imageUri)
//        else if (imageBitmap != null)
//            postImg.setImageBitmap(imageBitmap)
        else
            finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}