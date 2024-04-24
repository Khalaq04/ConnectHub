package com.krskhalaq.connecthub

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private lateinit var profileTV: TextView
    private lateinit var friends: ImageView
    private lateinit var noOfFriends: TextView
    private lateinit var signOutBtn: Button
    private lateinit var profileRV: RecyclerView
    private lateinit var profilePic: ImageView

    private fun getImageByteArray(photoUri: Uri): ByteArray {
        val originalBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource((activity as AppCompatActivity).contentResolver, photoUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap((activity as AppCompatActivity).contentResolver, photoUri)
        }
        Log.i("PostActivity", "Original width ${originalBitmap.width} and height ${originalBitmap.height}")
        val scaledBitmap = BitmapScalar.scaleToFitHeight(originalBitmap, 250)
        Log.i("PostActivity", "Scaled width ${scaledBitmap.width} and height ${scaledBitmap.height}")
        val byteOutputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteOutputStream)
        return byteOutputStream.toByteArray()
    }

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
                Log.i("PostActivity", "Saving Data to Firebase!")

                val imageByteArray = getImageByteArray(imgUri)
                val filePath = "profile/${SignUpActivity.uId}/${System.currentTimeMillis()}.jpg"
                val photoReference = SignUpActivity.storage.reference.child(filePath)
                photoReference.putBytes(imageByteArray)
                    .continueWithTask { photoUploadTask ->
                        Log.i("PostActivity", "Uploaded bytes: ${photoUploadTask.result?.bytesTransferred}")
                        photoReference.downloadUrl
                    }.addOnCompleteListener { downloadUrlTask ->
                        if (!downloadUrlTask.isSuccessful) {
                            Log.e("PostActivity", "Exception with Firebase Storage", downloadUrlTask.exception)
                            Toast.makeText(requireContext(), "Failed to Upload Image", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }
                        val downloadUrl = downloadUrlTask.result.toString()
                        Log.i("PostActivity", "Finished uploading $imgUri")

                        val dbReference = SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("profileImage")

                        dbReference.setValue(downloadUrl)
                    }
            }
        }
    )

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Profile"

        profileTV = view.findViewById(R.id.profileName)
        noOfFriends = view.findViewById(R.id.noOfConnects)
        signOutBtn = view.findViewById(R.id.signOutBtn)
        profileRV = view.findViewById(R.id.profileRV)
        profilePic = view.findViewById(R.id.profilePic)

        profilePic.setOnClickListener {
            if (requestPermission())
                launchGalleryIntent()
        }

        var profileImg = ""
        SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("profileImage").get().addOnSuccessListener {
            profileImg = it.value.toString()
            if (profileImg.isNotEmpty()) {
                val imgRef = SignUpActivity.storage.getReferenceFromUrl(profileImg)
                imgRef.getBytes(10 * 1024 * 1024).addOnSuccessListener {it1 ->
                    val bitmap = BitmapFactory.decodeByteArray(it1, 0, it1.size)
                    profilePic.setImageBitmap(bitmap)
//                Log.i("HmAdapter", "Image Set!!!!!!!!!")
                }.addOnFailureListener {
                    // Handle any errors
                }
            }
        }

        profileRV.setHasFixedSize(true)
        profileRV.layoutManager = GridLayoutManager(requireContext(), 3)
        var imgUrls = ArrayList<String>()

        val adapter = ProfileAdapter(activity as AppCompatActivity, imgUrls)
        profileRV.adapter = adapter

        SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("Posts").get().addOnSuccessListener {
            for (ds in it.children) {
                imgUrls.add(ds.value.toString())
            }
            adapter.notifyDataSetChanged()
        }

        friends = view.findViewById(R.id.connections)

        friends.setOnClickListener{

            friends.setOnClickListener {
                // Get the MainActivity and access mainFL
                val mainActivity = activity as MainActivity
                val mainFL = mainActivity.findViewById<FrameLayout>(R.id.mainFL)

                val transaction = mainActivity.supportFragmentManager.beginTransaction()
                transaction.replace(mainFL.id, FriendListFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }

        }

        signOutBtn.setOnClickListener {
            SignUpActivity.auth.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        var userName: String?
        SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userName = snapshot.child("userName").getValue(String::class.java)
                userName?.let { Log.i("ProfileFragment", it) }
                userName?.let { profileTV.text = userName }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileFragment", "Failed to read connections data", error.toException())
            }

        })

        SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("Connections").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connectionsCount = snapshot.childrenCount.toInt()
                noOfFriends.text = connectionsCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileFragment", "Failed to read connections data", error.toException())
            }
        })

        return view
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