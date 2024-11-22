package com.example.apitest.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.SpinnerAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.apitest.R
import com.example.apitest.databinding.ActivityCustomQuizBinding
import com.example.apitest.fetch_trivia.FetchTrivia
import com.example.apitest.utilities.Constants
import com.example.apitest.utilities.SimpleOnItemSelectedListener
import com.example.apitest.utilities.SimpleOnSeekBarChangeListener

class CustomTriviaActivity : AppCompatActivity() {

    private var binding: ActivityCustomQuizBinding? = null
    private var amount = 10
    private var category: Int? = null
    private var difficulty: String? = null
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomQuizBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSeekbar()

        val categories = Constants.getCategoryArray()

        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarTitle?.text = getString(R.string.custom_quiz_title)

        binding?.categorySpinner?.adapter = setSpinner(categories)
        binding?.difficultySpinner?.adapter = setSpinner(Constants.difficulties)
        binding?.typeSpinner?.adapter = setSpinner(Constants.types)

        setCategorySpinner()
        setDifficultySpinner()
        setTypeSpinner()

        binding?.startCustomQuiz?.setOnClickListener {
            FetchTrivia(this).getQuiz(amount, category, difficulty, type)
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

    private fun setCategorySpinner() {
        binding?.categorySpinner?.onItemSelectedListener =
            object : SimpleOnItemSelectedListener() {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    category = if (position == 0) null else position + 8
                }
            }
    }

    private fun setDifficultySpinner() {
        binding?.difficultySpinner?.onItemSelectedListener =
            object : SimpleOnItemSelectedListener() {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    difficulty = when (position) {
                        0 -> null
                        1 -> "easy"
                        2 -> "medium"
                        else -> "hard"
                    }
                }
            }
    }

    private fun setTypeSpinner() {
        binding?.typeSpinner?.onItemSelectedListener =
            object : SimpleOnItemSelectedListener() {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?,
                    position: Int, id: Long
                ) {
                    type = when (position) {
                        0 -> null
                        1 -> "multiple"
                        else -> "boolean"
                    }
                }

            }
    }

    private fun setSeekbar() {
        binding?.seekBarAmount?.setOnSeekBarChangeListener(object :
            SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                amount = progress
                val amountTextView = "Amount: $progress"
                binding?.amountTextView?.text = amountTextView
            }

        })
    }


    private fun setSpinner(list: List<String>): SpinnerAdapter {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

}