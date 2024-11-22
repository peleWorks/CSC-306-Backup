package com.example.apitest.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.apitest.R
import com.example.apitest.databinding.ActivityRandomFactBinding
import com.example.apitest.fetch_trivia.FetchFact
import com.example.apitest.firebase.FirebaseSetup
import com.example.apitest.utilities.Utils

class RandomFactActivity : AppCompatActivity(), FetchFact.FactCallback {

    private var binding: ActivityRandomFactBinding? = null
    private var currentFactNumber: Int = 0
    private var currentFactText: String = ""
    private var isCurrentFactSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRandomFactBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.random_fact_title)

        binding?.saveFact?.setOnClickListener {
            if (!isCurrentFactSaved) {
                FirebaseSetup().saveFact(currentFactNumber, currentFactText)
                isCurrentFactSaved = true
                Utils.showToast(this, "Fact Saved!")
            } else {
                Utils.showToast(this, "You have already saved this fact!")
            }

        }


        FetchFact(this).getFact(this)
    }

    override fun onFactFetched(factNumber: Int, fact: String) {
        currentFactNumber = factNumber
        currentFactText = fact
        binding?.factNumber?.text = "Fact ZAP #$factNumber"
        binding?.randomFact?.text = fact
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