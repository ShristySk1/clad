package com.ayata.clad.shopping_bag.checkout

import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.bold
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.R
import com.ayata.clad.databinding.DialogShoppingSizeBinding
import com.ayata.clad.databinding.FragmentCheckoutBinding
import com.ayata.clad.shop.model.ModelShop
import com.ayata.clad.shopping_bag.adapter.AdapterCheckout
import com.ayata.clad.shopping_bag.adapter.AdapterCircleText
import com.ayata.clad.shopping_bag.model.ModelCheckout
import com.ayata.clad.shopping_bag.model.ModelCircleText
import com.google.android.material.bottomsheet.BottomSheetDialog

class FragmentCheckout : Fragment() ,AdapterCheckout.OnItemClickListener{

    private lateinit var binding: FragmentCheckoutBinding

    private lateinit var adapterCheckout: AdapterCheckout
    private var listCheckout=ArrayList<ModelCheckout>()

    private lateinit var adapterCircleSize: AdapterCircleText
    private var listSize=ArrayList<ModelCircleText>()

    private lateinit var adapterCircleQty: AdapterCircleText
    private var listQty=ArrayList<ModelCircleText>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCheckoutBinding.inflate(inflater, container, false)

        binding.textTotal.text=SpannableStringBuilder().bold { append("Total ") }.append("(incl. VAT)")

        initRecycler()

        return binding.root
    }

    private fun initRecycler(){

        adapterCheckout= AdapterCheckout(requireContext(),listCheckout,this)
        binding.recyclerView.apply {
            adapter=adapterCheckout
            layoutManager=LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        }
        prepareList()

    }

    private fun prepareList(){
        listCheckout.clear()
        listCheckout.add(ModelCheckout("Nike Air Jordan",784569,8790.0,"A",2,true,""))
        listCheckout.add(ModelCheckout("Nike Air Jordan",784579,9000.0,"A",1,false,""))

        adapterCheckout.notifyDataSetChanged()
    }

    override fun onSizeClicked(data: ModelCheckout,position:Int) {
        //open dialog
        showDialogSize(data,position)
    }

    override fun onQuantityClicked(data: ModelCheckout,position:Int) {
        //open dialog
        showDialogQTY(data, position)
    }

    override fun onCheckBoxClicked(data: ModelCheckout, isChecked: Boolean,position:Int) {
        Toast.makeText(requireContext(),"Checkbox $isChecked",Toast.LENGTH_SHORT).show()

    }

    private fun showDialogSize(data: ModelCheckout,position:Int){

        val dialogBinding= DialogShoppingSizeBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.title.text="Size"
        adapterCircleSize= AdapterCircleText(context,listSize).also { adapter ->
            adapter.setCircleClickListener { listItem->
                for(item in listSize){
                    item.isSelected = item.equals(listItem)
                }
                adapterCircleSize.notifyDataSetChanged()
                data.size=listItem.title
                adapterCheckout.notifyItemChanged(position)
            }
        }
        prepareListSize()

        dialogBinding.recyclerView.apply {
            layoutManager= GridLayoutManager(context,5)
            adapter=adapterCircleSize
        }

        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun showDialogQTY(data: ModelCheckout,position:Int){

        val dialogBinding= DialogShoppingSizeBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        dialogBinding.title.text="Quantity"

        adapterCircleQty= AdapterCircleText(context,listQty).also { adapter ->
            adapter.setCircleClickListener { listItem->
                for(item in listQty){
                    item.isSelected = item.equals(listItem)
                }
                adapterCircleQty.notifyDataSetChanged()
                try {
                    data.qty=listItem.title.toInt()
                }catch (e:Exception){
                    data.qty=0
                }
                adapterCheckout.notifyItemChanged(position)
            }
        }
        prepareListQuantity()

        dialogBinding.recyclerView.apply {
            layoutManager= GridLayoutManager(context,5)
            adapter=adapterCircleQty
        }

        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun prepareListSize(){
        listSize.clear()
        listSize.add(ModelCircleText("s",true))
        listSize.add(ModelCircleText("m",false))
        listSize.add(ModelCircleText("l",false))
        listSize.add(ModelCircleText("xl",false))
        listSize.add(ModelCircleText("xxl",false))
        adapterCircleSize.notifyDataSetChanged()
    }
    private fun prepareListQuantity(){
        listQty.clear()
        listQty.add(ModelCircleText("1",true))
        listQty.add(ModelCircleText("2",false))
        listQty.add(ModelCircleText("3",false))
        listQty.add(ModelCircleText("4",false))
        listQty.add(ModelCircleText("5",false))
        adapterCircleQty.notifyDataSetChanged()
    }

}