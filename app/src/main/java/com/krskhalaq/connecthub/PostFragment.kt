package com.krskhalaq.connecthub

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class PostFragment : Fragment() {

    private lateinit var galleryCV: CardView
    private lateinit var cameraCV: CardView

    private lateinit var imageUri: Uri

    private val galleryActivityLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
            if (it.resultCode != Activity.RESULT_OK || it.data == null)
            {
                Log.w("PostFragment", "Did not get data back from the launched activity, user likely cancelled flow")
                return@ActivityResultCallback
            }

            val imgUri = it.data!!.data

            if (imgUri != null)
            {
                imageUri = imgUri
//                Log.i("PostFragment", imageUri.toString())
                val intent = Intent(requireContext(), PostActivity::class.java)
                intent.putExtra("ImageUri", imgUri.toString())
                startActivity(intent)
            }
    })

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val cameraActivityLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
            if (it.resultCode != Activity.RESULT_OK)
            {
                Log.w("PostFragment", "Did not get data back from the launched activity, user likely cancelled flow")
                return@ActivityResultCallback
            }

//            val imageBitmap = it.data!!.extras!!.getParcelable("data", Bitmap::class.java) as Bitmap
//
//            val intent = Intent(requireContext(), PostActivity::class.java)
//            intent.putExtra("ImageBitmap", imageBitmap)
//            startActivity(intent)

//            val imgUri = it.data!!.data

            //                Log.i("PostFragment", imageUri.toString())
            val intent = Intent(requireContext(), PostActivity::class.java)
            intent.putExtra("ImageUri", imageUri.toString())
            startActivity(intent)
        }
    )

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Select Image"

        galleryCV = view.findViewById(R.id.galleryCV)
        cameraCV = view.findViewById(R.id.cameraCV)

        galleryCV.setOnClickListener {
            if (requestPermission())
                launchGalleryIntent()
        }

        cameraCV.setOnClickListener {
            if (requestPermission())
                launchCameraIntent()
        }

        return view
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun launchCameraIntent() {
        val photoURI = FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".provider",
            createImageFile()!!
        )

        imageUri = photoURI

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        cameraActivityLauncher.launch(intent)
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "Pic$timeStamp"
        val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/")
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        return image
    }

    private fun launchGalleryIntent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        galleryActivityLauncher.launch(Intent.createChooser(intent, "Choose Pics"))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA), 444)
            return false
        }
        return true
    }

}