package com.exponential_groth.notenlesetrainer.home

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.graphics.drawable.toBitmap
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.exponential_groth.notenlesetrainer.R
import com.exponential_groth.notenlesetrainer.SettingsActivity
import com.exponential_groth.notenlesetrainer.game.GameActivity
import com.exponential_groth.notenlesetrainer.home.objects.circleoffifths.CircleOfFifthsAdapter
import com.exponential_groth.notenlesetrainer.home.objects.circleoffifths.CircleOfFifthsItem
import com.exponential_groth.notenlesetrainer.home.objects.circleoffifths.CircleOfFifthsLayoutManager
import com.exponential_groth.notenlesetrainer.home.objects.circleoffifths.PROPORTION
import com.exponential_groth.notenlesetrainer.home.objects.keyboardview.KeyboardView
import com.exponential_groth.notenlesetrainer.util.*
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MainActivity: AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels { Injector.provideMainViewModelFactory(applicationContext) }

    private var screenWidth = 0
    private var level = 1
    private var key = 0
    private val minTone get() = keyBoardSlider.values[0].toInt()
    private val maxTone get() = keyBoardSlider.values[1].toInt()

    private lateinit var playBtn: ImageButton
    private lateinit var practiceBtn: ImageButton
    private lateinit var settingsBtn: AppCompatImageButton
    private lateinit var keyRV: RecyclerView
    private lateinit var levelPicker: Slider
    private lateinit var levelTitleTV: TextView
    private lateinit var levelDescriptionTV: TextView
    private lateinit var highScoreValueTV: TextView
    private lateinit var highScoreDateTV: TextView
    private lateinit var keyboardView: KeyboardView
    private lateinit var keyBoardSlider: RangeSlider
    private lateinit var rhythmSwitch: MaterialSwitch

    private lateinit var levelDescriptions: Array<String>
    private val fifths = mutableListOf<CircleOfFifthsItem>()
    private val circleOfFifthsAdapter = CircleOfFifthsAdapter(fifths)

    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        levelDescriptions = resources.getStringArray(R.array.level_descriptions)
        viewModel.loadHighScores()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {}

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            with (window.insetsController!!) {
                hide(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
/*                hide(WindowInsets.Type.navigationBars())
                hide(WindowInsets.Type.displayCutout())*/
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
//            val insets = metrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.displayCutout())
            val bounds = metrics.bounds
            Pair(bounds.width()/* - insets.left - insets.right*/, bounds.height()/* - insets.top - insets.bottom*/)
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
        screenWidth = dimensions.first

        val shouldSetUp = fifths.isEmpty()
        if (shouldSetUp) {
            keyRV = findViewById(R.id.home_key_rv)
            playBtn = findViewById(R.id.home_play_btn)
            practiceBtn = findViewById(R.id.home_practice_btn)
            settingsBtn = findViewById(R.id.home_settings_btn)
            levelPicker = findViewById(R.id.home_level_picker)
            levelTitleTV = findViewById(R.id.home_level_title_tv)
            levelDescriptionTV = findViewById(R.id.home_level_description_tv)
            highScoreValueTV = findViewById(R.id.home_high_score_notes_played_tv)
            highScoreDateTV = findViewById(R.id.home_high_score_date_tv)
            keyboardView = findViewById(R.id.home_keyboard)
            keyBoardSlider = findViewById(R.id.home_keyboard_slider)
            rhythmSwitch = findViewById(R.id.home_rhythm_switch)
            setUpUI()
        }

        intent.extras?.let {
            key = it.getInt(EXTRA_KEY_KEY)
            level = it.getInt(EXTRA_KEY_DIFFICULTY, 1)
            val leftTone = it.getInt(EXTRA_KEY_MIN_TONE)
            val rightTone = it.getInt(EXTRA_KEY_MAX_TONE)

            keyRV.scrollToPosition(key + 6)
            levelPicker.value = level.toFloat()
            levelDescriptionTV.text = levelDescriptions[level-1]
            keyBoardSlider.setValues(leftTone.toFloat(), rightTone.toFloat())
            keyboardView.key = key
            keyboardView.noAdditionalAccidentals = level == 1
            keyboardView.updateSelectedKeys(leftTone, rightTone)
            rhythmSwitch.isChecked = it.getBoolean(EXTRA_KEY_IS_WITH_RHYTHM)

            val wasPractice = it.getBoolean(EXTRA_KEY_IS_PRACTICE)
            if (!wasPractice) {
                viewModel.updateHighScore(
                    it.getInt(EXTRA_KEY_NOTES),
                    key,
                    level,
                    leftTone,
                    rightTone,
                    rhythmSwitch.isChecked
                )
            }

        }?: run { keyRV.scrollToPosition(6) }

        observe()
    }

    private fun setUpUI() {
        val startListener = OnClickListener {
            val intent = Intent(this, GameActivity::class.java)
                .putExtra(EXTRA_KEY_KEY, key)
                .putExtra(EXTRA_KEY_DIFFICULTY, level)
                .putExtra(EXTRA_KEY_MIN_TONE, minTone)
                .putExtra(EXTRA_KEY_MAX_TONE, maxTone)
                .putExtra(EXTRA_KEY_IS_PRACTICE, it.id != R.id.home_play_btn)
                .putExtra(EXTRA_KEY_IS_WITH_RHYTHM, rhythmSwitch.isChecked)
            if (!rhythmSwitch.isChecked) intent.putExtra(EXTRA_KEY_RHYTHMLESS_ANIM_SPEED, sharedPreferences.getInt("rhythmless_animation_speed", 20))
            startActivity(intent)
        }
        playBtn.setOnClickListener(startListener)
        practiceBtn.setOnClickListener(startListener)

        rhythmSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            updateHighScoreView()
        }

        settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        levelTitleTV.text = getString(R.string.level, level)
        levelPicker.addOnChangeListener { slider, value, fromUser ->
            level = value.toInt()
            levelTitleTV.text = getString(R.string.level, level)
            levelDescriptionTV.text = levelDescriptions[value.toInt()-1]
            updateHighScoreView()
            keyboardView.noAdditionalAccidentals = value <= 1
        }


//        TODO("maybe valueTo=52, no steps for black keys")
        keyBoardSlider.addOnChangeListener { slider, value, fromUser ->
            slider.values.let {values ->
                if (value.toInt().toKeyColor() == KeyColor.BLACK) {
                    values.indexOfFirst { it != value }.takeUnless { it == -1 }?.let {
                        if (slider.values[it] > value) {
                            slider.values[it] = value + 1
                        } else {
                            slider.values[it] = value - 1
                        }
                    }
                } else {
                    keyboardView.updateSelectedKeys(values[0].toInt(), values[1].toInt())
                    updateHighScoreView()
                }

            }
        }

        circleOfFifthsAdapter.recyclerView = keyRV
        keyRV.adapter = circleOfFifthsAdapter
        (keyRV.layoutManager as CircleOfFifthsLayoutManager).onItemSelectedListener = OnItemSelectedListener {pos ->
            key = pos - 6
            updateHighScoreView()
            keyboardView.key = key
        }
        LinearSnapHelper().attachToRecyclerView(keyRV)

        val drawables = listOf(
            R.drawable.key__6,
            R.drawable.key__5,
            R.drawable.key__4,
            R.drawable.key__3,
            R.drawable.key__2,
            R.drawable.key__1,
            R.drawable.key_0,
            R.drawable.key_1,
            R.drawable.key_2,
            R.drawable.key_3,
            R.drawable.key_4,
            R.drawable.key_5,
            R.drawable.key_6,
        ).map {
            AppCompatResources.getDrawable(this, it)!!
                .toBitmap(screenWidth/5, (screenWidth.toFloat() / 5 / PROPORTION).toInt())
        }

        fifths.addAll(drawables.map { CircleOfFifthsItem(it) })
        circleOfFifthsAdapter.notifyItemRangeInserted(0, 13)
        keyRV.scrollToPosition(key + 6)
    }


    private fun observe() {
        viewModel.highScores.observe(this) {
            updateHighScoreView()
        }
    }

    private fun updateHighScoreView() {
        viewModel.highScores.value?.find {
            it.hsKey == key && it.hsLevel == level && it.hsMinTone == minTone && it.hsMaxTone == maxTone && it.hsIsWithRhythm == rhythmSwitch.isChecked
        }?.let {
            highScoreValueTV.text = it.hsNotesPlayed.toString()
            highScoreDateTV.text = it.hsDate.toLocalDate().format(dateFormatter)
        }?: run {
            highScoreValueTV.text = "0"
            highScoreDateTV.text = ""
        }
    }
}
