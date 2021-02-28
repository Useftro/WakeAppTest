package com.uniolco.wakeapptest.ui.lost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.uniolco.wakeapptest.R
import com.uniolco.wakeapptest.shareResult

class LostFragment : Fragment() {

    companion object {
        fun newInstance() = LostFragment()
    }

    private lateinit var viewModel: LostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.lost_fragment, container, false)
        val retryButton = view.findViewById<ImageButton>(R.id.retryButtonLost)
        val shareButton = view.findViewById<ImageButton>(R.id.shareButtonLost)
        retryButton.setOnClickListener {
            restart()
        }
        shareButton.setOnClickListener {
            shareResult("lost", view)
        }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LostViewModel::class.java)
    }

    private fun restart() {
        val navOptions = NavOptions.Builder().setPopUpTo(R.id.menuFragment, false).build()
        findNavController().navigate(R.id.menuFragment, null, navOptions)
    }

}