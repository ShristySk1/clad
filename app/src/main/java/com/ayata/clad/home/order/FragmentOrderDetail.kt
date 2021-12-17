package com.ayata.clad.home.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentOrderDetailBinding

class FragmentOrderDetail : Fragment() {
    private lateinit var list_orderTrack: ArrayList<ModelOrderTrack>
    lateinit var activityFragmentOrderDetailBinding: FragmentOrderDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activityFragmentOrderDetailBinding =
            FragmentOrderDetailBinding.inflate(inflater, container, false)

        return activityFragmentOrderDetailBinding.root
    }
//    private fun populateData() {
//        list_orderTrack=ArrayList<ModelOrderTrack>()
//        val titles = arrayOf(
//            ModelOrderTrack.ORDER_TYPE_PLACED,
//            ModelOrderTrack.ORDER_TYPE_CONFIRMED,
//            ModelOrderTrack.ORDER_TYPE_PROCESS,
//            ModelOrderTrack.ORDER_TYPE_SHIP,
//            ModelOrderTrack.ORDER_TYPE_DELIVERY,
//            ModelOrderTrack.ORDER_TYPE_DELIVERED
//        )
//        val descriptions = arrayOf(
//            "Order Placed",
//            "Order Dispatched",
//            "Order in Transit",
//            "Delivered Successfully"
//        )
//        val colorInComplete: Int = R.color.colorGrayLight
//        val colorCompleted: Int = R.color.colorPriceTag
//        val colorCurrent: Int = R.color.colorPrimary
//        var setNone = false
//        for (i in titles.indices) {
//            if (titles[i].toLowerCase().trim { it <= ' ' } != conditionalStatus.toLowerCase()
//                    .trim { it <= ' ' }) {
//                if (setNone) {
//                    //set rest to grayed out
//                    list_orderTrack.add(
//                        ModelOrderTrack(
//                            titles[i],
//                            descriptions[i], ModelOrderTrack.ORDER_TYPE_NONE, colorInComplete, false
//                        )
//                    )
//                } else {
//                    list_orderTrack.add(
//                        ModelOrderTrack(
//                            titles[i],
//                            descriptions[i], titles[i], colorCompleted, false
//                        )
//                    )
//                }
//            } else {
//                list_orderTrack.add(
//                    ModelOrderTrack(
//                        titles[i],
//                        descriptions[i], titles[i], colorCurrent, true
//                    )
//                )
//                setNone = true //set it to true after we found the exact title
//            }
//        }
//    }
}