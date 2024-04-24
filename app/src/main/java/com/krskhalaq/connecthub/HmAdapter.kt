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

class HmAdapter(private val context: Context, private val imageUrls: ArrayList<String>, private val imgConnections: ArrayList<String>) : RecyclerView.Adapter<HmAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HmAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.hm_fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HmAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = imageUrls.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val hmIV = itemView.findViewById<ImageView>(R.id.hmIV)
        val hmTV = itemView.findViewById<TextView>(R.id.hmTV)

        fun bind(position: Int) {
            val imgRef = SignUpActivity.storage.getReferenceFromUrl(imageUrls[position])
            imgRef.getBytes(10 * 1024 * 1024).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                hmIV.setImageBitmap(bitmap)
                hmTV.text = imgConnections[position]
//                Log.i("HmAdapter", "Image Set!!!!!!!!!")
            }.addOnFailureListener {
                // Handle any errors
            }
        }

    }

}
