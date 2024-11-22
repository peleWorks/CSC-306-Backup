package com.example.apitest.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.apitest.R
import com.example.apitest.fragments.HomeFragment
import com.example.apitest.fragments.LeaderboardFragment
import com.example.apitest.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarTitle: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val socialFragment = LeaderboardFragment()
        val profileFragment = ProfileFragment()


        toolbar = findViewById(R.id.toolbar)
        toolbarTitle = findViewById(R.id.toolbar_title)
        setSupportActionBar(toolbar)

        setCurrentFragment(homeFragment, "Home")

        bottomNavigationView = findViewById(R.id.bottom_navigation_menu)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    setCurrentFragment(homeFragment, "Home")
                    Log.i(tag, "Home selected")
                }

                R.id.leaderboard -> {
                    setCurrentFragment(socialFragment, "Social")
                    Log.i(tag, "Social selected")
                }

                R.id.profile -> {
                    setCurrentFragment(profileFragment, "Profile")
                    Log.i(tag, "Profile selected")
                }

            }
            true
        }

    }


    private fun setCurrentFragment(fragment: Fragment, title: String? = null) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }




        toolbarTitle.text = title ?: ""
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayUseLogoEnabled(true)
            setLogo(R.drawable.logo)
        }
    }



}