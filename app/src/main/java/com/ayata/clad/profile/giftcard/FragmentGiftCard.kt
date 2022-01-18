package com.ayata.clad.profile.giftcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.databinding.FragmentAccountBinding
import com.ayata.clad.databinding.FragmentGiftcardBinding

class FragmentGiftCard : Fragment(),AdapterGiftCard.OnItemClickListener {
    private lateinit var binding: FragmentGiftcardBinding

    private lateinit var adapterGiftCard: AdapterGiftCard
    private var listGiftCard=ArrayList<ModelGiftCard>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentGiftcardBinding.inflate(inflater, container, false)

        initRecycler()
        return binding.root
    }

    private fun initRecycler(){
        adapterGiftCard= AdapterGiftCard(requireContext(),listGiftCard,this)
        binding.recyclerView.apply {
            adapter=adapterGiftCard
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        }
        prepareGiftCard()
    }

    private fun prepareGiftCard(){
        listGiftCard.clear()
        listGiftCard.add(ModelGiftCard("Gift Card 1","Promo code is not applicable in cart","GIFT-100gh","Valid until 22 Feb 2022"))
        listGiftCard.add(ModelGiftCard("Gift Card 2","Promo code is not applicable in cart","GIFT-ae400","Valid until 01 Feb 2022"))
        listGiftCard.add(ModelGiftCard("Gift Card 3","Promo code is not applicable in cart","GIFT-70000","Valid until 15 Feb 2022"))
        listGiftCard.add(ModelGiftCard("Gift Card 4","Promo code is not applicable in cart","GIFT-100","Valid until 30 Jan 2022"))
        adapterGiftCard.notifyDataSetChanged()
    }

    override fun onMostPopularClicked(data: ModelGiftCard, position: Int) {
        //
    }


}