package com.ayata.clad.shopping_bag.checkout

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.text.bold
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
import com.ayata.clad.databinding.DialogShoppingSizeBinding
import com.ayata.clad.databinding.FragmentCartCheckoutBinding
import com.ayata.clad.shopping_bag.adapter.AdapterCheckout
import com.ayata.clad.shopping_bag.adapter.AdapterCircleText
import com.ayata.clad.shopping_bag.model.ModelCheckout
import com.ayata.clad.shopping_bag.model.ModelCircleText
import com.ayata.clad.shopping_bag.response.checkout.Cart
import com.ayata.clad.shopping_bag.response.checkout.CartResponse
import com.ayata.clad.shopping_bag.response.checkout.Selected
import com.ayata.clad.shopping_bag.shipping.FragmentShipping
import com.ayata.clad.shopping_bag.viewmodel.CheckoutViewModel
import com.ayata.clad.shopping_bag.viewmodel.CheckoutViewModelFactory
import com.ayata.clad.utils.MyLayoutInflater
import com.ayata.clad.utils.PreferenceHandler
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson


class FragmentCheckout : Fragment(), AdapterCheckout.OnItemClickListener {

    private lateinit var binding: FragmentCartCheckoutBinding
    private lateinit var viewModel: CheckoutViewModel

    private lateinit var adapterCheckout: AdapterCheckout
    private var listCheckout = ArrayList<ModelCheckout>()
    private lateinit var layoutManagerCheckout: LinearLayoutManager

    private lateinit var adapterCircleSize: AdapterCircleText
    private var listSize = ArrayList<ModelCircleText>()
    private lateinit var apiCartList: List<Cart>

    private lateinit var adapterCircleQty: AdapterCircleText
    private var listQty = ArrayList<ModelCircleText>()
    private var updatePosition = -1
    private lateinit var listContainingGrandtotal: CartResponse

    companion object {
        private const val TAG = "FragmentCheckout"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartCheckoutBinding.inflate(inflater, container, false)

        setUpViewModel()
//        (parentFragment as FragmentShoppingBag).checkoutPage()
        initAppbar()
        initView()
        setShimmerLayout(false)
        initRecycler()
//        setUpView()
        initRefreshLayout()
        getCartAPI()
        setAddObserver()
        setMinusObserver()
        setSelectObserver()
        setRemoveObserver()

        return binding.root
    }

