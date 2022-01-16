package com.ayata.clad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ayata.clad.databinding.FragmentTestBinding
import com.ayata.clad.product.FragmentProductDetail
import omari.hamza.storyview.StoryView
import omari.hamza.storyview.callback.StoryCallbacks
import omari.hamza.storyview.callback.StoryClickListeners
import omari.hamza.storyview.model.MyStory
import java.text.ParseException
import java.text.SimpleDateFormat


class TestFragment : Fragment() {


    private lateinit var binding:FragmentTestBinding

    private lateinit var storyBuilder:StoryView.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentTestBinding.inflate(inflater, container, false)

        setImageLoop()

        binding.btnStory.setOnClickListener {
            showStories()
        }
        return binding.root
    }

    private var listDrawable= arrayListOf<Int>(R.drawable.brand_aamayra,
        R.drawable.brand_aroan,R.drawable.brand_bishrom,
        R.drawable.brand_caliber,R.drawable.brand_creative_touch,
        R.drawable.brand_fibro,R.drawable.brand_fuloo,
        R.drawable.brand_gofi,R.drawable.brand_goldstar,
        R.drawable.brand_hillsandclouds,R.drawable.brand_jujuwears,
        R.drawable.brand_kasa,R.drawable.brand_ktm_city,R.drawable.brand_logo,
        R.drawable.brand_mode23,R.drawable.brand_newmew,
        R.drawable.brand_phalanoluga,R.drawable.brand_sabah,
        R.drawable.brand_station,R.drawable.brand_tsarmoire)

    private fun setImageLoop(){
        val animationFadeIn: Animation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
        val animationFadeOut: Animation = AnimationUtils.loadAnimation(requireContext(),  android.R.anim.fade_out)

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


    private fun showStories() {
        val myStories = ArrayList<MyStory>()
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
        try {
            val story1 = MyStory(
            "https://image.made-in-china.com/202f0j00gqjRIDFdribc/Autumn-and-Winter-Hand-Made-Double-Sided-Woolen-Cashmere-Ladies-Wool-Coat.jpg",
                simpleDateFormat.parse("20-10-2019 10:00:00")
            )
            myStories.add(story1)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        try {
            val story2 = MyStory(
                "https://www.hergazette.com/wp-content/uploads/2020/01/Stylish-Photography-Poses-For-Girls-11.jpg",
                simpleDateFormat.parse("26-10-2019 15:00:00"),
                "#TEAM_STANNIS"
            )
            myStories.add(story2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val story3 = MyStory(
            "https://anninc.scene7.com/is/image/LO/575769_6857?\$plp\$"
        )
        myStories.add(story3)
        storyBuilder=StoryView.Builder(parentFragmentManager)
            .setStoriesList(myStories)
            .setStoryDuration(5000)
            .setTitleLogoUrl("https://p7.hiclipart.com/preview/595/571/731/swoosh-nike-logo-just-do-it-adidas-nike.jpg")
            .setTitleText("Hamza Al-Omari")
            .setSubtitleText("Damascus")
            .setStoryClickListeners(object : StoryClickListeners {
                override fun onDescriptionClickListener(position: Int) {
//                    Toast.makeText(
//                        requireContext(),
//                        "Clicked: " + myStories[position].description,
//                        Toast.LENGTH_SHORT
//                    ).show()
                    parentFragmentManager.beginTransaction().replace(
                        com.ayata.clad.R.id.main_fragment,
                        FragmentProductDetail()
                    ).addToBackStack(null).commit()
                }
                override fun onTitleIconClickListener(position: Int) {}
            })
            .setOnStoryChangedCallback { position ->
                Toast.makeText(
                    requireContext(),
                    position.toString() + "",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setStartingIndex(0)
            .build()
        storyBuilder.show()
    }

    override fun onPause() {
        super.onPause()
        if(this::storyBuilder.isInitialized){
            storyBuilder.dismiss()
        }
    }
}