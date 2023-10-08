package br.com.fiapcorrida

import android.view.TextureView
import android.view.View
import android.widget.TextView
import br.com.fiapcorrida.databinding.ActivityMainBinding
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.Job

class UtilsMethods() {

    fun resetRaceState(coroutine: Job?, binding: ActivityMainBinding) {
        resetProgressBars(binding)
        coroutine?.cancel()
    }

    fun enableStopRaceButton(binding: ActivityMainBinding) {
        binding.stopRaceButton.isEnabled = true
    }

    fun disableStopRaceButton(binding: ActivityMainBinding) {
        binding.stopRaceButton.isEnabled = false
    }

    fun disableStartRaceButton(binding: ActivityMainBinding) {
        binding.startRaceButton.isEnabled = false
    }

    fun enableStartRaceButton(binding: ActivityMainBinding) {
        binding.startRaceButton.isEnabled = true
    }

    fun calculateRaceEndTime(raceDuration: Int): Long {
        return System.currentTimeMillis() + raceDuration * 1000
    }

    fun stopRace(coroutine: Job?, binding: ActivityMainBinding) {
        getAndShowWinnerByProgress(binding)
        coroutine?.cancel()
        enableStartRaceButton(binding)
    }

    fun resetProgressBars(binding: ActivityMainBinding) {
        binding.horse1Progress.progress = 0
        binding.horse2Progress.progress = 0
    }

    fun resetWinners(binding: ActivityMainBinding) {
        binding.winner1.text = binding.winner1.context.getString(R.string.empty_string)
        binding.winner2.text = binding.winner2.context.getString(R.string.empty_string)
    }

    fun showWinner(mensagem: String, text: TextView, binding: ActivityMainBinding) {
        enableStopRaceButton(binding)
        text.text = mensagem
        text.visibility = View.VISIBLE
    }

    fun updateProgress(progressBar: LinearProgressIndicator, progress: Int) {
        progressBar.progress = progress
    }

    fun getAndShowWinnerByProgress(binding: ActivityMainBinding) {
        val horse1Progress = binding.horse1Progress.progress
        val horse2Progress = binding.horse2Progress.progress

        var winner = if (horse1Progress > horse2Progress) 1 else 2;
        if (winner == 1) {
            showWinner(binding.winner1.context.getString(R.string.winner1), binding.winner1, binding)
        } else {
            showWinner(binding.winner2.context.getString(R.string.winner2), binding.winner2, binding)
        }
    }

}