    private fun setRemoveObserver() {
        viewModel.getRemoveFromCartAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
//                    binding.spinKit.visibility = View.GONE
                    Log.d(TAG, "removeWishListAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val message = jsonObject.get("message").asString
                            Log.d(TAG, "setRemoveObserver: " + message);
                            if (message.contains("removed", true)) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                updateCartAtPosition(updatePosition)
                            } else {
                                val checkoutResponse =
                                    Gson().fromJson<CartResponse>(
                                        jsonObject,
                                        CartResponse::class.java
                                    )
                                listContainingGrandtotal = checkoutResponse
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "getCartAPI:Error ${e.message}")

                        }
                    }

                }
                Status.LOADING -> {
//                    binding.spinKit.visibility = View.VISIBLE
                }
                Status.ERROR -> {
//                    binding.spinKit.visibility = View.GONE
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "removeWishListAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun setSelectObserver() {
        viewModel.getSelectCartAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
//                    binding.spinKit.select = View.GONE
                    Log.d(TAG, "selectobserver: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val checkoutResponse =
                                Gson().fromJson<CartResponse>(
                                    jsonObject,
                                    CartResponse::class.java
                                )
                            val cartArray = checkoutResponse.cart
                            val p_npr = checkoutResponse.cartTotalNpr
                            val p_dollar = checkoutResponse.cartTotalDollar
                            listContainingGrandtotal = checkoutResponse
                            if (cartArray != null) {
//                                    updateCartAtPosition(cartArray.selected,cartArray.is_selected,updatePosition,p_npr,p_dollar)

                                //update cart
                                updateCartAtPosition(
                                    cartArray[0].selected,
                                    cartArray[0].is_selected,
                                    updatePosition,
                                    p_npr,
                                    p_dollar
                                )

                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "getCartAPI:Error ${e.message}")
                            try {
                                val message = jsonObject.get("message").asString
                                if (message.contains("empty.", true)) {

                                    setUpView()
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.d(TAG, "getCartAPI:Error ${e.message}")
                            }
                        }
                    }

                }
                Status.LOADING -> {
//                    binding.spinKit.visibility = View.VISIBLE
                }
                Status.ERROR -> {
//                    binding.spinKit.visibility = View.GONE
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "removeWishListAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun setMinusObserver() {

        viewModel.getMinusFromCartAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
//                    binding.spinKit.visibility = View.GONE
                    Log.d(TAG, "removeWishListAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val checkoutResponse =
                                Gson().fromJson<CartResponse>(
                                    jsonObject,
                                    CartResponse::class.java
                                )
                            val cartArray = checkoutResponse.cart
                            val p_npr = checkoutResponse.cartTotalNpr
                            val p_dollar = checkoutResponse.cartTotalDollar
                            listContainingGrandtotal = checkoutResponse
                            if (cartArray != null) {
                                //update cart
                                updateCartAtPosition(
                                    cartArray[0].selected,
                                    cartArray[0].is_selected,
                                    updatePosition,
                                    p_npr,
                                    p_dollar
                                )
                            }


                        } catch (e: Exception) {
                            Log.d(TAG, "getCartAPI:Error ${e.message}")
                            try {
                                val message = jsonObject.get("message").asString
                                if (message.contains("empty.", true)) {

                                    setUpView()
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.d(TAG, "getCartAPI:Error ${e.message}")

                            }
                        }
                    }

                }
                Status.LOADING -> {
//                    binding.spinKit.visibility = View.VISIBLE
                }
                Status.ERROR -> {
//                    binding.spinKit.visibility = View.GONE
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "removeWishListAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun setAddObserver() {
        viewModel.getAddToCartAPI().observe(viewLifecycleOwner, {
            if (it != null) {
                Log.d("testmystatus", "addToCartAPI: " + it.status);
                when (it.status) {
                    Status.SUCCESS -> {
                        Log.d("testsuccess", "addToCartAPI: ${it.data}")
                        val jsonObject = it.data
                        if (jsonObject != null) {
                            try {
                                val checkoutResponse =
                                    Gson().fromJson<CartResponse>(
                                        jsonObject,
                                        CartResponse::class.java
                                    )
                                val cartArray = checkoutResponse.cart
                                val p_npr = checkoutResponse.cartTotalNpr
                                val p_dollar = checkoutResponse.cartTotalDollar
                                listContainingGrandtotal = checkoutResponse
                                //update cart
                                Log.d("imhere", "addToCartAPI: ");

                                updateCartAtPosition(
                                    cartArray[0].selected,
                                    cartArray[0].is_selected,
                                    updatePosition, p_npr, p_dollar
                                )
                            } catch (e: Exception) {
                                Log.d(TAG, "getCartAPI:Error ${e.message}")
                                try {
                                    val message = jsonObject.get("message").asString
                                    if (message.contains("empty.", true)) {

                                        setUpView()
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Log.d(TAG, "getCartAPI:Error ${e.message}")


                                }

                            }
                        }

                    }
//                    binding.spinKit.visibility=View.GONE


                    Status.LOADING -> {
//                    binding.spinKit.visibility=View.VISIBLE
                    }
                    Status.ERROR -> {
                        //Handle Error
//                    binding.spinKit.visibility=View.GONE
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        Log.d(TAG, "addToCartAPI:Error ${it.message}")
                    }
                }
            }
        })
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            CheckoutViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[CheckoutViewModel::class.java]
    }


    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(
            getString(R.string.shopping_bag),
            isSearch = false,
            isProfile = true,
            isClose = false
        )
    }

    private fun initRefreshLayout() {
        //refresh layout on swipe
        binding.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getCartAPI()
            binding.swipeRefreshLayout.isRefreshing = false
        })
        //Adding ScrollListener to activate swipe refresh layout
        binding.shimmerView.root.setOnScrollChangeListener(View.OnScrollChangeListener { view, i, i1, i2, i3 ->
            binding.swipeRefreshLayout.isEnabled = i1 == 0
        })

        // Adding ScrollListener to getting whether we're on First Item position or not
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.swipeRefreshLayout.isEnabled =
                    layoutManagerCheckout.findFirstVisibleItemPosition() == 0
            }
        })

    }

    private fun setUpView() {
        if (listCheckout.isEmpty()) {
            MainActivity.NavCount.myBoolean = 0
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.layoutMain.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.layoutMain.visibility = View.VISIBLE
        }
    }

    private fun initView() {
        binding.textTotal.text =
            SpannableStringBuilder().bold { append("Total ") }.append("(incl. VAT)")

        binding.btnCheckout.setOnClickListener {
//            fragment_shopping
            val frag = FragmentShipping()
            val bundle: Bundle = Bundle()
            val selectedCarts = listCheckout.filter { it.isSelected == true }
            val otherPrices = listContainingGrandtotal
            Log.d("tetstcarts", "initView: " + selectedCarts);
            bundle.putSerializable("carts", selectedCarts as ArrayList<ModelCheckout>)
            bundle.putSerializable("totals", otherPrices)
            frag.arguments = bundle
            fragmentManager?.beginTransaction()?.replace(R.id.main_fragment, frag)
                ?.addToBackStack("checkout")?.commit()
        }

        binding.btnBrowse.setOnClickListener {
            (activity as MainActivity).openFragmentShop()
        }

        binding.checkBoxAll.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                for (item in listCheckout) {
                    item.isSelected = true
                }
                adapterCheckout.notifyDataSetChanged()
                calculatePrice()
            }
        }

        binding.totalPrice.text = "Rs. 00.00"
        binding.textItemSelected.text = "0/0 ITEMS Selected"
    }

    private fun initRecycler() {

        layoutManagerCheckout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapterCheckout = AdapterCheckout(requireContext(), listCheckout, this)
        binding.recyclerView.apply {
            adapter = adapterCheckout
            layoutManager = layoutManagerCheckout
        }
//        prepareList()

    }

    private fun prepareList(
        res: List<Cart>,
        cartTotalNpr: Double,
        cartTotalDollar: Double,
        checkoutResponse: CartResponse
    ) {
        apiCartList = res

        listContainingGrandtotal = checkoutResponse
        listCheckout.clear()
        Log.d(TAG, "prepareList: " + listCheckout.size);
        Log.d(TAG, "prepareList: " + apiCartList.size);

        for (item in apiCartList) {
            Log.d(TAG, "prepareList: loop"+item.productDetails.coupon?.code);
            listCheckout.add(
                ModelCheckout(
                    item.productDetails?.name ?: "",
                    item.selected?.variantId ?: 0,
                    item?.selected?.vTotal ?: 0.0,
                    item.selected?.vDollarTotal ?: 0.0,
                    item?.selected?.size ?: "",
                    item.selected.quantity,
                    item.is_selected,
                    item.selected?.imageUrl ?: "",
                    item.cartId ?: 0,
                    item.selected.colorName,
                    item.selected.colorHex,
                    item.productDetails.brand.name,
                    item.selected.sku,
                    item.productDetails.isCouponAvailable?:false,
                    item.productDetails.coupon?.let { it.code }?:run {""}
                )
            )
        }
        adapterCheckout.notifyDataSetChanged()
        setUpView()
        calculatePrice()
        binding.totalPrice.text = if (PreferenceHandler.getCurrency(context).equals("npr", true)) {
            "${getString(R.string.rs)} $cartTotalNpr."
        } else {
            "${getString(R.string.usd)} $cartTotalDollar"
        }

    }

    override fun onSizeClicked(data: ModelCheckout, position: Int) {
        //open dialog
//        showDialogSize(data, position)
    }

    override fun onQuantityClicked(data: ModelCheckout, position: Int) {
        //open dialog
//        showDialogQTY(data, position)
    }

    override fun onCheckBoxClicked(data: ModelCheckout, isChecked: Boolean, position: Int) {
//        Toast.makeText(requireContext(),"Checkbox $isChecked++++$position",Toast.LENGTH_SHORT).show()
        selectCartApi(data.cartId, position)
//        for (item in listCheckout) {
//            if (item == data) {
//                item.isSelected = isChecked
//            }
//        }
//        isCheckAll()
//        calculatePrice()
    }

    override fun onAddClick(data: ModelCheckout, position: Int) {
        addToCartAPI(data.itemId, position, data)
    }

    override fun onRemove(data: ModelCheckout, position: Int) {
        minusFromCartAPI(data.cartId, position)
    }

    override fun onCompleteRemove(data: ModelCheckout, position: Int) {
        //complete remove api
        removeFromCartAPI(data.cartId, position)

    }

    private fun isCheckAll() {
        var isAllChecked = true
        for (item in listCheckout) {
            if (!item.isSelected) {
                isAllChecked = false
                break
            }
        }
        binding.checkBoxAll.isChecked = isAllChecked
    }

    private fun calculatePrice() {
        var selected = 0
        for (item in listCheckout) {
            if (item.isSelected) {
//                total_price += if (PreferenceHandler.getCurrency(context).equals("npr", true)) {
//                    (item.priceNPR * item.qty)
//                } else {
//                    (item.priceUSD * item.qty)
//                }
                selected++
            }
        }

        binding.textItemSelected.text = "$selected/${listCheckout.count()} ITEMS Selected"
    }

