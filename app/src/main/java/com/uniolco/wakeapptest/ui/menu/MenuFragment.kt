package com.uniolco.wakeapptest.ui.menu

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.uniolco.wakeapptest.R

class MenuFragment : Fragment() {

    companion object {
        fun newInstance() = MenuFragment()
    }

    private lateinit var viewModel: MenuViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.menu_fragment, container, false)
        val playBtn: ImageButton = view.findViewById(R.id.playButton)
        playBtn.setOnClickListener {
            val navbuilder: NavOptions.Builder = NavOptions.Builder()
            Log.d("Biba", "BOBA")
            val navOptions = navbuilder.setPopUpTo(R.id.menuFragment, true).build()
            Navigation.findNavController(view).navigate(R.id.gameFragment, null)
        }


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)

    }

}