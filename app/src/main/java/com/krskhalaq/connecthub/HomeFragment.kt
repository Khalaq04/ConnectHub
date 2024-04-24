package com.krskhalaq.connecthub

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HomeFragment : Fragment() {

    private lateinit var hmRV: RecyclerView

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "ConnectHub"

        hmRV = view.findViewById(R.id.hmRV)
        hmRV.setHasFixedSize(true)
        hmRV.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        var imageUrls = HashMap<String, String>()
        var imgUrls = ArrayList<String>()
        var imgConnections = ArrayList<String>()

        val adapter = HmAdapter(activity as AppCompatActivity, imgUrls, imgConnections)
        hmRV.adapter = adapter

        val connections = ArrayList<String>()
        SignUpActivity.dbFirebase.getReference("Users").get().addOnSuccessListener {
            for (ds in it.child(SignUpActivity.uId).child("Connections").children) {
                connections.add(ds.value.toString())
//                Log.i("HomeFragment", ds.value.toString())
            }

//            Log.i("HomeFragment", "Connections Fetched!!!!!!!!!")

            for (ds in it.children) {
//                Log.i("HomeFragment", ds.value.toString())
                if (connections.contains(ds.child("userId").value.toString())) {
//                    Log.i("HomeFragment", "Getting Posts!!!!!!!!!")
                    for (ds1 in it.child(ds.child("userId").value.toString()).child("Posts").children) {
                        imageUrls.put(ds1.value.toString(), ds.child("userName").value.toString())
//                        Log.i("HomeFragment", ds1.value.toString())
                    }
                }
            }

//            Log.i("HomeFragment", "Urls Fetched!!!!!!!!!")

            var urls: List<*> = ArrayList<Any?>(imageUrls.keys)
            urls = urls.shuffled()



            for (url in urls) {
                imgUrls.add(url.toString())
                imgConnections.add(imageUrls[url.toString()].toString())
//                Log.i("HomeFragment", url.toString())
            }

//            Log.i("HomeFragment", "Notified Dataset!!!!!!!!!")

            adapter.notifyDataSetChanged()
        }

        return view
    }

}