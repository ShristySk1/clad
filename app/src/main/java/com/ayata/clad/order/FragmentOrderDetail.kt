package com.ayata.clad.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentOrderDetailBinding
import com.ayata.clad.utils.PreferenceHandler

class FragmentOrderDetail : Fragment() {
    private lateinit var list_orderTrack: ArrayList<ModelOrderTrack>
    lateinit var binding: FragmentOrderDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentOrderDetailBinding.inflate(inflater, container, false)
        initAppbar()
        populateData()
        binding.recyclerOrderTracker.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AdapterOrderTrack(requireContext(), list_orderTrack)
        }

        if(PreferenceHandler.getCurrency(context).equals("npr",true)){
           binding.include.price.text=getString(R.string.rs)+" 7850"
        }else{
            binding.include.price.text=getString(R.string.usd)+" 120"
        }
        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = true, isClear = false,
            textTitle = getString(R.string.order_details),
            textDescription = "X935-12SC"
        )
    }

    private fun populateData() {
        list_orderTrack = ArrayList<ModelOrderTrack>()
        val titles = arrayOf(
            ModelOrderTrack.ORDER_TYPE_PLACED,
            ModelOrderTrack.ORDER_TYPE_DISPATCHED,
            ModelOrderTrack.ORDER_TYPE_TRANSIT,
            ModelOrderTrack.ORDER_TYPE_DELIVERED
        )
        titles.reverse()
        val conditionalStatus = ModelOrderTrack.ORDER_TYPE_DELIVERED
        val descriptions = arrayOf(
            "11:58 AM Jan 22, 2020",
            "11:58 AM Jan 22, 2020",
            "11:58 AM Jan 22, 2020",
            "11:58 AM Jan 22, 2020"
        )
        descriptions.reverse()
        val colorInComplete: Int = R.color.colorGray
        val colorCompleted: Int = R.color.colorGray
        val colorCurrent: Int = R.color.colorBlack
        var setNone = false
        for (i in titles.indices) {
            if (titles[i].toLowerCase().trim { it <= ' ' } != conditionalStatus.toLowerCase()
                    .trim { it <= ' ' }) {
                if (setNone) {
                    //set rest to grayed out
                    list_orderTrack.add(
                        ModelOrderTrack(
                            titles[i],
                            descriptions[i], ModelOrderTrack.ORDER_TYPE_NONE, colorInComplete, false
                        )
                    )
                } else {
                    list_orderTrack.add(
                        ModelOrderTrack(
                            titles[i],
                            descriptions[i], titles[i], colorCompleted, false
                        )
                    )
                }
            } else {
                list_orderTrack.add(
                    ModelOrderTrack(
                        titles[i],
                        descriptions[i], titles[i], colorCurrent, true
                    )
                )
                setNone = true //set it to true after we found the exact title
            }
        }
    }
}