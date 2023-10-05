package br.com.fiapcorrida

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import br.com.fiapcorrida.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Random

class MainActivity: AppCompatActivity() {

    // Core
    lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<HorseRaceViewModel>()
    private var coroutine: Job? = null

    // Constants
    private val RACE_DURATION = 60

    // Services
    private val random = Random()
    private val raceUtils = UtilsMethods()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        raceUtils.resetProgressBars(binding)
        setupButtons()
        setObservers()
    }

    private fun setupButtons() {
        binding.startRaceButton.setOnClickListener {
            startRace()
        }

        binding.stopRaceButton.setOnClickListener {
            raceUtils.stopRace(coroutine, binding)
        }
    }

    private fun setObservers() {
        viewModel.corridaIsFinished.observe(this, Observer { raceNumber ->
            Snackbar.make(
                binding.startRaceButton,
                "$raceNumber",
                Snackbar.LENGTH_SHORT
            ).show()
        })
    }

    private fun startRace() {
        raceUtils.resetWinners(binding)
        raceUtils.resetRaceState(coroutine, binding)
        raceUtils.enableStopRaceButton(binding)

        coroutine = lifecycleScope.launch(Dispatchers.Main) {
            raceUtils.disableStartRaceButton(binding)

            val raceEndTime = raceUtils.calculateRaceEndTime(RACE_DURATION)

            runRaceUntil(raceEndTime)

            raceUtils.enableStartRaceButton(binding)
        }
    }

    private suspend fun runRaceUntil(endTime: Long) {
        while (System.currentTimeMillis() < endTime) {
//            val hourse1ProgressRemaing = 100 - binding.horse1Progress.progress
//            val hourse2ProgressRemaing = 100 - binding.horse2Progress.progress
//
//            if (hourse1ProgressRemaing <= 20 && hourse2ProgressRemaing > 20) {
//                horse1Progress = 100
//            }

            val horse1Progress = binding.horse1Progress.progress + random.nextInt(20)
            val horse2Progress = binding.horse2Progress.progress + random.nextInt(20)


            if (horse1Progress >= 100) {
                raceUtils.showWinner(getString(R.string.winner1), binding.winner1, binding)
                return
            } else if (horse2Progress >= 100) {
                raceUtils.showWinner(getString(R.string.winner2), binding.winner2, binding)
                return
            }

            updateHorseProgress(horse1Progress, horse2Progress)

            delay(1500)
        }
    }

    private fun updateHorseProgress(horse1Progress: Int, horse2Progress: Int) {
        binding.horse1Progress.progress = horse1Progress.coerceAtMost(100)
        binding.horse2Progress.progress = horse2Progress.coerceAtMost(100)
    }

}