package com.ayata.clad

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.ayata.clad.databinding.FragmentTestBinding


class TestFragment : Fragment() {


    private lateinit var binding:FragmentTestBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentTestBinding.inflate(inflater, container, false)

        setImageLoop()
        return binding.root
    }

    private var listDrawable= arrayListOf<Int>(R.drawable.ic_delete,
        R.drawable.ic_btn_speak_now,R.drawable.ic_dialog_alert,
        R.drawable.ic_input_get,R.drawable.ic_input_delete,
        R.drawable.ic_lock_lock,R.drawable.ic_dialog_info,
        R.drawable.ic_dialog_email,R.drawable.ic_lock_idle_charging,
        R.drawable.ic_lock_idle_alarm,R.drawable.ic_secure)

    private fun setImageLoop(){
        val animationFadeIn: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        val animationFadeOut: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)

        var count=0
        val animListener: AnimationListener = object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (animation == animationFadeIn) {
                    // Start fade-out animation
                 binding.imageLoop.startAnimation(animationFadeOut)

                } else if (animation == animationFadeOut) {
                    if (count < listDrawable.size-1) {
                        count++
                    } else {
                        count = 0
                    }
                    binding.imageLoop.setImageResource(listDrawable[count])
                    binding.imageLoop.startAnimation(animationFadeIn)
                }
            }
        }
        animationFadeOut.setAnimationListener(animListener)
        animationFadeIn.setAnimationListener(animListener)
        binding.imageLoop.setImageResource(listDrawable[0])
        binding.imageLoop.startAnimation(animationFadeIn)
    }

}