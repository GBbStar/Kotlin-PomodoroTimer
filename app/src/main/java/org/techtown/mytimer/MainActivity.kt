package org.techtown.mytimer

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private val minuteTextView:TextView by lazy{
        findViewById<TextView>(R.id.minuteTextView)
    }

    private val secondTextView:TextView by lazy{
        findViewById<TextView>(R.id.secondTextView)
    }

    private val timerSeekBar:SeekBar by lazy {
        findViewById<SeekBar>(R.id.timerSeekbar)
    }

    @SuppressLint("NewApi")
    private val soundPool = SoundPool.Builder().build()

    private var currentTimer: CountDownTimer? = null
    private var tickingSoundID:Int? = null
    private var bellSoundID:Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()
    }
    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
    private fun bindViews(){
        timerSeekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2) {
                        updateTime(p1 * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    stopTimer()
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    timerSeekBar ?: return

                    if (timerSeekBar.progress == 0) {
                        stopTimer()
                    } else {
                        startTimer()
                    }
                }
            }
        )
    }

    private fun initSounds(){
        tickingSoundID = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundID = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun startTimer(){
        currentTimer = createTimer(timerSeekBar.progress*60*1000L)
        currentTimer?.start()

        tickingSoundID?.let{ soundId->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun stopTimer(){
        currentTimer?.cancel()
        currentTimer = null

        soundPool.autoPause()
    }

    private fun createTimer(initialMillis: Long)=
        object : CountDownTimer(initialMillis, 1000L){
            override fun onTick(p0: Long) {
                updateTime(p0)
                updateSeekbar(p0)
            }

            override fun onFinish() {
                completeTimer()
                Toast.makeText(applicationContext,"타이머 종료", Toast.LENGTH_SHORT).show()
            }
        }


    private fun updateTime(remainMillis:Long){
        minuteTextView.text = "%02d'".format(remainMillis/1000/60)
        secondTextView.text = "%02d".format(remainMillis/1000%60)
    }

    private fun updateSeekbar(remainMillis:Long){
        timerSeekBar.progress = (remainMillis/1000/60).toInt()
    }

    private fun completeTimer(){
        updateTime(0)
        updateSeekbar(0)

        soundPool.autoPause()
        bellSoundID?.let{ soundId ->
            soundPool.play(soundId, 1F, 1F, 0, 0, 1F)
        }
    }
}