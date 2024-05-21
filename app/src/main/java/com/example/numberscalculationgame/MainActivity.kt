package com.example.numberscalculationgame

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.numberscalculationgame.databinding.ActivityMainBinding
import com.example.numberscalculationgame.databinding.DialogResultBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity()
{
    private lateinit var binding : ActivityMainBinding
    private var isPlayed = false
    private var firstRandomNumber : Int ?= null
    private var secondRandomNumber : Int ?= null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views and set click listeners
        binding.apply {
            btnStartOrNext.setOnClickListener {
                // Next Question
                if (isPlayed)
                {
                    //Next Question
                    getRandomNumbers()
                    tvScore.text = (tvScore.text.toString().toInt()-1).toString()
                }else
                {
                    //start the Game
                    isPlayed = true
                    btnStartOrNext.text = "Next!"
                    cardQuestion.visibility = View.VISIBLE
                    cardScore.visibility = View.VISIBLE
                    getRandomNumbers()
                    runTimer()


                }

            }
            etAnswer.addTextChangedListener {
                val answer = firstRandomNumber!! + secondRandomNumber!!
                if (!it.isNullOrEmpty() && it.toString().toInt() == answer)
                {
                    //answer is correct
                    tvScore.text = (tvScore.text.toString().toInt()+1).toString()
                    etAnswer.setText("")
                    getRandomNumbers()
                }
            }
        }
    }

    // Function to run the timer for each question
    private fun runTimer() {
        lifecycleScope.launch(Dispatchers.IO)
        {
            (1..29).asFlow().onStart {

                // Start animation when timer starts
                binding.constraintLayout.transitionToEnd()

            }.onCompletion {
                // Game finished. Show dialog to user

               runOnUiThread {
                   binding.cardQuestion.visibility = View.GONE
                   val dialogBinding = DialogResultBinding.inflate(layoutInflater)
                   val dialog = Dialog(this@MainActivity)
                   dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                   dialog.setContentView(dialogBinding.root)
                   dialog.setCancelable(false)
                   dialog.show()
                   dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT)
                   dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                   // Click listeners for dialog buttons
                   dialogBinding.apply {
                       //show data in dialog
                       tvDialogScore.text = binding.tvScore.text
                       btnClose.setOnClickListener {
                           dialog.dismiss()
                           finish()
                       }
                       btnTryAgain.setOnClickListener {
                           dialog.dismiss()
                           binding.apply {
                               btnStartOrNext.text = getString(R.string.start_game)
                               cardQuestion.visibility = View.GONE
                               cardScore.visibility = View.GONE
                               isPlayed = false
                               constraintLayout.setTransition(R.id.start,R.id.end)
                               constraintLayout.transitionToEnd()
                               tvScore.text = "0"
                           }
                       }
                   }


               }

            }.collect{ delay(1000) }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getRandomNumbers()
    {
        // Generate random numbers for the question
        firstRandomNumber = Random.nextInt(2,99)
        secondRandomNumber = Random.nextInt(2,99)
        binding.tvQuestionNumber.text = "$firstRandomNumber + $secondRandomNumber"
    }
}