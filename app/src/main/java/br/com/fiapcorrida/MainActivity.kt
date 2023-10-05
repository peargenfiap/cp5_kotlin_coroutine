package br.com.fiapcorrida

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import kotlin.math.min
import kotlin.math.max

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
        val maxProgress = 100
        val winningThreshold = 20

        while (System.currentTimeMillis() < endTime) {
            val horse1Remaining = maxProgress - binding.horse1Progress.progress
            val horse2Remaining = maxProgress - binding.horse2Progress.progress

            if (horse1Remaining <= winningThreshold && horse1Remaining < horse2Remaining) {
                raceUtils.updateProgress(binding.horse1Progress, maxProgress)
                raceUtils.showWinner(getString(R.string.winner1), binding.winner1, binding)
                return
            } else if (horse2Remaining <= winningThreshold && horse2Remaining < horse1Remaining) {
                raceUtils.updateProgress(binding.horse2Progress, maxProgress)
                raceUtils.showWinner(getString(R.string.winner2), binding.winner2, binding)
                return
            } else {
                val horse1RandomProgress = binding.horse1Progress.progress + random.nextInt(20)
                val horse2RandomProgress = binding.horse2Progress.progress + random.nextInt(20)
                raceUtils.updateProgress(binding.horse1Progress, min(horse1RandomProgress, maxProgress))
                raceUtils.updateProgress(binding.horse2Progress, min(horse2RandomProgress, maxProgress))
            }

            delay(1500)
        }
    }

}