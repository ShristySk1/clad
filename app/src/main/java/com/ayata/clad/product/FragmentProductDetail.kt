package com.ayata.clad.product

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentProductDetailBinding
import com.ayata.clad.shopping_bag.adapter.AdapterCircleText
import com.ayata.clad.shopping_bag.model.ModelCircleText
import com.ayata.clad.utils.copyToClipboard
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.snackbar.Snackbar


class FragmentProductDetail : Fragment() {
    val TAG = "FragmentProductDetail"
    private lateinit var adapterCircleText: AdapterCircleText
    private lateinit var binding: FragmentProductDetailBinding
    private var listText = ArrayList<ModelCircleText>()
    private var isProductLiked: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        initView()
        setUpFullScreen()
        setUpRecyclerColor()
        setUpRecyclerSize()
        setUpTabChoose()
        tapToCopyListener()
        productLikedListener()
        binding.detail2.ivClose.setOnClickListener {
            binding.appBar.setExpanded(true)
            binding.detail2.nestedScroll.scrollTo(0, 0)
        }
        setUpRecyclerRecommendation()
        return binding.root
    }

    private fun productLikedListener() {
        binding.frame.setOnClickListener {
            if (isProductLiked) {
                isProductLiked=false
                val snackbar = Snackbar
                    .make(binding.root, "Product removed from wishlist", Snackbar.LENGTH_SHORT)
//                .setAction("RETRY") { }
                snackbar.setActionTextColor(Color.WHITE)
                snackbar.show()
                binding.ivHeart.setImageResource(R.drawable.ic_heart_outline)
            } else {
                isProductLiked=true
                val snackbar = Snackbar
                    .make(binding.root, "Product added to wishlist", Snackbar.LENGTH_SHORT)
//                .setAction("RETRY") { }
                snackbar.setActionTextColor(Color.WHITE)
                snackbar.show()
                binding.ivHeart.setImageResource(R.drawable.ic_heart_filled)
            }
        }
        binding.frame2.setOnClickListener {
            val snackbar = Snackbar
                .make(binding.root, "Product added to cart", Snackbar.LENGTH_SHORT)
//                .setAction("RETRY") { }
            snackbar.setActionTextColor(Color.WHITE)
            snackbar.show()
        }
    }

    private fun tapToCopyListener() {
        binding
            .detail2.tvTapToCopy.setOnClickListener {
                val text =
                    requireContext().copyToClipboard(binding.detail2.tvTextToCopy.text.toString())
                Log.d(TAG, "tapToCopyListener: " + text);
                val toast = Toast.makeText(
                    requireContext(),
                    "Copied to Clipboard!", Toast.LENGTH_SHORT
                )
//                toast.setGravity(Gravity.BOTTOM or Gravity.RIGHT, 50, 50)
                toast.show()
            }
    }

    private fun setUpRecyclerRecommendation() {
        binding.detail2.rvRecommendation.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = AdapterRecommendation(
                listOf(
                    R.color.color1,
                    R.color.color2,
                    R.color.color3
                )
            ).also {
                it.setProductClickListener { recommendedProduct ->
                    Log.d(TAG, "setUpRecyclerRecommendation: " + recommendedProduct);
                }
            }
        }

    }

    private fun initView() {
        (activity as MainActivity).hideToolbar()
        (activity as MainActivity).showBottomNav(false)
        val product = ModelProduct(1, "", "ss", "com", "$123", false)

    }

    private fun setUpTabChoose() {
        val tabStrip = binding.detail2.tab.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.getChildCount()) {
            tabStrip.getChildAt(i).setOnTouchListener(OnTouchListener { v, event -> true })
        }
        binding.detail2.tab.getTabAt(1)?.select()
        val tabStrip2 = binding.detail2.tab2.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.getChildCount()) {
            tabStrip2.getChildAt(i).setOnTouchListener(OnTouchListener { v, event -> true })
        }
    }

    private fun setUpRecyclerColor() {
        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
        }
        binding.rvColor.apply {
            layoutManager =
                flexboxLayoutManager
            adapter = AdapterColor(
                listOf(
                    R.color.color1,
                    R.color.color2,
                    R.color.color3
                )
            )
        }
    }

    private fun setUpFullScreen() {
        activity?.let {
            it.window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                statusBarColor = Color.TRANSPARENT
            }
        }
    }

    private fun setUpRecyclerSize() {
        adapterCircleText = AdapterCircleText(context, listText).also { adapter ->
            adapter.setCircleClickListener { data ->
                for (item in listText) {
                    item.isSelected = item.equals(data)
                    adapterCircleText.notifyDataSetChanged()
                    Log.d(TAG, "setUpRecyclerSize: " + item.title);

                }

            }
        }
        prepareListSize()

        binding.detail2.rvSize.apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = adapterCircleText
        }

    }

    private fun prepareListSize() {
        listText.clear()
        listText.add(ModelCircleText("xs", false))
        listText.add(ModelCircleText("s", true))
        listText.add(ModelCircleText("m", false))
        listText.add(ModelCircleText("l", false))
        listText.add(ModelCircleText("xl", false))
        adapterCircleText.notifyDataSetChanged()
    }
}

