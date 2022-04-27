package com.ayata.clad.shopping_bag.checkout

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.text.bold
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.DialogCustomBinding
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
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.MyLayoutInflater
import com.ayata.clad.utils.PreferenceHandler
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener
import kotlin.reflect.KFunction2


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
    private var onTouchListener: RecyclerTouchListener? = null


    companion object {
        private const val TAG = "FragmentCheckout"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartCheckoutBinding.inflate(inflater, container, false)


//        (parentFragment as FragmentShoppingBag).checkoutPage()
        initAppbar()
        initView()
        setShimmerLayout(false)
        initRecycler()
//        setUpView()
        initRefreshLayout()
        getCartAPI()
        setAddObserver()
        setAddObserver()
        setMinusObserver()
        setSelectObserver()
        setRemoveObserver()
        setApplyCouponObserver()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    private fun setApplyCouponObserver() {
        binding.btnApplyCoupon.setOnClickListener {
            if (binding.etCoupon.text.toString().trim().isNotEmpty())
                viewModel.applyCouponAPI(
                    PreferenceHandler.getToken(context).toString(),
                    binding.etCoupon.text.toString()
                )
            else
                Toast.makeText(context, "Empty coupon", Toast.LENGTH_LONG).show()

        }
        viewModel.getApplyCouponResponseAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "getCartAPI: ${it.data}")
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val checkoutResponse =
                                Gson().fromJson<CartResponse>(
                                    jsonObject,
                                    CartResponse::class.java
                                )
                            if (checkoutResponse.message != null && checkoutResponse.cartTotalNpr != null) {
                                //coupon successfully applied
                                listContainingGrandtotal = checkoutResponse
                                updateTotals(checkoutResponse)
                                showSnackBar(checkoutResponse.message)
                            } else {
                                if (checkoutResponse.message != null) {
                                    Toast.makeText(
                                        context,
                                        checkoutResponse.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "getWishListAPI:Error2 ${e.message}")
                            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                    adapterCheckout.notifyDataSetChanged()
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun updateTotals(checkoutResponse: CartResponse?) {
        binding.totalPrice.text =
            if (PreferenceHandler.getCurrency(context).equals("npr", true)) {
                "${getString(R.string.rs)} ${checkoutResponse?.cartGrandTotalNpr}"
            } else {
                "${getString(R.string.usd)} ${checkoutResponse?.cartGrandTotalDollar}"
            }

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
                                Log.d("testtest", "setRemoveObserver: " + updatePosition);
                                updateCartAtPosition()
                                val checkoutResponse =
                                    Gson().fromJson<CartResponse>(
                                        jsonObject,
                                        CartResponse::class.java
                                    )
                                listContainingGrandtotal = checkoutResponse
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
        viewModel.cartListAPI(PreferenceHandler.getToken(context).toString())

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
            viewModel.cartListAPI(PreferenceHandler.getToken(context).toString())
            binding.swipeRefreshLayout.isRefreshing = false
        })
        //Adding ScrollListener to activate swipe refresh layout
        binding.shimmerView.root.setOnScrollChangeListener(View.OnScrollChangeListener { view, i, i1, i2, i3 ->
            binding.swipeRefreshLayout.isEnabled = i1 == 0
        })
//
//        // Adding ScrollListener to getting whether we're on First Item position or not
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
            if (selectedCarts.size == 0) {
                Toast.makeText(context, "Select at least one cart item.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
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
                showFillingText()
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
        // Create and add a callback
        onTouchListener = RecyclerTouchListener(activity, binding.recyclerView)
        onTouchListener!!
            .setSwipeOptionViews(R.id.add)
            .setSwipeable(
                R.id.layout1,
                R.id.rowBG,
                object : RecyclerTouchListener.OnSwipeOptionsClickListener {
                    override fun onSwipeOptionClicked(viewID: Int, position: Int) {
                        var message = ""
                        if (viewID == R.id.add) {
                            message += "Add"
                            updatePosition = position
                            showDialog(
                                "Alert!",
                                "Are you sure you want to remove this item from cart?",
                                adapterCheckout.getCartId(position),
                                position,
                                ::removeFromCartAPI
                            )

                        }
                        message += " clicked for row " + (position + 1)
                    }

                })
    }

    private fun prepareList(
        res: List<Cart>
    ) {
        apiCartList = res
        listCheckout.clear()
        Log.d(TAG, "prepareList: " + listCheckout.size);
        Log.d(TAG, "prepareList: " + apiCartList.size);

        for (item in apiCartList) {
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
                    item.productDetails.brand?.name?:"",
                    item.selected.stock_status,
                    item.productDetails.isCouponAvailable ?: false,
                    item.productDetails.coupon?.let { it.code } ?: run { "" }
                )
            )
        }
        adapterCheckout.notifyDataSetChanged()
        setUpView()
        showFillingText()
        updateTotals()


    }

    override fun onResume() {
        super.onResume()
        onTouchListener?.let {
            binding.recyclerView.addOnItemTouchListener(onTouchListener!!)

        }
    }

    override fun onPause() {
        super.onPause()
        binding.recyclerView.removeOnItemTouchListener(onTouchListener!!)
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
        updatePosition = position
        selectCartApi(data.cartId, position)
    }

    override fun onAddClick(data: ModelCheckout, position: Int) {
        updatePosition = position
        addToCartAPI(data.itemId, position, data)
    }

    override fun onRemove(data: ModelCheckout, position: Int) {
        updatePosition = position
        Log.d("myposition up", "onRemove: " + position);

        minusFromCartAPI(data.cartId, position)
    }

    override fun onCompleteRemove(data: ModelCheckout, position: Int) {
        //complete remove api
        updatePosition = position
        Log.d("myposition remove", "onRemove: " + position);
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

    private fun showFillingText() {
        var selected = 0
        for (item in listCheckout) {
            if (item.isSelected) {
                selected++
            }
        }
        binding.textItemSelected.text = "$selected/${listCheckout.count()} ITEMS Selected"
    }

    private fun updateTotals() {
        Log.d("uodatetoals", "updateTotals: here");
        binding.subTotal.setText(
            getMyPrice(
                listContainingGrandtotal.cartTotalNpr,
                listContainingGrandtotal.cartTotalDollar
            )
        )
        binding.shippingPrice.text = getMyPrice(
            listContainingGrandtotal.cartShippingPriceNpr,
            listContainingGrandtotal.cartShippingPriceDollar
        )
        binding.promoPrice.text = getMyPrice(
            listContainingGrandtotal.cartPromoDiscountNpr,
            listContainingGrandtotal.cartPromoDiscountDollar
        )
        binding.totalPrice.text = getMyPrice(
            listContainingGrandtotal.cartGrandTotalNpr,
            listContainingGrandtotal.cartGrandTotalDollar
        )
    }

    fun getMyPrice(npr: Double, dlr: Double): String {
        if (PreferenceHandler.getCurrency(context).equals("npr", true)) {
            return "${getString(R.string.rs)} ${npr}"
        } else {
            return "${getString(R.string.usd)} ${dlr}"
        }
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
                                        listContainingGrandtotal = checkoutResponse
                                        prepareList(
                                            cartlist
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
            Constants.ERROR_TEXT_DRAWABLE,
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
        viewModel.addToCartAPI(PreferenceHandler.getToken(context).toString(), id)

    }

    private fun updateCartAtPosition(
        seleted: Selected,
        isClick: Boolean,
        i: Int,
        totalPriceNpr: Double? = 0.0,
        totalPriceDollar: Double? = 0.0
    ) {
        Log.d(TAG, "updateCartAtPosition:minud " + updatePosition);
        if (updatePosition != -1) {
            listCheckout[i].apply {
                qty = seleted.quantity
                priceNPR = seleted.vTotal
                priceUSD = seleted.vDollarTotal
                isSelected = isClick
            }
            Log.d(TAG, "updateCartAtPosition: " + i);
            isCheckAll()
            showFillingText()
            updateTotals()
            adapterCheckout.notifyItemChanged(i)
        }
    }

    private fun updateCartAtPosition(
    ) {
        listCheckout.removeAt(updatePosition)
        Log.d("testlist", "updateCartAtPosition: " + updatePosition);
        Log.d("testlist", "updateCartAtPosition: " + listCheckout);
        isCheckAll()
        showFillingText()
        checkIfCartEmpty()
        updateTotals()
        adapterCheckout.notifyItemRemoved(updatePosition)
        MainActivity.NavCount.myBoolean = MainActivity.NavCount.myBoolean?.minus(1)
    }

    private fun checkIfCartEmpty() {
        setUpView()
    }


    private fun minusFromCartAPI(id: Int, position: Int) {

        viewModel.minusFromCartAPI(
            PreferenceHandler.getToken(context).toString(),
            id
        )


    }


    private fun removeFromCartAPI(id: Int, position: Int) {

        viewModel.removeFromCartAPI(
            PreferenceHandler.getToken(context).toString(),
            id
        )


    }


    private fun selectCartApi(cartId: Int, position: Int) {

        viewModel.selectCartApi(
            PreferenceHandler.getToken(context).toString(),
            cartId
        )

    }

    private fun showDialog(
        title: String,
        message: String,
        caratId: Int,
        pos: Int,
        action: KFunction2<Int, Int, Unit>
    ) {
        val bind: DialogCustomBinding =
            DialogCustomBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(requireContext(), R.style.CustomDialog)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)
        dialog?.setContentView(bind.root)
        bind.textTitle.text = title
        bind.textMsg.text =
            message
        bind.dialogBtnYes.text = "Yes"
        bind.dialogBtnYes.setOnClickListener {
            action(caratId, pos)
            dialog?.dismiss()
        }
        bind.dialogBtnNo.text = "No"
        bind.dialogBtnNo.setOnClickListener {
            dialog?.dismiss()
        }
        dialog?.show()
    }

}