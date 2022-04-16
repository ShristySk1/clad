package com.ayata.clad.profile.giftcard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentGiftcardBinding
import com.ayata.clad.profile.giftcard.response.Coupon
import com.ayata.clad.profile.giftcard.response.CouponReponse
import com.ayata.clad.profile.giftcard.viewmodel.GiftcardViewModel
import com.ayata.clad.profile.giftcard.viewmodel.GiftcardViewModelFactory
import com.ayata.clad.profile.viewmodel.ProfileViewModelFactory
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.MyLayoutInflater
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.utils.copyToClipboard
import com.google.gson.Gson

class FragmentGiftCard : Fragment(), AdapterGiftCard.OnItemClickListener {
    private lateinit var binding: FragmentGiftcardBinding

    private lateinit var adapterGiftCard: AdapterGiftCard
    private var listGiftCard = ArrayList<Coupon>()
    private lateinit var viewModel: GiftcardViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("testcreate", "onCreateView: here oncreate 2");

        // Inflate the layout for this fragment
        binding =
            FragmentGiftcardBinding.inflate(inflater, container, false)

        initRecycler()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    private fun setUpViewModel() {
        viewModel =
            ViewModelProvider(
                this,
                GiftcardViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
            )
                .get(GiftcardViewModel::class.java)

    }

    private fun initRecycler() {
        adapterGiftCard = AdapterGiftCard(requireContext(), listGiftCard, this)
        binding.recyclerView.apply {
            adapter = adapterGiftCard
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        prepareGiftCard()
    }
    private fun prepareGiftCard() {
//        listGiftCard.clear()
//        listGiftCard.add(
//            ModelGiftCard(
//                "Gift Card 1",
//                "Promo code is not applicable in cart",
//                "GIFT-100gh",
//                "Valid until 22 Feb 2022"
//            )
        viewModel.getCouponAPI(PreferenceHandler.getToken(context)!!)
        viewModel.observerGetCouponAPI().observe(viewLifecycleOwner, {
            Log.d("testhash", "prepareGiftCard: "+hashCode()+it.status);
            when (it.status) {
                Status.SUCCESS -> {
                    binding.progressBar.root.visibility=View.GONE
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val couponResponse =
                                Gson().fromJson<CouponReponse>(
                                    jsonObject,
                                    CouponReponse::class.java
                                )
                            if(couponResponse.coupons!=null) {
                                hideError()
                                listGiftCard.addAll(couponResponse.coupons)
                            }else{
                                showError("Coupon",couponResponse.message?:"")
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                        adapterGiftCard.notifyDataSetChanged()
                    } else {
                        Toast.makeText(context, "null", Toast.LENGTH_SHORT).show()
                    }
                }
                Status.LOADING -> {
                    binding.progressBar.root.visibility=View.VISIBLE

                }
                Status.ERROR -> {
                    //Handle Error
                    binding.progressBar.root.visibility=View.GONE
                    showError("Error",it.message.toString())
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

    }
    private fun showError(title:String,it: String) {
        MyLayoutInflater().onAddField(
            requireContext(),
            binding.root,
            R.layout.layout_error,
            Constants.ERROR_TEXT_DRAWABLE,
            title,
            it
        )

    }

    private fun hideError() {
        if (binding.root.findViewById<LinearLayout>(R.id.layout_root) != null) {
            MyLayoutInflater().onDelete(
                binding.root,
                binding.root.findViewById(R.id.layout_root)
            )
        }
    }
    override fun onGiftCardClick(data: Coupon, position: Int) {
        val text =
            requireContext().copyToClipboard(data.code)
        Log.d("testcode", "tapToCopyListener: $text")
        val toast = Toast.makeText(
            requireContext(),
            "Copied to Clipboard!", Toast.LENGTH_SHORT
        )
        toast.show()
    }


}