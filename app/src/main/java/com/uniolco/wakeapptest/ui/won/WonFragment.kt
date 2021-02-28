package com.uniolco.wakeapptest.ui.won

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.uniolco.wakeapptest.R
import com.uniolco.wakeapptest.shareResult

class WonFragment : Fragment() {

    companion object {
        fun newInstance() = WonFragment()
    }

    private lateinit var viewModel: WonViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.won_fragment, container, false)
        val retryButton = view.findViewById<ImageButton>(R.id.retryButtonLost)
        val shareButton = view.findViewById<ImageButton>(R.id.shareButtonLost)
        retryButton.setOnClickListener {
            restart()
        }
        shareButton.setOnClickListener {
            shareResult("won", view)
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WonViewModel::class.java)
    }

    private fun restart() {
        val navOptions = NavOptions.Builder().setPopUpTo(R.id.menuFragment, false).build()
        findNavController().navigate(R.id.menuFragment, null, navOptions)
    }

}