package com.ayata.clad.thrift

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentThriftBinding
import com.ayata.clad.thrift.adapter.AdapterThrift
import com.ayata.clad.thrift.model.ModelThrift


class FragmentThrift : Fragment() ,AdapterThrift.OnThriftClickListener{

    private lateinit var binding: FragmentThriftBinding

    private lateinit var adapterThrift: AdapterThrift
    private var listThrift=ArrayList<ModelThrift>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentThriftBinding.inflate(inflater, container, false)

        initAppbar()
        initRecycler()
        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(getString(R.string.thrift),
            isSearch = false,
            isProfile = true,
            isClose = false
        )
    }


    private fun initRecycler(){

        adapterThrift= AdapterThrift(context,listThrift,this)
        binding.recyclerView.apply {
            adapter=adapterThrift
            layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        }

        prepareThriftList()
    }

    private fun prepareThriftList(){

        listThrift.clear()
        listThrift.add(ModelThrift("H&M Studios","Few min ago","userna",
            getString(R.string.randomText),12000.0,5000.0,false,false))
        listThrift.add(ModelThrift("H&M Studios","Few min ago","userna",
            getString(R.string.randomText),1400.0,800.0,true,true))
        listThrift.add(ModelThrift("H&M Studios","Few min ago","userna",
            getString(R.string.randomText),120.0,0.0,false,true))

        adapterThrift.notifyDataSetChanged()
    }

    override fun onMoreClicked(data: ModelThrift) {
        Toast.makeText(context,"More Click",Toast.LENGTH_SHORT).show()
    }

    override fun onLikeClicked(data: ModelThrift,isLike:Boolean) {
        Toast.makeText(context,"Like Click $isLike",Toast.LENGTH_SHORT).show()
    }

    override fun onCommentClicked(data: ModelThrift) {
        Toast.makeText(context,"Comment Click",Toast.LENGTH_SHORT).show()
    }

    override fun onSendClicked(data: ModelThrift) {
        Toast.makeText(context,"Send Click",Toast.LENGTH_SHORT).show()
    }

    override fun onBookmarkClicked(data: ModelThrift,isBookMark:Boolean) {
        Toast.makeText(context,"Bookmark Click $isBookMark",Toast.LENGTH_SHORT).show()
    }

    override fun onLogoClicked(data: ModelThrift) {
        Toast.makeText(context,"Logo/Name Click",Toast.LENGTH_SHORT).show()
    }

}