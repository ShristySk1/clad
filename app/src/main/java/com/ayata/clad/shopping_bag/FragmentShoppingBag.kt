package com.ayata.clad.shopping_bag

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentShoppingBagBinding
import com.ayata.clad.shopping_bag.checkout.FragmentCheckout
import com.ayata.clad.shopping_bag.response.checkout.Cart
import com.ayata.clad.shopping_bag.viewmodel.CategoryViewModel
import com.ayata.clad.shopping_bag.viewmodel.CategoryViewModelFactory


//not used
class FragmentShoppingBag : Fragment() {

    private lateinit var binding: FragmentShoppingBagBinding
    private lateinit var viewModel: CategoryViewModel
    private lateinit var cartList: List<Cart>
    private var addressId: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentShoppingBagBinding.inflate(inflater, container, false)
        setUpViewModel()
        checkoutPage()
        initAppbar()
        initView()
        (activity as MainActivity).removeAllFragment()
        Log.d("BackCheck", "onCreateView: here")
        if (binding.fragmentShopping != null) {
            if (savedInstanceState == null) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.fragment_shopping, FragmentCheckout()).commit()
            }
        }
        return binding.root
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            CategoryViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[CategoryViewModel::class.java]
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

    private fun initView() {
        binding.layoutEmpty.visibility = View.GONE
        binding.layoutFilled.visibility = View.VISIBLE

        binding.btnBrowse.setOnClickListener {
            (activity as MainActivity).openFragmentShop()
        }
    }

    fun checkoutPage() {
        binding.textMiddle.typeface = Typeface.DEFAULT
        binding.textRight.typeface = Typeface.DEFAULT
        binding.textLeft.typeface = Typeface.DEFAULT_BOLD
        binding.progressBar.progress = 10
    }

    fun paymentPage() {
        binding.textMiddle.typeface = Typeface.DEFAULT_BOLD
        binding.textRight.typeface = Typeface.DEFAULT_BOLD
        binding.textLeft.typeface = Typeface.DEFAULT_BOLD
        binding.progressBar.progress = 100
    }

    fun shippingPage() {
        binding.textMiddle.typeface = Typeface.DEFAULT_BOLD
        binding.textRight.typeface = Typeface.DEFAULT
        binding.textLeft.typeface = Typeface.DEFAULT_BOLD
        binding.progressBar.progress = 50
    }

    fun setCarts(carts: List<Cart>) {
        cartList = carts
    }

    fun setAddress(id: Int) {
        addressId = id
    }

    fun getAddress() = addressId
    fun getCarts() = cartList

}