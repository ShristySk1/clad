package com.ayata.clad.profile.address

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentAddressDetailBinding
import com.ayata.clad.profile.address.adapter.AdapterAddress
import com.ayata.clad.shopping_bag.model.ModelShippingAddress

class FragmentAddressDetail : Fragment(),AdapterAddress.OnItemClickListener {

    private lateinit var binding:FragmentAddressDetailBinding
    private lateinit var adapterAddress:AdapterAddress
    private var listAddress=ArrayList<ModelShippingAddress>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAddressDetailBinding.inflate(inflater, container, false)

        initAppbar()
        initRecycler()
        binding.addNewBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment,FragmentAddressAdd()).addToBackStack(null).commit()
        }
        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "Address Detail",
            textDescription = ""
        )
    }

    private fun initRecycler(){

        adapterAddress= AdapterAddress(context,listAddress,this)
        binding.recyclerView.apply {
            adapter=adapterAddress
            layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        }

        prepareListAddress()
        if(listAddress.size>=2){
            binding.addNewBtn.visibility=View.GONE
        }else{
            binding.addNewBtn.visibility=View.VISIBLE
        }
    }

    private fun prepareListAddress(){

        listAddress.clear()
        listAddress.add(ModelShippingAddress("Home","New Baneshwor - 10,\nKathmandu, Nepal",true))
        listAddress.add(ModelShippingAddress("Office","Kuleshwor - 12,\nLalitpur, Nepal",false))

        adapterAddress.notifyDataSetChanged()
    }

    override fun onEditClicked(data: ModelShippingAddress, position: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment,FragmentAddressUpdate()).addToBackStack(null).commit()
    }

}