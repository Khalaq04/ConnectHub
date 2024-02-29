package com.krskhalaq.connecthub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        setFragment(HomeFragment())

        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home_nav -> {
                    setFragment(HomeFragment())
                }
                R.id.profile_nav -> {
                    setFragment(ProfileFragment())
                }
                R.id.post_nav -> {
                    setFragment(PostFragment())
                }
                R.id.frnds_nav -> {
                    setFragment(FrndsFragment())
                }
                R.id.search_nav -> {
                    setFragment(SearchFragment())
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFL, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }
}