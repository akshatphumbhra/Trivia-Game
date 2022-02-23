package hu.ait.trivagame

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import hu.ait.trivagame.databinding.ActivityInputBinding
import java.util.*

class InputActivity : AppCompatActivity() {

    lateinit var binding: ActivityInputBinding
    private var selectedCategory: String = "General Knowledge"
    private var selectedDifficulty: String = "Easy"
    private var numQuestions: Int = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        var username = intent.getStringExtra("USER_NAME")

        val categories = resources.getStringArray(R.array.categories_array)
        val difficulty = resources.getStringArray(R.array.difficulty_array)

        binding.etNumQuestions.filters = arrayOf<InputFilter>(MinMaxFilter(1, 50))

        val categoriesAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, categories)
        val difficultyAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, difficulty)

        binding.spinnerCategories.adapter = categoriesAdapter
        binding.spinnerDifficulty.adapter = difficultyAdapter

        binding.spinnerCategories.setSelection(0)
        binding.spinnerDifficulty.setSelection(0)

        binding.btnStart.setOnClickListener {
            if (binding.etNumQuestions.text.isEmpty()) {
                binding.etNumQuestions.setError(getString(R.string.empty_error));
            } else {
                selectedCategory = binding.spinnerCategories.selectedItem.toString()
                selectedDifficulty = binding.spinnerDifficulty.selectedItem.toString()
                numQuestions = binding.etNumQuestions.text.toString().toInt()
                val intent = Intent(this, QuestionsActivity::class.java)
                intent.putExtra("CATEGORY", getCategoryID(selectedCategory))
                intent.putExtra("DIFFICULTY", selectedDifficulty.lowercase(Locale.getDefault()))
                intent.putExtra("USER_NAME", username)
                intent.putExtra("NUM_QUESTIONS", numQuestions)
                startActivity(intent)
            }
        }

    }

    inner class MinMaxFilter() : InputFilter {
        private var intMin: Int = 0
        private var intMax: Int = 0

        // Initialized
        constructor(minValue: Int, maxValue: Int) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        // Check if input c is in between min a and max b and
        // returns corresponding boolean
        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }

    private fun getCategoryID(category: String) : Int {
        var id = 0
        when(category) {
                "General Knowledge" -> id = 9
                "Entertainment: Books" -> id = 10
                "Entertainment: Film"-> id = 11
                "Entertainment: Music"-> id = 12
                "Entertainment: Musicals and Theatres"-> id = 13
                "Entertainment: Television"-> id = 14
                "Entertainment: Video Games"-> id = 15
                "Entertainment: Board Games"-> id = 16
                "Science and Nature"-> id = 17
                "Science: Computers"-> id = 18
                "Science: Mathematics"-> id = 19
                "Mythology"-> id = 20
                "Sports"-> id = 21
                "Geography"-> id = 22
                "History"-> id = 23
                "Politics"-> id = 24
                "Art"-> id = 25
                "Celebrities"-> id = 26
                "Animals"-> id = 27
                "Vehicles"-> id = 28
                "Entertainment: Comics"-> id = 29
                "Science: Gadgets"-> id = 30
                "Entertainment: Japanese Anime and Manga"-> id = 31
                "Entertainment: Cartoon and Animations"-> id = 32
                else -> id = 0
        }
        return id
    }
}