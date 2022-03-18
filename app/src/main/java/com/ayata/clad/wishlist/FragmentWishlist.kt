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
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.DialogFilterBinding
import com.ayata.clad.databinding.DialogShoppingSizeBinding
import com.ayata.clad.databinding.FragmentWishlistBinding
import com.ayata.clad.filter.filterdialog.AdapterFilterContent
import com.ayata.clad.filter.filterdialog.MyFilterContentViewItem
import com.ayata.clad.home.FragmentHome
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.product.ModelProduct
import com.ayata.clad.product.ModelRecommendedProduct
import com.ayata.clad.product.adapter.AdapterRecommendation
import com.ayata.clad.productlist.ItemOffsetDecoration
import com.ayata.clad.shopping_bag.adapter.AdapterCircleText
import com.ayata.clad.shopping_bag.model.ModelCircleText
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.wishlist.adapter.AdapterDialogOption
import com.ayata.clad.wishlist.adapter.AdapterWishList
import com.ayata.clad.wishlist.response.get.GetWishListResponse
import com.ayata.clad.wishlist.response.get.Wishlist
import com.ayata.clad.wishlist.viewmodel.WishListViewModel
import com.ayata.clad.wishlist.viewmodel.WishListViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson


class FragmentWishlist : Fragment() {
    companion object {
        private const val TAG = "FragmentWishlist"
    }

    private lateinit var binding: FragmentWishlistBinding
    private var myWishList = ArrayList<Wishlist>()
    private lateinit var adapterWishList: AdapterWishList

    private lateinit var viewModel: WishListViewModel

    //size dialog
    private lateinit var adapterCircleSize: AdapterCircleText
    private var listSize = ArrayList<ModelCircleText>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWishlistBinding.inflate(inflater, container, false)
        setUpViewModel()
        initAppbar()
        initRefreshLayout()
        setUpFilterListener()
        setUpRecyclerProductList()
        getWishListAPI()

