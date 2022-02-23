package hu.ait.trivagame

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import hu.ait.trivagame.data.Question
import hu.ait.trivagame.data.Questions
import hu.ait.trivagame.databinding.ActivityMainBinding
import hu.ait.trivagame.databinding.ActivityQuestionsBinding
import hu.ait.trivagame.retrofit.TriviaAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

class QuestionsActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityQuestionsBinding

    private var myCurrentPos : Int = 1
    private var mySelectedOptionPos : Int = 0
    private var questionsList : List<Question>? = null
    private var correctOption : Int = 0
    private var score : Int = 0
    private var username : String? = null
    private var selectedCategory : Int? = null
    private var numQuestions : Int? = null
    private var selectedDifficulty : String? = null
    private var hasSubmitted : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        username = intent.getStringExtra("USER_NAME")
        selectedCategory = intent.getIntExtra("CATEGORY", 9)
        numQuestions = intent.getIntExtra("NUM_QUESTIONS", 10)
        selectedDifficulty = intent.getStringExtra("DIFFICULTY")

        binding.progessBar.max = numQuestions!!

        getQuestions()

        binding.tvOptionOne.setOnClickListener(this)
        binding.tvOptionTwo.setOnClickListener(this)
        binding.tvOptionThree.setOnClickListener(this)
        binding.tvOptionFour.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }

    private fun getQuestions() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val quizService = retrofit.create(TriviaAPI::class.java)

        val quizCall = quizService.getResult(
            numQuestions!!,
            selectedCategory!!,
            "multiple",
            selectedDifficulty!!
        )

        quizCall.enqueue(
            object: Callback<Questions> {
                override fun onFailure(call: Call<Questions>, t: Throwable) {
                    binding.tvQuestion.text = getString(R.string.api_error)
                }

                override fun onResponse(call: Call<Questions>, response: Response<Questions>) {
                    val questionResponse = response.body()
                    questionsList = questionResponse!!.results
                    setQuestion()
                }
            }
        )
    }

    private fun setQuestion() {

        hasSubmitted = false
        val question = questionsList!![myCurrentPos-1]

        defaultOptionsView()

        binding.btnSubmit.text = getString(R.string.submit)

        binding.progessBar.progress = myCurrentPos
        binding.tvProgress.text = "$myCurrentPos" + getString(R.string.oblique) + binding.progessBar.max

        binding.tvQuestion.text = Html.fromHtml(question.question).toString()

        correctOption = (1..4).random()

        if (correctOption == 1) {
            binding.tvOptionOne.text = Html.fromHtml(question.correct_answer).toString()
            binding.tvOptionTwo.text = Html.fromHtml(question.incorrect_answers[0]).toString()
            binding.tvOptionThree.text = Html.fromHtml(question.incorrect_answers[1]).toString()
            binding.tvOptionFour.text = Html.fromHtml(question.incorrect_answers[2]).toString()
        } else if (correctOption == 2) {
            binding.tvOptionTwo.text = Html.fromHtml(question.correct_answer).toString()
            binding.tvOptionOne.text = Html.fromHtml(question.incorrect_answers[0]).toString()
            binding.tvOptionThree.text = Html.fromHtml(question.incorrect_answers[1]).toString()
            binding.tvOptionFour.text = Html.fromHtml(question.incorrect_answers[2]).toString()
        } else if (correctOption == 3) {
            binding.tvOptionThree.text = Html.fromHtml(question.correct_answer).toString()
            binding.tvOptionTwo.text = Html.fromHtml(question.incorrect_answers[0]).toString()
            binding.tvOptionOne.text = Html.fromHtml(question.incorrect_answers[1]).toString()
            binding.tvOptionFour.text = Html.fromHtml(question.incorrect_answers[2]).toString()
        } else {
            binding.tvOptionFour.text = Html.fromHtml(question.correct_answer).toString()
            binding.tvOptionTwo.text = Html.fromHtml(question.incorrect_answers[0]).toString()
            binding.tvOptionThree.text = Html.fromHtml(question.incorrect_answers[1]).toString()
            binding.tvOptionOne.text = Html.fromHtml(question.incorrect_answers[2]).toString()
        }
    }

    private fun defaultOptionsView() {
        val options = ArrayList<TextView>()
        options.add(0, binding.tvOptionOne)
        options.add(1, binding.tvOptionTwo)
        options.add(2, binding.tvOptionThree)
        options.add(3, binding.tvOptionFour)

        for (option in options) {
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(
                this,
                R.drawable.default_option_bg
            )
        }
    }

    override fun onClick(p0: View?) {

        when(p0?.id) {
            R.id.tvOptionOne -> {
                if (!hasSubmitted) {
                    selectedOptionView(binding.tvOptionOne, 1)
                }
            }
            R.id.tvOptionTwo -> {
                if (!hasSubmitted) {
                    selectedOptionView(binding.tvOptionTwo, 2)
                }
            }
            R.id.tvOptionThree -> {
                if (!hasSubmitted) {
                    selectedOptionView(binding.tvOptionThree, 3)
                }
            }
            R.id.tvOptionFour -> {
                if (!hasSubmitted) {
                    selectedOptionView(binding.tvOptionFour, 4)
                }
            }
            R.id.btnSubmit -> {

                if (mySelectedOptionPos == 0){
                    myCurrentPos++

                    when{
                        myCurrentPos <= questionsList!!.size -> {
                            setQuestion()
                        } else -> {
                            val intent = Intent(this, ResultActivity::class.java)
                            intent.putExtra("USER_NAME", username)
                            intent.putExtra("SCORE", score)
                            intent.putExtra("NUM_QUESTIONS", numQuestions)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    hasSubmitted = true
                    val question = questionsList!![myCurrentPos-1]
                    if (correctOption != mySelectedOptionPos){
                        answerView(mySelectedOptionPos, R.drawable.wrong_option_bg)
                    } else {
                        score++
                    }
                    answerView(correctOption, R.drawable.correct_option_bg)

                    if (myCurrentPos == questionsList!!.size) {
                        binding.btnSubmit.text = getString(R.string.finish)
                    } else {
                        binding.btnSubmit.text = getString(R.string.next)
                    }
                    mySelectedOptionPos = 0
                }
            }
        }
    }

    private fun answerView(answer: Int, drawableView: Int) {
        when(answer) {
            1 -> {
                binding.tvOptionOne.background = ContextCompat.getDrawable(
                    this, drawableView
                )
            }
            2 -> {
                binding.tvOptionTwo.background = ContextCompat.getDrawable(
                    this, drawableView
                )
            }
            3 -> {
                binding.tvOptionThree.background = ContextCompat.getDrawable(
                    this, drawableView
                )
            }
            4 -> {
                binding.tvOptionFour.background = ContextCompat.getDrawable(
                    this, drawableView
                )
            }
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOption: Int){
        defaultOptionsView()
        mySelectedOptionPos = selectedOption
        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(
            this,
            R.drawable.selected_option_bg
        )
    }
}