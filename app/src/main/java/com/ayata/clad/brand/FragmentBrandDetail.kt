package com.ayata.clad.brand

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.brand.response.BrandDetailResponse
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentBrandDetailBinding
import com.ayata.clad.home.FragmentHome
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.productlist.adapter.AdapterProductList
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.MyLayoutInflater
import com.ayata.clad.view_all.viewmodel.BrandAllViewModel
import com.ayata.clad.view_all.viewmodel.BrandAllViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentBrandDetail.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentBrandDetail : Fragment() {
    // TODO: Rename and change types of parameters
    private var brandSlug: String? = null
    lateinit var binding: FragmentBrandDetailBinding
    private lateinit var viewModel: BrandAllViewModel
    private var listProduct = ArrayList<ProductDetail>()
    private lateinit var myAdapter: AdapterProductList
//    private lateinit var adapterPaging: ProductDetailViewAllAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            brandSlug = it.getString(ARG_PARAM1)
        }
        setUpViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentBrandDetailBinding.inflate(inflater, container, false)
//        adapterPaging = ProductDetailViewAllAdapter(this)
        setUpFullScreen()
        initRecyclerView()
        brandDetailObserver()
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
        return binding.root
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

    private fun initRecyclerView() {
        myAdapter = AdapterProductList(requireContext(), listProduct)
        binding.apply {
            rvProducts.apply {
                layoutManager = GridLayoutManager(context, 2)
                itemAnimator = null
                setHasFixedSize(true)
                adapter = myAdapter
            }
        }
        myAdapter.setProductClickListener {data->
            val i = Intent(activity, MainActivity::class.java)
            i.putExtra(Constants.FROM_STORY, true)
            i.putExtra("data",data as ProductDetail)
            startActivity(i)
        }
        myAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkEmpty()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                checkEmpty()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                checkEmpty()
            }

            fun checkEmpty() {
                binding.layoutEmpty.visibility = (if (myAdapter.itemCount == 0) View.VISIBLE else View.GONE)
            }
        })
    }

    private fun brandDetailObserver() {
       setMainLayout(false)
        viewModel.getBrandDetailAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    hideProgress()
                    hideError()
                    Log.d("test", "getbranddetail products: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            setMainLayout(true)
                           val brandDetail= Gson().fromJson(jsonObject, BrandDetailResponse::class.java)
                            //set rest values
                            setView(brandDetail)
                            //set product recyclerview
                            listProduct.clear()
                            if (brandDetail.brand.products.size > 0) {
                                listProduct.addAll(brandDetail.brand.products)
                            } else {
//                                Toast.makeText(context, "Empty Products", Toast.LENGTH_LONG).show()
                            }
                            myAdapter.notifyDataSetChanged()
                        } catch (e: Exception) {
                            showError(e.message.toString())
                            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            Log.d("test", "getbranddetail products: " + e.message)
                        }

                    }
                }
                Status.LOADING -> {
                    showProgress()
                }
                Status.ERROR -> {
                    //Handle Error
                    hideProgress()
                    showError(it.message.toString())
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun setMainLayout(b: Boolean) {
        binding.apply {
            coverImage.isVisible=b
            appBar.isVisible=b
            rvProducts.isVisible=b
            layoutEmpty.isVisible=b
            layoutSearch.isVisible=b
            coverGradiant.isVisible=b
        }
    }

    private fun setView(brandDetail: BrandDetailResponse?) {
        brandDetail?.brand?.apply {
            binding.apply {
                brandAddress.setText(address?:"Kathmandu,Nepal")
                Glide.with(requireContext()).load(brandImage).fallback(Constants.ERROR_DRAWABLE).error(Constants.ERROR_DRAWABLE).into(brandlogo)
                Glide.with(requireContext())
                    .load(brandCover)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error("https://www.organizedinteriors.com/blog/wp-content/uploads/2017/09/wrinkle-free-dry-cleaned-clothes.jpg")
                    .fallback(Constants.ERROR_DRAWABLE)
                    .into(binding.coverImage)

                tvDesc.text=description
                soldNo.text=itemsSold.toString()
//                reviewNo.text=totalReviews.toString()
                brandTitle.text=name
            }

        }

    }

    fun showProgress() {
        binding.progressBar.rootContainer.visibility = View.VISIBLE
    }

    fun hideProgress() {
        binding.progressBar.rootContainer.visibility = View.GONE
    }

    private fun showError(it: String) {
//        binding.layoutGroup.visibility=View.GONE
        MyLayoutInflater().onAddField(
            requireContext(),
            binding.root,
            R.layout.layout_error,
            Constants.ERROR_SERVER,
            "Error!",
            it
        )

    }

    private fun hideError() {
//        binding.layoutGroup.visibility=View.VISIBLE
        if (binding.root.findViewById<LinearLayout>(R.id.layout_root) != null) {
            MyLayoutInflater().onDelete(
                binding.root,
                binding.root.findViewById(R.id.layout_root)
            )
        }
    }
    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            BrandAllViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[BrandAllViewModel::class.java]
        viewModel.brandDetailApi(brandSlug ?: "goldstar")

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentBrandDetail.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            FragmentBrandDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}