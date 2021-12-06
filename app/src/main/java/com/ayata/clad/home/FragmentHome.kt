package com.ayata.clad.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentHomeBinding

class FragmentHome : Fragment() {


    private var liststory = ArrayList<Modelcircularlist>()
    lateinit var activityFragmentHomeBinding: FragmentHomeBinding
    private lateinit var adapterFragmentcircle: AdapterFragmentcircle
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {

        activityFragmentHomeBinding= FragmentHomeBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment

        initRecyclerView()

//       section for circular story
        adapterFragmentcircle = AdapterFragmentcircle(context, liststory)
        with(activityFragmentHomeBinding.recyclerStory)
        {
            setHasFixedSize(true)
            isNestedScrollingEnabled=false
            apply {
                layoutManager=LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
                adapter=adapterFragmentcircle
            }
        }
        PrepareDataforStory()
        adapterFragmentcircle.notifyDataSetChanged()
//        setion close adapater

        return activityFragmentHomeBinding.root
    }

    private fun PrepareDataforStory() {
 liststory.clear()
        liststory.add(Modelcircularlist("https://static.parade.com/wp-content/uploads/2020/04/4.26_Scarlett-Johanson-FTR.jpg","New In"))
        liststory.add(Modelcircularlist("https://static.parade.com/wp-content/uploads/2020/04/4.26_Scarlett-Johanson-FTR.jpg","Summer"))
        liststory.add(Modelcircularlist("https://static.parade.com/wp-content/uploads/2020/04/4.26_Scarlett-Johanson-FTR.jpg","Activewear"))
        liststory.add(Modelcircularlist("https://static.parade.com/wp-content/uploads/2020/04/4.26_Scarlett-Johanson-FTR.jpg","Basic"))
        liststory.add(Modelcircularlist("https://static.parade.com/wp-content/uploads/2020/04/4.26_Scarlett-Johanson-FTR.jpg","Сouples"))
    }


    private fun initRecyclerView() {

    }


}