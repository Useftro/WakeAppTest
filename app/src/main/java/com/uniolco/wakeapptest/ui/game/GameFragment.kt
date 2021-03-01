package com.uniolco.wakeapptest.ui.game

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.uniolco.wakeapptest.OnSwipeTouchListener
import com.uniolco.wakeapptest.R
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.fixedRateTimer
import kotlin.math.pow

class GameFragment : Fragment() {

    private val tile: Int = R.drawable.tile
    private val comet: Int = R.drawable.cometstatic
    private var widthOfBlock = 0
    private val numberOfBlocks = 6 // size of field (6x6, 8x8, etc.)

    //    private val numberOfBlocksHorizontal = 6
//    private val numberOfBlocksVertical = 8
    private var widthOfScreen: Int = 0
    private var heightOfScreen: Int = 0
    private lateinit var cometImageView: ImageView

    private lateinit var gridLayout: androidx.gridlayout.widget.GridLayout
    private lateinit var constraintLayout: ConstraintLayout

    lateinit var fixedTimer: Timer
    lateinit var objAnimatorOnStart: ObjectAnimator
    lateinit var objAnimatorOnFinish: ObjectAnimator

    private val idOfComet = 255


    private fun createComet() {
        cometImageView = ImageView(context)
        cometImageView.id = idOfComet
        cometImageView.maxHeight = widthOfBlock / 2
        cometImageView.maxWidth = widthOfBlock / 2
        cometImageView.setImageResource(comet)
        cometImageView.alpha = 0.8F
        val index = (0 until numberOfBlocks * numberOfBlocks).random()
        gridLayout.addView(cometImageView, index)

    }

    private fun optionsForGrid() {
        gridLayout.rowCount = numberOfBlocks
        gridLayout.columnCount = numberOfBlocks
        gridLayout.layoutParams.height = widthOfScreen
        gridLayout.layoutParams.width = widthOfScreen
    }

    private fun createGrid() {

        optionsForGrid()

        for (i in 0 until numberOfBlocks.toDouble().pow(2.0).toInt()) {
            createTile(i)
        }

        createComet()
        generatePlayingTiles()

    }

    private fun createTile(i: Int) {
        val imageView = ImageView(context)
        imageView.id = i
        imageView.layoutParams = ViewGroup.LayoutParams(widthOfBlock, widthOfBlock)
        imageView.maxHeight = widthOfBlock
        imageView.maxWidth = widthOfBlock
        imageView.setImageResource(tile)
        imageView.alpha = 0.1F
        gridLayout.addView(imageView)
    }

    private fun generatePlayingTiles() {
        val comet = gridLayout.findViewById<ImageView>(idOfComet)
        val playingFieldSize = (numberOfBlocks * 3 + 4..numberOfBlocks * 4 + 3).random()
        var ind = gridLayout.indexOfChild(comet)
        for (i in 0..playingFieldSize) {
            val direct = setOf(-1, 1, numberOfBlocks, -numberOfBlocks).random()
            when (direct) {
                -1 -> {
                    if ((ind) % numberOfBlocks != 0) {
                        gridLayout[ind - 1].alpha = 1F
                        ind += -1
                    }
                }
                1 -> {
                    if ((ind + 1) % numberOfBlocks != 0) {
                        gridLayout[ind + 1].alpha = 1F
                        ind += 1
                    }
                }
                numberOfBlocks -> {
                    if (ind + numberOfBlocks < (numberOfBlocks * numberOfBlocks)) {
                        gridLayout[ind + numberOfBlocks].alpha = 1F
                        ind += numberOfBlocks
                    }
                }
                -numberOfBlocks -> {
                    if (ind - numberOfBlocks >= 0) {
                        gridLayout[ind - numberOfBlocks].alpha = 1F
                        ind += -numberOfBlocks
                    }
                }
            }
        }
    }

    private fun moveComet(direction: Int) { // 1 is up, 2 is right, 3 is bottom, 4 is left
        finishTimer()

        val index = gridLayout.indexOfChild(cometImageView)
        when (direction) {
            1 -> {
                if (index - numberOfBlocks >= 0) { // checking upper border
                    movingComet(index, -numberOfBlocks)
                }
            }
            2 -> {
                if ((index + 1) % numberOfBlocks != 0) { // checking right borders
                    movingComet(index, 1, addition = -1)
                }
            }
            3 -> {
                if (index + numberOfBlocks < (numberOfBlocks * numberOfBlocks)) { // checking bottom border
                    movingComet(index, numberOfBlocks, addition = -1)
                }
            }
            4 -> {
                if ((index) % numberOfBlocks != 0) { // checking left borders
                    movingComet(index, -1)
                }
            }
        }

        checkIfWin()
        fixedTimer = createTimer()
    }

