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

class SearchFragment : Fragment() {

    private lateinit var searchRV: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Search"

        var tempList = ArrayList<String>()
        for (i in 1..10)
            tempList.add("User $i")

        searchRV = view.findViewById(R.id.searchRV)
        searchRV.setHasFixedSize(true)
        searchRV.layoutManager = GridLayoutManager(requireContext(), 3)
        searchRV.adapter = SearchFragmentAdapter(requireContext(), tempList)

        return view
    }

}