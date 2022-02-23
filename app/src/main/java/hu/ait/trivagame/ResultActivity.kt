package hu.ait.trivagame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import hu.ait.trivagame.databinding.ActivityMainBinding
import hu.ait.trivagame.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    lateinit var binding : ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        val username = intent.getStringExtra("USER_NAME")
        binding.tvName.text = username
        val score = intent.getIntExtra("SCORE", 0)
        val numQuestions = intent.getIntExtra("NUM_QUESTIONS", 0)
        binding.tvScore.text = getString(R.string.score_string, score, numQuestions)

        binding.btnFinish.setOnClickListener {
            finish()
        }
    }
}