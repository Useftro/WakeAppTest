package com.uniolco.wakeapptest.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.uniolco.wakeapptest.R

class GameFragment : Fragment() {

    private val xSize: Int = 7
    private val ySize = 10

    private lateinit var grid: GridView

    private fun createGridArray(): MutableList<Int>{
        val tiles = mutableListOf<Int>()
        for (i in xSize downTo 0){
            for (j in ySize downTo 0){
                tiles.add((0..1).random())
            }
        }
        return tiles
    }

    companion object {
        fun newInstance() = GameFragment()
    }

    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        grid = requireView().findViewById(R.id.gameGrid)
        val tiles = createGridArray()
    }

}