package com.krskhalaq.connecthub

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FrndsFragment : Fragment() {

    private lateinit var frndsRV: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_frnds, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Friends"

        var tempList = ArrayList<String>()
        for (i in 1..10)
            tempList.add("Friend $i")

        frndsRV = view.findViewById(R.id.frndsRV)
        frndsRV.setHasFixedSize(true)
        frndsRV.layoutManager = GridLayoutManager(requireContext(), 3)
        frndsRV.adapter = FrndsFragmentAdapter(requireContext(), tempList)

        return view
    }

}