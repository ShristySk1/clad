package com.ayata.clad.wishlist

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.DialogFilterBinding
import com.ayata.clad.databinding.FragmentWishlistBinding
import com.ayata.clad.filter.filterdialog.AdapterFilterContent
import com.ayata.clad.filter.filterdialog.MyFilterContentViewItem
import com.ayata.clad.product.adapter.AdapterRecommendation
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.product.ModelProduct
import com.ayata.clad.product.ModelRecommendedProduct
import com.ayata.clad.productlist.ItemOffsetDecoration
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.wishlist.adapter.AdapterWishList
import com.ayata.clad.wishlist.viewmodel.WishListViewModel
import com.ayata.clad.wishlist.viewmodel.WishListViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar


class FragmentWishlist : Fragment() {
    companion object{
        private const val TAG="FragmentWishlist"
    }
    private lateinit var binding: FragmentWishlistBinding
    private var myWishList= ArrayList<ModelProduct>()
    private lateinit var adapterWishList: AdapterWishList

    private lateinit var viewModel:WishListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWishlistBinding.inflate(inflater, container, false)
        setUpViewModel()
        initAppbar()
        getWishListAPI()
        setUpFilterListener()
        setUpRecyclerProductList()
        return binding.root
    }

    private fun setUpViewModel(){
        viewModel=ViewModelProvider(this,
            WishListViewModelFactory(ApiRepository(ApiService.getInstance()))).get(WishListViewModel::class.java)
    }

    private fun setUpView(){
        if (myWishList.isEmpty()) {
            binding.textView12.visibility=View.GONE
            binding.btnFilter.visibility=View.GONE
            binding.rvWishList.visibility = View.GONE
            binding.llEmpty.visibility = View.VISIBLE
            setUpRecyclerRecommendation()
        } else {
            binding.textView12.visibility=View.VISIBLE
            binding.btnFilter.visibility=View.VISIBLE
            binding.llEmpty.visibility = View.GONE
            binding.rvWishList.visibility = View.VISIBLE
        }
    }

    private fun setUpFilterListener() {
        binding.btnFilter.setOnClickListener {
            val list = listOf(
                MyFilterContentViewItem.SingleChoice("Recommended", true),
                MyFilterContentViewItem.SingleChoice("Cheapest (low - high)", false),
                MyFilterContentViewItem.SingleChoice("Most Expensive (high - low)", false),
            )
            showDialogSingleChoice(
                "SORT BY", list
            )
        }
    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(
            getString(R.string.wishlist),
            isSearch = false,
            isProfile = true,
            isClose = false
        )
    }

    private fun setUpRecyclerRecommendation() {
        binding.rvRecommendation.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = AdapterRecommendation(requireContext(),
                prepareDataForRecommended(mutableListOf()).toList()
            ).also {
                it.setProductClickListener { recommendedProduct ->
                    parentFragmentManager.beginTransaction().replace(
                        R.id.main_fragment,
                        FragmentProductDetail()
                    )
                        .addToBackStack(null).commit()
                }
            }
        }

    }
    private fun prepareDataForRecommended(listRecommended: MutableList<ModelRecommendedProduct>): MutableList<ModelRecommendedProduct> {
        listRecommended.clear()
        listRecommended.add(
            ModelRecommendedProduct(
                "https://freepngimg.com/thumb/categories/627.png",
                "Nike ISPA Overreact Sail Multi", "Nike Company",
                "https://p7.hiclipart.com/preview/595/571/731/swoosh-nike-logo-just-do-it-adidas-nike.jpg",
                "3561","37"
            )
        )
        listRecommended.add(
            ModelRecommendedProduct(
                "https://images.squarespace-cdn.com/content/v1/566e100d0e4c116bdc11b2c2/1473302788755-FL48S6YFWHYC9KU18K52/245282-ceb4145ac7b646889a16b6f5dbd2f455.png?format=750w",
                "adidas Yeezy Boost 700 MNVN Bone", "Lowest Ask",
                "https://www.pngkit.com/png/full/436-4366026_adidas-stripes-png-adidas-logo-without-name.png",
                "5004","64"
            )
        )
        listRecommended.add(
            ModelRecommendedProduct(
                "https://www.pngkit.com/png/full/70-704028_running-shoes-png-image-running-shoes-clipart-transparent.png",
                "Jordan 11 Retro Low White Concord (W) ", "Lowest Ask",
                "https://upload.wikimedia.org/wikipedia/en/thumb/3/37/Jumpman_logo.svg/1200px-Jumpman_logo.svg.png",
                "4500","100"
            )
        )
        return listRecommended

    }

    private fun setUpRecyclerProductList() {
        myWishList.add(
            ModelProduct(
                1,
                "https://freepngimg.com/thumb/categories/627.png",
                "ss",
                "com",
                "8523",
                "123",
                false
            )
        )
        myWishList.add(
            ModelProduct(
                2,
                "https://images.squarespace-cdn.com/content/v1/566e100d0e4c116bdc11b2c2/1473302788755-FL48S6YFWHYC9KU18K52/245282-ceb4145ac7b646889a16b6f5dbd2f455.png?format=750w",
                "ss",
                "com",
                "4500",
                "123",
                false
            )
        )
        myWishList.add(ModelProduct(1, "", "ss", "com", "7800","123", false))
        adapterWishList=AdapterWishList(requireContext(),
            myWishList
        ).also {
            it.setProductClickListener { recommendedProduct ->
                //wishlist
                parentFragmentManager.beginTransaction().replace(
                    R.id.main_fragment,
                    FragmentProductDetail()
                )
                    .addToBackStack(null).commit()
            }
        }.also {
            it.setSettingClickListener {
                val list = listOf(
                    MyFilterContentViewItem.MultipleChoice("Add to Bag", false),
                    MyFilterContentViewItem.MultipleChoice("Remove from wishlist", false)
                )
                showDialogMultipleChoice("Options", list)
            }
        }

        binding.rvWishList.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2)
            adapter = adapterWishList
            val itemDecoration = ItemOffsetDecoration(context, R.dimen.item_offset)
            addItemDecoration(itemDecoration)
        }
    }

    private fun showDialogMultipleChoice(title: String, listContent: List<MyFilterContentViewItem.MultipleChoice>) {
        val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        val list = listContent
        val adapterfilterContent = AdapterFilterContent(
            context, list
        ).also { adapter ->
            adapter.setCircleClickListener { data ->
                for (item in list) {
                    item.isSelected = item.equals(data)
                }
                adapter.notifyDataSetChanged()
            }
        }
        dialogBinding.title.text = title
        dialogBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterfilterContent
        }

        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun showDialogSingleChoice(title: String, listContent: List<MyFilterContentViewItem.SingleChoice>) {
        val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        val list = listContent
        val adapterfilterContent = AdapterFilterContent(
            context, list
        ).also { adapter ->
            adapter.setCircleClickListener { data ->
                for (item in list) {
                    item.isSelected = item.equals(data)
                }
                adapter.notifyDataSetChanged()
            }
        }
        dialogBinding.title.text = title
        dialogBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterfilterContent
        }

        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun showSnackBar(msg:String){
        val snackbar = Snackbar
            .make(binding.root, msg, Snackbar.LENGTH_SHORT)
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun addToCartAPI(){
        viewModel.addToCartAPI(PreferenceHandler.getToken(context).toString(),1)
        viewModel.getAddToCartAPI().observe(viewLifecycleOwner,{
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "addToCartAPI: ${it.data}")
                    val jsonObject=it.data
                    if(jsonObject!=null){
                        showSnackBar("Product added to cart")
                        try{

                        }catch (e:Exception){
                            Log.d(TAG, "addToCartAPI:Error ${e.message}")
                        }
                    }

                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "addToCartAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun removeWishListAPI(){
        viewModel.removeFromWishAPI(PreferenceHandler.getToken(context).toString(),1)
        viewModel.getRemoveFromWishAPI().observe(viewLifecycleOwner,{
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "removeWishListAPI: ${it.data}")
                    val jsonObject=it.data
                    if(jsonObject!=null){
                        showSnackBar(msg="Product removed from wishlist")
                        try{

                        }catch (e:Exception){
                            Log.d(TAG, "removeWishListAPI:Error ${e.message}")
                        }
                    }

                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "removeWishListAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun getWishListAPI(){
        setShimmerLayout(true)
        viewModel.wishListAPI(PreferenceHandler.getToken(context).toString())
        viewModel.getWishListAPI().observe(viewLifecycleOwner,{
            when (it.status) {
                Status.SUCCESS -> {
                    setShimmerLayout(false)
//                    myWishList.clear()
                    Log.d(TAG, "getWishListAPI: ${it.data}")
                    val jsonObject=it.data
                    if(jsonObject!=null){
                        try{
                            val message=jsonObject.get("message").asString
                            if(message.contains("Your wishlist is empty.",true)){
//                                setUpView()
                            }
                        }catch (e:Exception){
                            Log.d(TAG, "getWishListAPI:Error ${e.message}")
                        }
                    }
                    adapterWishList.notifyDataSetChanged()
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    setShimmerLayout(false)
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "getWishListAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun setShimmerLayout(isVisible:Boolean){
        if(isVisible){
            binding.layoutMain.visibility=View.GONE
            binding.shimmerFrameLayout.visibility=View.VISIBLE
            binding.shimmerFrameLayout.startShimmer()
        }else{
            binding.layoutMain.visibility=View.VISIBLE
            binding.shimmerFrameLayout.visibility=View.GONE
            binding.shimmerFrameLayout.stopShimmer()
        }
    }
}