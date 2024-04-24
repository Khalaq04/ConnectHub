package com.krskhalaq.connecthub

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProfileAdapter(private val context: Context, private val imageUrls: ArrayList<String>) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.profile_fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = imageUrls.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val profileIV = itemView.findViewById<ImageView>(R.id.profileIV)

        fun bind(position: Int) {
            val imgRef = SignUpActivity.storage.getReferenceFromUrl(imageUrls[position])
            imgRef.getBytes(10 * 1024 * 1024).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                profileIV.setImageBitmap(bitmap)
//                Log.i("HmAdapter", "Image Set!!!!!!!!!")
            }.addOnFailureListener {
                // Handle any errors
            }
        }

    }

}