        return binding.root
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            WishListViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        ).get(WishListViewModel::class.java)
    }

    private fun initRefreshLayout() {
        //refresh layout on swipe
        binding.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getWishListAPI()
            binding.swipeRefreshLayout.isRefreshing = false
        })
        //Adding ScrollListener to activate swipe refresh layout
        binding.shimmerView.root.setOnScrollChangeListener(View.OnScrollChangeListener { view, i, i1, i2, i3 ->
            binding.swipeRefreshLayout.isEnabled = i1 == 0
        })

        // Adding ScrollListener to getting whether we're on First Item position or not
        binding.rvWishList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.swipeRefreshLayout.isEnabled =
                    !recyclerView.canScrollVertically(-1) && dy < 0
            }
        })
        binding.rvRecommendation.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.swipeRefreshLayout.isEnabled = true
            }
        })

    }

    private fun setUpView() {
        Log.d(TAG, "setUpView: " + myWishList.size);

        if (myWishList.isEmpty()) {
            binding.layoutFilled.visibility = View.GONE
            binding.llEmpty.visibility = View.VISIBLE
            setUpRecyclerRecommendation()
        } else {
            binding.layoutFilled.visibility = View.VISIBLE
            binding.llEmpty.visibility = View.GONE
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
            adapter = AdapterRecommendation(
                requireContext(),
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
                "3561", "37"
            )
        )
        listRecommended.add(
            ModelRecommendedProduct(
                "https://images.squarespace-cdn.com/content/v1/566e100d0e4c116bdc11b2c2/1473302788755-FL48S6YFWHYC9KU18K52/245282-ceb4145ac7b646889a16b6f5dbd2f455.png?format=750w",
                "adidas Yeezy Boost 700 MNVN Bone", "Lowest Ask",
                "https://www.pngkit.com/png/full/436-4366026_adidas-stripes-png-adidas-logo-without-name.png",
                "5004", "64"
            )
        )
        listRecommended.add(
            ModelRecommendedProduct(
                "https://www.pngkit.com/png/full/70-704028_running-shoes-png-image-running-shoes-clipart-transparent.png",
                "Jordan 11 Retro Low White Concord (W) ", "Lowest Ask",
                "https://upload.wikimedia.org/wikipedia/en/thumb/3/37/Jumpman_logo.svg/1200px-Jumpman_logo.svg.png",
                "4500", "100"
            )
        )
        return listRecommended

    }

    private fun setUpRecyclerProductList() {
        adapterWishList = AdapterWishList(
            requireContext(),
            myWishList
        ).also {
            it.setProductClickListener { recommendedProduct ->
                //wishlist
                Log.d("testmyfilter", "setUpRecyclerRecommendation: $recommendedProduct")
                val bundle = Bundle()
                bundle.putSerializable(FragmentHome.PRODUCT_DETAIL, recommendedProduct.product)
                val fragmentProductDetail = FragmentProductDetail()
                fragmentProductDetail.arguments = bundle
                parentFragmentManager.beginTransaction().replace(
                    R.id.main_fragment,
                    fragmentProductDetail
                ).addToBackStack(null).commit()
            }
        }.also { it ->
            it.setSettingClickListener { product ->
                val isWish = product.product.isInWishlist
//                val list=ArrayList<MyFilterContentViewItem.MultipleChoice>()
//                if(product.isCart){
//                    list.add(MyFilterContentViewItem.MultipleChoice(getString(R.string.wl_case3), isCart))
//                }else{
//                    list.add(MyFilterContentViewItem.MultipleChoice(getString(R.string.wl_case1), isCart))
//                }
//                if(isWish){
//                    list.add(MyFilterContentViewItem.MultipleChoice(getString(R.string.wl_case2), isWish))
//                }
                val list = ArrayList<String>()
//                if (!product.product.is_in_cart) {
//                    list.add(getString(R.string.wl_case1))
//                }
                list.add(getString(R.string.wl_case3))
//                if (isWish) {
                list.add(getString(R.string.wl_case2))
//                }
//                showDialogMultipleChoice("Options",list,product)
                showDialogWishlist(product, list)
            }
        }.also {
            it.setBagClickListener {
                addToCartAPI(it)
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
    //not used
    private fun showDialogMultipleChoice(
        title: String,
        listContent: List<MyFilterContentViewItem.MultipleChoice>, product: Wishlist
    ) {
        val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        val adapterFilterContent = AdapterFilterContent(
            context, listContent
        ).also { adapter ->
            adapter.setFilterContentMultipleClickListener { data ->
                for (item in listContent) {
                    when {
                        item.title.contains(getString(R.string.wl_case1), true) -> {
                            //add to bag
                            addToCartAPI(product)
                        }
                        item.title.contains(getString(R.string.wl_case2), true) -> {
                            //remove wishlist
                            removeWishListAPI(product)
                        }
                        item.title.contains(getString(R.string.wl_case3), true) -> {
                            //change size
                            showDialogSize(product)
                        }
                    }
                    if (item == data) {
                        item.isSelected = true
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
        dialogBinding.title.text = title
        dialogBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterFilterContent
        }

        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun showDialogSingleChoice(
        title: String,
        listContent: List<MyFilterContentViewItem.SingleChoice>
    ) {
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

    private fun showSnackBar(msg: String) {
        val snackbar = Snackbar
            .make(binding.root, msg, Snackbar.LENGTH_SHORT)
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun addToCartAPI(product: Wishlist) {
        Log.d(TAG, "addToCartAPI: "+product);
        viewModel.wishListToCart(PreferenceHandler.getToken(context).toString(), product.wishlist_id)
        viewModel.getWishAPIToCart().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "addToCartAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        showSnackBar("Product added to cart")
                        MainActivity.NavCount.myBoolean= MainActivity.NavCount.myBoolean?.plus(1)
                        var pos=0
                        for (item in myWishList) {
                            if (item.wishlist_id == product.wishlist_id) {
//                                item.product.is_in_cart = true
                                pos=myWishList.indexOf(item)
                            }
                        }
                        myWishList.removeAt(pos)
                        adapterWishList.notifyItemRemoved(pos)
                        MainActivity.NavCount.myWishlist = MainActivity.NavCount.myWishlist?.minus(1)
                    }
                    binding.spinKit.visibility=View.GONE

                }
                Status.LOADING -> {
                    binding.spinKit.visibility=View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    binding.spinKit.visibility=View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "addToCartAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun removeWishListAPI(product: Wishlist) {
        viewModel.removeFromWishAPI(
            PreferenceHandler.getToken(context).toString(),
            product.wishlist_id!!
        )
        viewModel.getRemoveFromWishAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.spinKit.visibility = View.GONE
                    Log.d(TAG, "removeWishListAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        showSnackBar(msg = "Product removed from wishlist")
                        var remove: Wishlist? = null;
                        var position: Int? = null
                        try {
                            for ((index, item) in myWishList.withIndex()) {
                                if (item.wishlist_id == product.wishlist_id) {
                                    remove = item
                                    position = index
                                }
                            }
                            remove?.let {
                                myWishList.remove(remove)
                            }
                            setUpView()
                            position?.let {
                                adapterWishList.notifyItemRemoved(position)
                            }

                        } catch (e: Exception) {
                            Log.d(TAG, "removeWishListAPI:Error ${e.message}")
                        }
                    }

                }
                Status.LOADING -> {
                    binding.spinKit.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.spinKit.visibility = View.GONE
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "removeWishListAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun getWishListAPI() {
        setShimmerLayout(true)
        viewModel.wishListAPI(PreferenceHandler.getToken(context).toString())
        viewModel.getWishListAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    setShimmerLayout(false)
                    Log.d(TAG, "getWishListAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val message = jsonObject.get("message").asString
                            if (message.contains("Your wishlist is empty.", true)) {
                                MainActivity.NavCount.myWishlist = 0
                                setUpView()
                            }

                        } catch (e: Exception) {
                            Log.d(TAG, "getWishListAPI:Error ${e.message}")
                            try {
                                val wishListResponse =
                                    Gson().fromJson<GetWishListResponse>(
                                        jsonObject,
                                        GetWishListResponse::class.java
                                    )
                                if (wishListResponse.wishlist != null) {
                                    if (wishListResponse.wishlist.size > 0) {
                                        val wishlist = wishListResponse.wishlist
                                        Log.d(TAG, "getWishListAPI: " + wishlist.size);
                                        MainActivity.NavCount.myWishlist = wishlist.size
                                        setDataToView(wishlist)
                                    } else {
                                        MainActivity.NavCount.myWishlist = 0
                                        setUpView()
                                    }

                                }
                            } catch (e: Exception) {
                                Log.d(TAG, "getWishListAPI:Error ${e.message}")
                            }
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

    private fun setDataToView(wishlist: List<Wishlist>) {
        myWishList.clear()
        myWishList.addAll(wishlist)
        Log.d(TAG, "setUpView upper: " + wishlist.size);
        adapterWishList.notifyDataSetChanged()
        setUpView()
    }

    private fun setShimmerLayout(isVisible: Boolean) {
        if (isVisible) {
            binding.layoutFilled.visibility = View.GONE
            binding.llEmpty.visibility = View.GONE
            binding.shimmerFrameLayout.visibility = View.VISIBLE
            binding.shimmerFrameLayout.startShimmer()
        } else {
            binding.layoutFilled.visibility = View.VISIBLE
            binding.llEmpty.visibility = View.GONE
            binding.shimmerFrameLayout.visibility = View.GONE
            binding.shimmerFrameLayout.stopShimmer()
        }
    }

    private fun showDialogSize(product: Wishlist) {

        val dialogBinding = DialogShoppingSizeBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.title.text = "Size"
        var sizeSelected = ""
        adapterCircleSize = AdapterCircleText(context, listSize).also { adapter ->
            adapter.setCircleClickListener { listItem ->
                for (item in listSize) {
                    item.isSelected = item.equals(listItem)
                }
                sizeSelected = listItem.title
                adapterCircleSize.notifyDataSetChanged()
            }
        }
        prepareListSize()

        dialogBinding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = adapterCircleSize
        }

        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        dialogBinding.btnSave.setOnClickListener {
            //TODO API SIZE
//            saveSizeAPI(product, sizeSelected)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun prepareListSize() {
        listSize.clear()
//        listSize.add(ModelCircleText("s", true))
//        listSize.add(ModelCircleText("m", false))
//        listSize.add(ModelCircleText("l", false))
//        listSize.add(ModelCircleText("xl", false))
//        listSize.add(ModelCircleText("xxl", false))
        adapterCircleSize.notifyDataSetChanged()
    }

    private fun showDialogWishlist(product: Wishlist, listDialog: ArrayList<String>) {

        val dialogBinding = DialogShoppingSizeBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.title.text = "Options"
        val adapterOption = AdapterDialogOption(context, listDialog).also { adapter ->
            adapter.setOptionClickListener { item ->
                //click
                when {
                    item.contains(getString(R.string.wl_case1), true) -> {
                        //add to bag
                        addToCartAPI(product)
                    }
                    item.contains(getString(R.string.wl_case2), true) -> {
                        //remove wishlist
                        removeWishListAPI(product)
                        bottomSheetDialog.dismiss()
                    }
                    item.contains(getString(R.string.wl_case3), true) -> {
                        //change size
                        showDialogSize(product)
                        bottomSheetDialog.dismiss()
                    }
                }
            }
        }

        dialogBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = adapterOption
        }

        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        dialogBinding.btnSave.visibility = View.GONE

        bottomSheetDialog.show()
    }

    private fun saveSizeAPI(product: ModelProduct, sizeSelected: String) {
        viewModel.saveSizeAPI(
            PreferenceHandler.getToken(context).toString(),
            product.id,
            sizeSelected
        )
        viewModel.getSizeAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "saveSizeAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        showSnackBar("Size Updated")
                        try {

                        } catch (e: Exception) {
                            Log.d(TAG, "saveSizeAPI:Error ${e.message}")
                        }
                    }
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "saveSizeAPI:Error ${it.message}")
                }
            }
        })
    }
}