package com.ayata.clad.wishlist

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
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
import com.ayata.clad.home.adapter.AdapterRecommended
import com.ayata.clad.home.response.HomeResponse
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.home.viewmodel.HomeViewModel
import com.ayata.clad.home.viewmodel.HomeViewModelFactory
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.product.ModelProduct
import com.ayata.clad.productlist.ItemOffsetDecoration
import com.ayata.clad.shopping_bag.adapter.AdapterCircleText
import com.ayata.clad.shopping_bag.model.ModelCircleText
import com.ayata.clad.utils.MyLayoutInflater
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.utils.removeDoubleQuote
import com.ayata.clad.wishlist.adapter.AdapterDialogOption
import com.ayata.clad.wishlist.adapter.AdapterWishList
import com.ayata.clad.wishlist.response.get.GetWishListResponse
import com.ayata.clad.wishlist.response.get.Wishlist
import com.ayata.clad.wishlist.viewmodel.WishListViewModel
import com.ayata.clad.wishlist.viewmodel.WishListViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson


class FragmentWishlist : Fragment() {
    companion object {
        private const val TAG = "FragmentWishlist"
    }

    private var listRecommendation = ArrayList<ProductDetail>()
    private lateinit var binding: FragmentWishlistBinding
    private var myWishList = ArrayList<Wishlist>()
    private var myWishListRecommendation = ArrayList<Wishlist>()
    private lateinit var adapterWishList: AdapterWishList
    private lateinit var adapterRecommended: AdapterRecommended
    private lateinit var viewModel: WishListViewModel
    private lateinit var viewModelHome: HomeViewModel


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

