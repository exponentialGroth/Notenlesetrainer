package com.exponential_groth.notenlesetrainer.game

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import com.exponential_groth.notenlesetrainer.home.*
import com.exponential_groth.notenlesetrainer.util.OnFinishedListener


class GameActivity : Activity() {
    private var key = 0
    private var difficulty = 0
    private var minTone = 1
    private var maxTone = 88
    private var isPractice = false
    private var withRhythm = false
    private var rhythmlessAnimSpeed = 0.2f

    private var gameView: GameView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(1) {
                gameView?.onFinishedListener?.finish()
            }
        }

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars())
                hide(WindowInsets.Type.navigationBars())
                hide(WindowInsets.Type.displayCutout())
            }
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE
        }

        val dimensions  =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            val insets = metrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars())
            val bounds = metrics.bounds
            Pair(bounds.width() - insets.left - insets.right, bounds.height() - insets.top - insets.bottom)
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }

        intent.extras?.also {
            key = it.getInt(EXTRA_KEY_KEY)
            difficulty = it.getInt(EXTRA_KEY_DIFFICULTY)
            minTone = it.getInt(EXTRA_KEY_MIN_TONE)
            maxTone = it.getInt(EXTRA_KEY_MAX_TONE)
            isPractice = it.getBoolean(EXTRA_KEY_IS_PRACTICE)
            withRhythm = it.getBoolean(EXTRA_KEY_IS_WITH_RHYTHM)
            rhythmlessAnimSpeed = it.getInt(EXTRA_KEY_RHYTHMLESS_ANIM_SPEED, 20) * 0.01f
        }

        gameView = (if (!withRhythm && isPractice) {
            RhythmlessPracticeGameView(this, dimensions, minTone, maxTone, key, difficulty, FPS, rhythmlessAnimSpeed)
        } else if (!withRhythm) {
            RhythmlessGameView(this, dimensions, minTone, maxTone, key, difficulty, FPS, rhythmlessAnimSpeed)
        } else if (isPractice) {
            PracticeGameView(this, dimensions, minTone, maxTone, key, difficulty, FPS)
        } else {
            RankedGameView(this, dimensions, minTone, maxTone, key, difficulty, FPS)
        }).also {
            it.onFinishedListener = getOnFinishGame(it)
        }
        setContentView(gameView!!)
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            gameView?.onFinishedListener?.finish()
        }
        super.onBackPressed()
    }


    private fun putExtras(intent: Intent, gV: GameView) {
        intent.putExtra(EXTRA_KEY_NOTES, gV.getPoints())
        intent.putExtra(EXTRA_KEY_KEY, key)
        intent.putExtra(EXTRA_KEY_DIFFICULTY, difficulty)
        intent.putExtra(EXTRA_KEY_MIN_TONE, minTone)
        intent.putExtra(EXTRA_KEY_MAX_TONE, maxTone)
        intent.putExtra(EXTRA_KEY_IS_PRACTICE, isPractice)
        intent.putExtra(EXTRA_KEY_IS_WITH_RHYTHM, withRhythm)
    }

    private fun getOnFinishGame(gV: GameView) = OnFinishedListener {
        val intent = Intent(gV.context, MainActivity::class.java)
        putExtras(intent, gV)
        startActivity(intent)
    }
}