package com.krskhalaq.connecthub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var chatButton: ImageButton
    private lateinit var suggestion: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.post_nav
        setFragment(PostFragment())
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

        chatButton = findViewById(R.id.chat)
        chatButton.setOnClickListener{
            setFragment(ChatFragment())
        }

        suggestion = findViewById(R.id.suggestion)
        suggestion.setOnClickListener{
            setFragment(SuggestionFragment())
        }

    }

    fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFL, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }
}