        viewModelHome = ViewModelProvider(
            requireActivity(),
            HomeViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )
            .get(HomeViewModel::class.java)
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
            binding.productCount.text = "${myWishList.size} PRODUCT(S)"
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
        adapterRecommended = AdapterRecommended(
            requireContext(),
            listRecommendation, object : AdapterRecommended.OnItemClickListener {
                override fun onRecommendedClicked(data: ProductDetail, position: Int) {
                    val bundle = Bundle()
                    bundle.putSerializable(FragmentHome.PRODUCT_DETAIL, data)
                    val fragmentProductDetail = FragmentProductDetail()
                    fragmentProductDetail.arguments = bundle
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragmentProductDetail)
                        .addToBackStack(null).commit()
                }

            }
        )
        binding.rvRecommendation.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterRecommended
        }
        viewModelHome.getDashboardAPI().observe(viewLifecycleOwner, {
            Log.d(TAG, "setUpRecyclerRecommendation: " + it.status);
            when (it.status) {
                Status.SUCCESS -> {
//                    setShimmerLayout(false)
//                    hideError()
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val homeResponse =
                                Gson().fromJson<HomeResponse>(jsonObject, HomeResponse::class.java)
                            if (homeResponse.details != null) {
                                val detail = homeResponse.details
                                prepareDataForRecommended(detail.recommended)
                            }
                        } catch (e: Exception) {
                        }
                    }

                }
                Status.LOADING -> {
//                    setShimmerLayout(true)
                }
                Status.ERROR -> {
                    //Handle Error
//                    setShimmerLayout(false)
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
//                    showError(it.message.toString())
                }
            }
        })
    }

    private fun prepareDataForRecommended(list: List<ProductDetail>) {

        listRecommendation.addAll(list)
        adapterRecommended.notifyDataSetChanged()
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
//                list.add(getString(R.string.wl_case3))
//                if (isWish) {
                list.add(getString(R.string.wl_case2))
//                }
//                showDialogMultipleChoice("Options",list,product)
                showDialogWishlist(product, list)
            }
        }.also {
            it.setBagClickListener { wishlist ->
                val currentVariant = wishlist.product.variants.filter {
                    it.variantId == wishlist.selected.variantId
                }.single()
                if (!currentVariant.isInCart) {
                    addToCartAPI(wishlist)
                } else {
                    Toast.makeText(requireContext(), "Product already in cart", Toast.LENGTH_SHORT)
                        .show()
                }

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

    private fun showToast(message: String, isSuccess: Boolean) {
        val toast = Toast(context)
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.custom_toast, null)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val ivImage = view.findViewById<ImageView>(R.id.ivImage)
        val cardView: CardView = view.findViewById(R.id.cardBackground)
        tvMessage.text = message
        if (isSuccess) {
            cardView.setCardBackgroundColor(requireContext().resources.getColor(R.color.title_color))
            ivImage.setImageResource(R.drawable.ic_success)
        } else {
//            cardView.setCardBackgroundColor(context!!.resources.getColor(R.color.colorPriceTag))
//            ivImage.setImageResource(R.drawable.ic_info)
        }
        toast.setView(view)
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 150)
        toast.show()
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
                when (data.title) {
                    "Cheapest (low - high)" -> {
                        myWishList.sortBy {
                            it.product.price
                        }
                        adapterWishList.notifyDataSetChanged()
                    }
                    "Most Expensive (high - low)" -> {
                        myWishList.sortByDescending {
                            it.product.price
                        }
                        adapterWishList.notifyDataSetChanged()
                    }
                    "Recommended" -> {
                        myWishList.clear()
                        myWishList.addAll(myWishListRecommendation)
                        adapterWishList.notifyDataSetChanged()
                    }

                }
                adapter.notifyDataSetChanged()
                bottomSheetDialog.dismiss()
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
//        val snackbar = Snackbar
//            .make(binding.root, msg, Snackbar.LENGTH_SHORT)
//        snackbar.setActionTextColor(Color.WHITE)
//        snackbar.show()
        showToast(msg.removeDoubleQuote(), true)

    }

    private fun addToCartAPI(product: Wishlist) {
        Log.d(TAG, "addToCartAPI: " + product);
        viewModel.addToCartAPI(
            PreferenceHandler.getToken(context).toString(),
            product.selected.variantId
        )
        viewModel.getAddToCartAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "addToCartAPI: ${it.data}")
                    val jsonObject = it.data
                    binding.spinKit.visibility = View.GONE
                    if (jsonObject != null) {
                        showSnackBar(jsonObject.get("message").toString())
                        MainActivity.NavCount.myBoolean = MainActivity.NavCount.myBoolean?.plus(1)
                        var pos = 0
                        for (item in myWishList) {
                            if (item.wishlist_id == product.wishlist_id) {
                                pos = myWishList.indexOf(item)
                                break
                            }
                        }
                        val data =
                            myWishList[pos].product.variants.filter { myWishList[pos].selected.variantId == it.variantId }
                                .single()

                        data.isInCart = true
                        adapterWishList.notifyItemChanged(pos)

                        //                        myWishList.removeAt(pos)
//                        adapterWishList.notifyItemRemoved(pos)
//                        MainActivity.NavCount.myWishlist =
//                            MainActivity.NavCount.myWishlist?.minus(1)
                    }


                }
                Status.LOADING -> {
                    binding.spinKit.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    binding.spinKit.visibility = View.GONE
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
                                MainActivity.NavCount.myWishlist =
                                    MainActivity.NavCount.myWishlist?.minus(1)

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
                    hideError()
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
                    showError(it.message.toString())
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "getWishListAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun showError(it: String) {
        binding.layoutFilled.visibility = View.GONE
        MyLayoutInflater().onAddField(
            requireContext(),
            binding.layoutContainer,
            R.layout.layout_error,
            R.drawable.ic_cart,
            "Error!",
            it
        )

    }

    private fun hideError() {
        binding.layoutFilled.visibility = View.VISIBLE
        if (binding.root.findViewById<LinearLayout>(R.id.layout_root) != null) {
            MyLayoutInflater().onDelete(
                binding.layoutContainer,
                binding.root.findViewById(R.id.layout_root)
            )
        }
    }

    private fun setDataToView(wishlist: List<Wishlist>) {
        myWishList.clear()
        myWishList.addAll(wishlist)
        myWishListRecommendation.addAll(wishlist)
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