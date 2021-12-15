package com.ayata.clad.shopping_bag

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.ayata.clad.R
import com.ayata.clad.databinding.DialogShoppingSizeBinding
import com.ayata.clad.databinding.FragmentShoppingBagBinding
import com.ayata.clad.shopping_bag.adapter.AdapterCircleText
import com.ayata.clad.shopping_bag.model.ModelCircleText
import com.google.android.material.bottomsheet.BottomSheetDialog

class FragmentShoppingBag : Fragment() {

    private lateinit var binding: FragmentShoppingBagBinding

    private lateinit var adapterCircleText: AdapterCircleText
    private var listText=ArrayList<ModelCircleText>()

    private lateinit var adapterCircleText2: AdapterCircleText
    private var listText2=ArrayList<ModelCircleText>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentShoppingBagBinding.inflate(inflater, container, false)

        binding.btn.setOnClickListener {
            showDialogSize()
        }
        initRecycler()
        return binding.root
    }

    private fun initRecycler(){

        adapterCircleText2= AdapterCircleText(context,listText2).also { adapter ->
            adapter.setCircleClickListener { data->
                for(item in listText2){
                    item.isSelected = item.equals(data)
                }
                adapterCircleText2.notifyDataSetChanged()
            }
        }

        binding.recyclerView1.apply {
            layoutManager=GridLayoutManager(context,5)
            adapter=adapterCircleText2
        }
        prepareList2()


    }

    private fun prepareList(){
        listText.clear()
        listText.add(ModelCircleText("s",true))
        listText.add(ModelCircleText("m",false))
        listText.add(ModelCircleText("l",false))
        listText.add(ModelCircleText("xl",false))
        listText.add(ModelCircleText("xxl",false))
        adapterCircleText.notifyDataSetChanged()
    }
    private fun prepareList2(){
        listText2.clear()
        listText2.add(ModelCircleText("1",true))
        listText2.add(ModelCircleText("2",false))
        listText2.add(ModelCircleText("3",false))
        listText2.add(ModelCircleText("4",false))
        listText2.add(ModelCircleText("5",false))
        adapterCircleText2.notifyDataSetChanged()
    }

    private fun showDialogSize(){

        val dialogBinding=DialogShoppingSizeBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)

        adapterCircleText= AdapterCircleText(context,listText).also { adapter ->
            adapter.setCircleClickListener { data->
                for(item in listText){
                    item.isSelected = item.equals(data)
                }
                adapterCircleText.notifyDataSetChanged()
            }
        }
        prepareList()

        dialogBinding.recyclerView.apply {
            layoutManager=GridLayoutManager(context,5)
            adapter=adapterCircleText
        }

        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

}