//    private fun showDialogSize(data: ModelCheckout, position: Int) {
//
//        val dialogBinding = DialogShoppingSizeBinding.inflate(LayoutInflater.from(requireContext()))
//        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
//        bottomSheetDialog.setContentView(dialogBinding.root)
//
//        dialogBinding.title.text = "Size"
//        adapterCircleSize = AdapterCircleText(context, listSize).also { adapter ->
//            adapter.setCircleClickListener { listItem ->
//                for (item in listSize) {
//                    item.isSelected = item.equals(listItem)
//                }
//                adapterCircleSize.notifyDataSetChanged()
//                data.size = listItem.title
//                adapterCheckout.notifyItemChanged(position)
//            }
//        }
//        prepareListSize(data)
//
//        dialogBinding.recyclerView.apply {
//            layoutManager = GridLayoutManager(context, 5)
//            adapter = adapterCircleSize
//        }
//
//        dialogBinding.btnClose.setOnClickListener {
//            bottomSheetDialog.dismiss()
//        }
//
//        dialogBinding.btnSave.setOnClickListener {
//            saveSizeAPI(data, data.size)
//            bottomSheetDialog.dismiss()
//        }
//
//        bottomSheetDialog.show()
//    }

//    private fun showDialogQTY(data: ModelCheckout, position: Int) {
//
//        val dialogBinding = DialogShoppingSizeBinding.inflate(LayoutInflater.from(requireContext()))
//        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
//        bottomSheetDialog.setContentView(dialogBinding.root)
//        dialogBinding.title.text = "Quantity"
//
//        adapterCircleQty = AdapterCircleText(context, listQty).also { adapter ->
//            adapter.setCircleClickListener { listItem ->
//                for (item in listQty) {
//                    item.isSelected = item.equals(listItem)
//                }
//                adapterCircleQty.notifyDataSetChanged()
//                try {
//                    data.qty = listItem.title.toInt()
//                } catch (e: Exception) {
//                    data.qty = 1
//                }
//                adapterCheckout.notifyItemChanged(position)
//                calculatePrice()
//            }
//        }
//        prepareListQuantity()
//
//        dialogBinding.recyclerView.apply {
//            layoutManager = GridLayoutManager(context, 5)
//            adapter = adapterCircleQty
//        }
//
//        dialogBinding.btnClose.setOnClickListener {
//            bottomSheetDialog.dismiss()
//        }
//
//        dialogBinding.btnSave.setOnClickListener {
//            saveQuantityAPI(data, data.qty.toString())
//            bottomSheetDialog.dismiss()
//        }
//
//        bottomSheetDialog.show()
//    }

    private fun prepareListSize(data: ModelCheckout) {
        listSize.clear()
        for (cart in apiCartList) {
            for (v in cart.productDetails.variants) {
                if (data.itemId == v.variantId) {
//                    for (v in cart?.product?.variant!!) {
                    listSize.add(ModelCircleText(v.variantId ?: 0, v.size ?: "", false, "", ""))
                    //}
                }
            }

        }
//        listSize.add(ModelCircleText("s", true))
//        listSize.add(ModelCircleText("m", false))
//        listSize.add(ModelCircleText("l", false))
//        listSize.add(ModelCircleText("xl", false))
//        listSize.add(ModelCircleText("xxl", false))
        adapterCircleSize.notifyDataSetChanged()
    }

    private fun prepareListQuantity() {
        listQty.clear()
//        listQty.add(ModelCircleText("1", true))
//        listQty.add(ModelCircleText("2", false))
//        listQty.add(ModelCircleText("3", false))
//        listQty.add(ModelCircleText("4", false))
//        listQty.add(ModelCircleText("5", false))
        adapterCircleQty.notifyDataSetChanged()
    }

    private fun setShimmerLayout(isVisible: Boolean) {
        if (isVisible) {
            binding.layoutMain.visibility = View.GONE
            binding.layoutEmpty.visibility = View.GONE
            binding.shimmerFrameLayout.visibility = View.VISIBLE
            binding.shimmerFrameLayout.startShimmer()
        } else {
            binding.layoutMain.visibility = View.VISIBLE
            binding.layoutEmpty.visibility = View.GONE
            binding.shimmerFrameLayout.visibility = View.GONE
            binding.shimmerFrameLayout.stopShimmer()
        }
    }

    private fun getCartAPI() {
        listCheckout.clear()
        setShimmerLayout(true)
        viewModel.cartListAPI(PreferenceHandler.getToken(context).toString())
        viewModel.getCartListAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    setShimmerLayout(false)
                    hideError()
                    Log.d(TAG, "getCartAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val message = jsonObject.get("message").asString
                            if (message.contains("empty.", true)) {
                                setUpView()
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "getCartAPI:Error ${e.message}")
                            try {
                                val checkoutResponse =
                                    Gson().fromJson<CartResponse>(
                                        jsonObject,
                                        CartResponse::class.java
                                    )
                                if (checkoutResponse.cart != null) {
                                    if (checkoutResponse.cart.size > 0) {
                                        val cartlist = checkoutResponse.cart
                                        MainActivity.NavCount.myBoolean = cartlist.size
                                        prepareList(
                                            cartlist,
                                            checkoutResponse.cartTotalNpr,
                                            checkoutResponse.cartTotalDollar, checkoutResponse
                                        )
                                    } else {
                                        setUpView()
                                    }

                                }
                            } catch (e: Exception) {
                                Log.d(TAG, "getWishListAPI:Error2 ${e.message}")
                            }
                        }
                    }
                    adapterCheckout.notifyDataSetChanged()
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    setShimmerLayout(false)
//                    listCheckout.clear()
//                    setUpView()
                    showError(it.message.toString())
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "getCartAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun showError(it: String) {
        binding.layoutMain.visibility = View.GONE
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
        binding.layoutMain.visibility = View.VISIBLE
        if (binding.root.findViewById<LinearLayout>(R.id.layout_root) != null) {
            MyLayoutInflater().onDelete(
                binding.layoutContainer,
                binding.root.findViewById(R.id.layout_root)
            )
        }
    }

