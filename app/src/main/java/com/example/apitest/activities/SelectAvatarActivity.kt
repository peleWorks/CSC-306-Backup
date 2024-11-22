package com.example.apitest.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.apitest.R
import com.example.apitest.data_classes.UserModel
import com.example.apitest.databinding.ActivitySelectAvatarBinding
import com.example.apitest.firebase.FirebaseSetup
import com.example.apitest.recycler_view_adapters.AvatarAdapter

class SelectAvatarActivity : AppCompatActivity() {
    private var binding: ActivitySelectAvatarBinding? = null
    private val firebaseSetup = FirebaseSetup()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectAvatarBinding.inflate(layoutInflater)
        setContentView(binding?.root)



        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarTitle?.text = getString(R.string.select_avatar_title)




        firebaseSetup.getUserInfo(object : FirebaseSetup.UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                userInfo?.let { user ->
                    setupRecyclerView(user.image)
                }
            }
        })

    }

    private fun setupRecyclerView(currentAvatar: String) {
        val adapter = AvatarAdapter(currentAvatar) { selectedAvatar ->
            firebaseSetup.getUserInfo(object : FirebaseSetup.UserInfoCallback {
                override fun onUserInfoFetched(userInfo: UserModel?) {
                    val let = userInfo?.let { user ->
                        val updatedUser = user.copy(image = selectedAvatar)
                        firebaseSetup.updateUserPreferences(updatedUser)


                    }
                }
            })
        }

        binding?.avatarsRecyclerView?.apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(this@SelectAvatarActivity, 3)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }


}