    private fun checkIfWin() {
        var winCondition: Boolean = true
        for (i in 0 until numberOfBlocks.toDouble().pow(2.0).toInt()) {
            if (gridLayout[i].alpha == 1F) {
                Log.d("TILE", "number: $i, alpha: ${gridLayout[i].alpha}")
                winCondition = false
                break
            } else {
                winCondition = true
            }
        }
        if (winCondition) {
            win()
        }
        Log.d("WIN_CONDITION", "$winCondition")
    }

    private fun movingComet(index: Int, part: Int, addition: Int = 0) {
        val emptyImageView = gridLayout[index + part] as ImageView
        if (emptyImageView.alpha == 0.3F || emptyImageView.alpha == 0.1F) { // or 0.1F
            lose()
        }
        emptyImageView.alpha = 0.3F
        gridLayout.removeViewAt(index + part)
        gridLayout.removeView(cometImageView)
        gridLayout.addView(cometImageView, index + part + addition)
        cometImageView.alpha = 0.8F
        gridLayout.addView(emptyImageView, index)
    }

    private fun lose() {
        val navOptions = NavOptions.Builder().setPopUpTo(R.id.menuFragment, true).build()
        findNavController().navigate(R.id.lostFragment, null, navOptions)
    }

    private fun win() {
        val navOptions = NavOptions.Builder().setPopUpTo(R.id.gameFragment, true).build()
        findNavController().navigate(R.id.wonFragment, null, navOptions)
    }

    companion object {
        fun newInstance() = GameFragment()
    }

    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.game_fragment, container, false)

        gridLayout = view.findViewById(R.id.gameField)
        constraintLayout = view.findViewById(R.id.gameLayout)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        widthOfScreen = displayMetrics.widthPixels
        heightOfScreen = displayMetrics.heightPixels
        widthOfBlock = widthOfScreen / numberOfBlocks
    }

    override fun onPause() {
        super.onPause()
        onDestroy()
    }

    override fun onStop() {
        super.onStop()
        onDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        finishTimer()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        createGrid()
        listenerForLayout()
        fixedTimer = createTimer()
    }

    private fun createTimer(): Timer {
        val fixedTim = fixedRateTimer("timer", false, 0, 4000) {
            if (isAdded) {
                requireActivity().runOnUiThread {
                    run {
                        Thread.sleep(150)

                        // randomly selecting if we are moving along X or Y axis
                        val xOrY = listOf("translationX", "translationY").random()
                        var direction: Int
                        direction = if (xOrY == "translationX") {
                            listOf(4, 2).random()
                        } else {
                            listOf(1, 3).random()
                        }

                        Log.d("DIRECTION", "xOrY: $xOrY ,direction: $direction")

                        // checking direction in which we are moving
                        var width = if (direction == 2 || direction == 3) {
                            widthOfBlock
                        } else {
                            -widthOfBlock
                        }

                        val mLock = ReentrantLock()
                        synchronized(lock = mLock) {
                            objAnimatorOnStart =
                                ObjectAnimator.ofFloat(cometImageView, xOrY, width.toFloat())
                                    .apply {
                                        duration = 3000
                                        start()
                                        doOnEnd {
                                            moveComet(direction)
                                            // playing animation to 0F to return image to it's location
                                            objAnimatorOnFinish =
                                                ObjectAnimator.ofFloat(cometImageView, xOrY, 0F)
                                                    .apply {
                                                        duration = 0
                                                        start()
                                                    }
                                        }
                                    }
                        }
                    }
                }
            }

        }
        return fixedTim
    }

    private fun finishTimer() {
        fixedTimer.cancel()
        objAnimatorOnStart.removeAllListeners()
        objAnimatorOnStart.cancel()
        if (::objAnimatorOnFinish.isInitialized) {
            objAnimatorOnFinish.removeAllListeners()
            objAnimatorOnFinish.end()
            objAnimatorOnFinish.cancel()
        }
    }

    private fun listenerForLayout() {
        with(constraintLayout) {
            setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
                override fun onSwipeLeft() {
                    moveComet(4)
                }

                override fun onSwipeRight() {
                    moveComet(2)
                }

                override fun onSwipeBottom() {
                    moveComet(3)
                }

                override fun onSwipeTop() {
                    moveComet(1)
                }
            })
        }

    }

}