//    private fun saveSizeAPI(product: ModelCheckout, sizeSelected: String) {
//        viewModel.saveSizeAPI(
//            PreferenceHandler.getToken(context).toString(),
//            product.itemId,
//            sizeSelected
//        )
//        viewModel.getSizeAPI().observe(viewLifecycleOwner, {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    Log.d(TAG, "saveSizeAPI: ${it.data}")
//                    val jsonObject = it.data
//                    if (jsonObject != null) {
//                        showSnackBar("Size Updated")
//                        try {
//
//                        } catch (e: Exception) {
//                            Log.d(TAG, "saveSizeAPI:Error ${e.message}")
//                        }
//                    }
//                }
//                Status.LOADING -> {
//                }
//                Status.ERROR -> {
//                    //Handle Error
//                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
//                    Log.d(TAG, "saveSizeAPI:Error ${it.message}")
//                }
//            }
//        })
//    }
//
//    private fun saveQuantityAPI(product: ModelCheckout, quantitySelected: String) {
//        viewModel.saveQuantityAPI(
//            PreferenceHandler.getToken(context).toString(),
//            product.itemId,
//            quantitySelected
//        )
//        viewModel.getQuantityAPI().observe(viewLifecycleOwner, {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    Log.d(TAG, "saveQuantityAPI: ${it.data}")
//                    val jsonObject = it.data
//                    if (jsonObject != null) {
//                        showSnackBar("Quantity Updated")
//                        try {
//
//                        } catch (e: Exception) {
//                            Log.d(TAG, "saveQuantityAPI:Error ${e.message}")
//                        }
//                    }
//                }
//                Status.LOADING -> {
//                }
//                Status.ERROR -> {
//                    //Handle Error
//                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
//                    Log.d(TAG, "saveQuantityAPI:Error ${it.message}")
//                }
//            }
//        })
//    }

    private fun showSnackBar(msg: String) {
        val snackbar = Snackbar
            .make(binding.root, msg, Snackbar.LENGTH_SHORT)
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun addToCartAPI(id: Int, position: Int, old: ModelCheckout) {
        Log.d(TAG, "hitapicart: " + id);
//        viewModel.resetAddCartLiveData()
        updatePosition = position
        viewModel.addToCartAPI(PreferenceHandler.getToken(context).toString(), id)

    }

    private fun updateCartAtPosition(
        seleted: Selected,
        isClick: Boolean,
        i: Int,
        totalPriceNpr: Double? = 0.0,
        totalPriceDollar: Double? = 0.0
    ) {
        Log.d(TAG, "updateCartAtPosition: " + updatePosition);
        if (updatePosition != -1) {
            listCheckout[i].apply {
                qty = seleted.quantity
                priceNPR = seleted.vTotal
                priceUSD = seleted.vDollarTotal
                isSelected = isClick
            }
            Log.d(TAG, "updateCartAtPosition: " + i);
            isCheckAll()
            calculatePrice()
            binding.totalPrice.text =
                if (PreferenceHandler.getCurrency(context).equals("npr", true)) {
                    "${getString(R.string.rs)} $totalPriceNpr"
                } else {
                    "${getString(R.string.usd)} $totalPriceDollar"
                }
            adapterCheckout.notifyItemChanged(i)
        }
    }

    private fun updateCartAtPosition(
        position: Int,
        totalPriceNpr: Double? = 0.0,
        totalPriceDollar: Double? = 0.0
    ) {
        listCheckout.removeAt(position)
        Log.d(TAG, "updateCartAtPosition: " + position);
        isCheckAll()
        calculatePrice()
        checkIfCartEmpty()
        binding.totalPrice.text =
            if (PreferenceHandler.getCurrency(context).equals("npr", true)) {
                "${getString(R.string.rs)} $totalPriceNpr"
            } else {
                "${getString(R.string.usd)} $totalPriceDollar"
            }
        adapterCheckout.notifyItemRemoved(position)
    }

    private fun checkIfCartEmpty() {
        setUpView()
    }


    private fun minusFromCartAPI(id: Int, position: Int) {
        updatePosition = position
        viewModel.minusFromCartAPI(
            PreferenceHandler.getToken(context).toString(),
            id
        )


    }

    private fun removeFromCartAPI(id: Int, position: Int) {
        updatePosition = position
        viewModel.removeFromCartAPI(
            PreferenceHandler.getToken(context).toString(),
            id
        )


    }


    private fun selectCartApi(cartId: Int, position: Int) {
        updatePosition = position
        viewModel.selectCartApi(
            PreferenceHandler.getToken(context).toString(),
            cartId
        )

    }

}