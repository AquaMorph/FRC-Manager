package com.aquamorph.frcmanager.fragments.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Generic slide fragment that can be passed a layout.
 *
 * @author Christian Colglazier
 * @version 3/31/2018
 */
class Slide : Fragment() {
    private var layoutResId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && requireArguments().containsKey(ARG_LAYOUT_RES_ID)) {
            layoutResId = requireArguments().getInt(ARG_LAYOUT_RES_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutResId, container, false)
    }

    companion object {

        private const val ARG_LAYOUT_RES_ID = "layoutResId"

        /**
         * newInstance() creates a new slide.
         *
         * @return slide
         */
        fun newInstance(layoutResId: Int): Slide {
            val slide = Slide()
            val args = Bundle()
            args.putInt(ARG_LAYOUT_RES_ID, layoutResId)
            slide.arguments = args
            return slide
        }
    }
}
