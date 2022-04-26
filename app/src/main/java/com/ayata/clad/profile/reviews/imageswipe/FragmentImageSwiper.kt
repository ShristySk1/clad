package com.ayata.clad.profile.reviews.imageswipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentImageSwiperBinding
import com.ayata.clad.profile.reviews.imageswipe.adapter.ViewPagerAdapter
import com.igreenwood.loupe.Loupe
import java.io.Serializable


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentImageSwiper.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentImageSwiper : Fragment() {
    private lateinit var binding: FragmentImageSwiperBinding

    // TODO: Rename and change types of parameters
    private var param1: List<String>? = null
    private var param2: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as List<String>
            param2 = it.getInt(ARG_PARAM2) as Int

        }
    }

    lateinit var mViewPagerAdapter: ViewPagerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentImageSwiperBinding.inflate(inflater, container, false)
        // Initializing the ViewPagerAdapter
        initView()
        // Initializing the ViewPagerAdapter
        Log.d("testhere", "onCreateView: "+param1?.size);
        mViewPagerAdapter = ViewPagerAdapter(requireContext(), param1?: listOf())
        Log.d("testposotopn", "onCreateView: "+param2);

        // Adding the Adapter to the ViewPager
        binding.viewPagerMain.setAdapter(mViewPagerAdapter)
        mViewPagerAdapter.setImageDragListener {
            parentFragmentManager.popBackStackImmediate()
        }
        binding.viewPagerMain.setCurrentItem(param2 ?: 0)


        return binding.root
    }

    private fun initView() {
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "",
            textDescription = ""
        )
        (activity as MainActivity).showBottomNavigation(false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentImageSwiper.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: List<String>,param2:Int) =
            FragmentImageSwiper().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1 as Serializable)
                    putInt(ARG_PARAM2, param2)
                }
            }
    }
}