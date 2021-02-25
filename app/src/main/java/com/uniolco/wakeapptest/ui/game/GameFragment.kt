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
import androidx.lifecycle.ViewModelProvider
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

    private val idOfComet = 255

    private val xSize: Int = 7
    private val ySize = 10

    private fun createComet(){
        cometImageView = ImageView(context)
        cometImageView.id = idOfComet
        cometImageView.maxHeight = widthOfBlock/2
        cometImageView.maxWidth = widthOfBlock/2
        cometImageView.setImageResource(comet)
        cometImageView.alpha = 0.8F
        val index = (0 until numberOfBlocks*numberOfBlocks).random()
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

        for (i in 0 until numberOfBlocks.toDouble().pow(2.0).toInt()){
            val imageView = ImageView(context)
            imageView.id = i
            imageView.layoutParams = ViewGroup.LayoutParams(widthOfBlock, widthOfBlock)
            imageView.maxHeight = widthOfBlock
            imageView.maxWidth = widthOfBlock
            imageView.setImageResource(tile)
            imageView.alpha = 0.1F
            gridLayout.addView(imageView)
        }

        createComet()
        generatePlayingTiles()

    }

    private fun generatePlayingTiles() {
        val comet = gridLayout.findViewById<ImageView>(idOfComet)
        val playingFieldSize = (numberOfBlocks*3 + 4..numberOfBlocks*4 + 3).random()
        var ind = gridLayout.indexOfChild(comet)
        for (i in 0..playingFieldSize){
            val direct = setOf(-1, 1, numberOfBlocks, -numberOfBlocks).random()
            when (direct){
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

        val index = gridLayout.indexOfChild(cometImageView)

        when (direction){
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
                if (index + numberOfBlocks < (numberOfBlocks * numberOfBlocks)){ // checking bottom border
                    movingComet(index, numberOfBlocks, addition = -1)
                }
            }
            4 -> {
                if ((index) % numberOfBlocks != 0) { // checking left borders
                    movingComet(index, -1)
                }
            }
        }

        /*if (direction == 1) { // up
            if (index - numberOfBlocks >= 0) {
                emptyImageView = gridLayout[index - numberOfBlocks] as ImageView
                movingComet(emptyImageView, index, -numberOfBlocks)
            }
        }
        else if (direction == 2) { // right
            if ((index + 1) % numberOfBlocks != 0) { // checking right borders
                emptyImageView = gridLayout[index + 1] as ImageView
                movingComet(emptyImageView, index, 1, addition = -1)
            }


        }
        else if (direction == 3) { // down
            if (index + numberOfBlocks < (numberOfBlocks * numberOfBlocks)){
                emptyImageView = gridLayout[index + numberOfBlocks] as ImageView
                movingComet(emptyImageView, index, numberOfBlocks, addition = -1)
            }

        }
        // Можно объединить все методы, если всем им назначить основным действием +, а передавать
        // аргументом значения с разными знаками, т.е. к примеру, при down передавать аргументом + numberOfBlocks-1
        else if (direction == 4) { // left
            Log.d("LEFT", "INDEX $index")
            if ((index) % numberOfBlocks != 0) { // checking left borders
                emptyImageView = gridLayout[index - 1] as ImageView
                movingComet(emptyImageView, index, -1)
            }
        }*/
        checkIfWin()
    }

    private fun checkIfWin(){
        var winCondition: Boolean = true
        for (i in 0 until numberOfBlocks.toDouble().pow(2.0).toInt()){
            if (gridLayout[i].alpha == 1F){
                Log.d("TILE", "number: $i, alpha: ${gridLayout[i].alpha}")
                winCondition = false
                break
            }
            else {
                winCondition = true
            }
        }
        if (winCondition) {
            win()
        }
        Log.d("WIN_CONDITION", "$winCondition")
    }

    private fun movingComet(index: Int, part: Int, addition: Int = 0){
        val emptyImageView = gridLayout[index + part] as ImageView
        if (emptyImageView.alpha == 0.3F || emptyImageView.alpha == 0.1F){ // or 0.1F
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
        Log.d("RESULT", "LOST")
    }

    private fun win(){
        Log.d("RESULT", "WON")
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        createGrid()
        listenerForLayout()

/*        ObjectAnimator.ofFloat(cometImageView, "translationX", widthOfBlock.toFloat()).apply {
            duration = 3000
            start()
            doOnEnd {  }
        }*/

        fixedRateTimer("timer", false, 0, 4000){
            requireActivity().runOnUiThread {
                run {
                    Thread.sleep(100)
                    val mLock = ReentrantLock()
                    synchronized (lock = mLock) {
                        ObjectAnimator.ofFloat(cometImageView, "translationX", widthOfBlock.toFloat()).apply {
                            duration = 3000
                            start()
                            doOnEnd {
                                moveComet(2)
                                ObjectAnimator.ofFloat(cometImageView, "translationX", 0F).apply {
                                    duration = 0
                                    start()
                                }
                            }
                        }
                    }
                }
            }

        }

//        fixedTimer = createTimer()

    }

    private fun createTimer(): Timer {
        val fixedTim = fixedRateTimer("timer",false,0,6000){
            requireActivity().runOnUiThread {

                val xOrY = listOf("translationX", "translationY").random()
                var direction: Int
                direction = if (xOrY == "translationX") {
                    listOf(-1, 1).random()
                } else {
                    listOf(-numberOfBlocks, numberOfBlocks).random()
                }

                Log.d("DIRECTION", "xOrY: $xOrY ,direction: $direction")

                var width = if (direction > 0){
                    widthOfBlock
                } else {
                    -widthOfBlock
                }

                ObjectAnimator.ofFloat(cometImageView, xOrY, width.toFloat())
                    .apply {
                        duration = 5000
                        start()
                    }

            }
        }
        return fixedTim
    }

    private fun listenerForLayout(){
        with(constraintLayout) {
            setOnTouchListener(object: OnSwipeTouchListener(requireContext()) {
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