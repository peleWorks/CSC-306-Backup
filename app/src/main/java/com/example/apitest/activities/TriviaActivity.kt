package com.example.apitest.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.apitest.R
import com.example.apitest.data_classes.Result
import com.example.apitest.data_classes.TriviaQuestion
import com.example.apitest.databinding.ActivityTriviaBinding
import com.example.apitest.utilities.Constants
import java.util.Locale

class TriviaActivity : AppCompatActivity() {

    private var binding: ActivityTriviaBinding? = null

    private var score = 0
    private var timer: CountDownTimer? = null
    private var timeRemaining = 0
    private var play = true
    private var index = 0
    private var results = ArrayList<Result>()
    private lateinit var correctAnswer: String
    private lateinit var options: List<String>
    private lateinit var questions: ArrayList<TriviaQuestion>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTriviaBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        questions = intent.getSerializableExtraProvider<ArrayList<TriviaQuestion>>("questionList")
            ?: arrayListOf()

        setQuestions()
        setOptions()
        startTimer()

        binding?.questionNumber?.text =
            binding?.root?.context?.getString(R.string.question_text, 1, questions.size)
        binding?.nextButton?.setOnClickListener {
            onNext()
        }

        val redButtonBackground = ContextCompat.getDrawable(
            this, R.drawable.red_button_background
        )

        val optionListener = OnClickListener { view ->
            if (play) {
                timer?.cancel()
                view.background = redButtonBackground
                displayAnswer()
                setScore(view as Button?)
                play = false
            }

        }

        binding?.optionA?.setOnClickListener(optionListener)
        binding?.optionB?.setOnClickListener(optionListener)
        binding?.optionC?.setOnClickListener(optionListener)
        binding?.optionD?.setOnClickListener(optionListener)

    }

    private fun setQuestions() {
        val questionDecoded = Constants.decodeString(questions[index].question)
        binding?.questionText?.text = questionDecoded
    }

    private fun setOptions() {
        val question = questions[index]
        val randomOptions =
            Constants.randomizeOptions(question.correctAnswer, question.incorrectAnswers)

        correctAnswer = randomOptions.first
        options = randomOptions.second

        binding?.optionA?.text = options[0]
        binding?.optionB?.text = options[1]

        if (question.type == "multiple") {
            binding?.optionC?.visibility = View.VISIBLE
            binding?.optionD?.visibility = View.VISIBLE
            binding?.optionC?.text = options[2]
            binding?.optionD?.text = options[3]
        } else {
            binding?.optionC?.visibility = View.GONE
            binding?.optionD?.visibility = View.GONE
        }

    }

    private fun onNext() {

        val result = Result(
            10 - timeRemaining,
            questions[index].type,
            questions[index].difficulty,
            score
        )

        results.add(result)
        score = 0

        if (index < questions.size - 1) {
            timer?.cancel()
            index++
            setQuestions()
            setOptions()
            binding?.questionNumber?.text =
                binding?.root?.context?.getString(R.string.question_text, index + 1, questions.size)
            resetBackgroundColour()
            play = true
            startTimer()
        } else {
            endGame()
        }

    }

    private fun startTimer() {
        binding?.timeProgressBar?.max = 10
        binding?.timeProgressBar?.progress = 10

        timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(remainingTime: Long) {
                binding?.timeProgressBar?.incrementProgressBy(-1)
                binding?.timeLimit?.text = String.format(
                    Locale.getDefault(), "%d", remainingTime / 1000
                )
                timeRemaining = (remainingTime / 1000).toInt()
            }

            override fun onFinish() {
                displayAnswer()
                play = false
            }

        }.start()
    }

    private fun setScore(button: Button?) {
        if (correctAnswer == button?.text) {
            score = getScore()
        }
    }

    private fun getScore(): Int {

        val difficultyScore = when (questions[index].difficulty) {
            "easy" -> 1
            "medium" -> 2
            else -> 3
        }

        val questionTypeScore = when (questions[index].type) {
            "boolean" -> 1
            else -> 2
        }

        val timeRemainingScore: Int = (timeRemaining) / (10)


        return difficultyScore + questionTypeScore + timeRemainingScore
    }

    private fun displayAnswer() {
        val yellowBackground = ContextCompat.getDrawable(
            this, R.drawable.yellow_correct_button_background
        )
        when (true) {
            (correctAnswer == options[0]) -> binding?.optionA?.background = yellowBackground
            (correctAnswer == options[1]) -> binding?.optionB?.background = yellowBackground
            (correctAnswer == options[2]) -> binding?.optionC?.background = yellowBackground
            else -> binding?.optionD?.background = yellowBackground
        }
    }

    private fun resetBackgroundColour() {
        val defaultBackground = ContextCompat.getDrawable(
            this, R.drawable.button_gray_background
        )

        binding?.optionA?.background = defaultBackground
        binding?.optionB?.background = defaultBackground
        binding?.optionC?.background = defaultBackground
        binding?.optionD?.background = defaultBackground

    }

    private fun endGame() {
        val intent = Intent(this, FinishActivity::class.java)
        intent.putExtra("resultList", results)
        startActivity(intent)
        finish()
    }


    private inline fun <reified T : java.io.Serializable> Intent.getSerializableExtraProvider(
        identifierParameter: String
    ): T? {
        return this.getSerializableExtra(identifierParameter, T::class.java)
    }
}