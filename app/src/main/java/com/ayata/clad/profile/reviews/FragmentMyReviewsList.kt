package com.ayata.clad.profile.reviews

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentMyReviewsListBinding
import com.ayata.clad.profile.reviews.adapter.AdapterReviewsViewPager
import com.ayata.clad.profile.reviews.model.ModelReview
import com.ayata.clad.profile.reviews.model.Review
import com.ayata.clad.profile.reviews.utils.observeOnce
import com.ayata.clad.profile.reviews.viewmodel.ReviewViewModel
import com.ayata.clad.profile.reviews.viewmodel.ReviewViewModelFactory
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.MyLayoutInflater
import com.ayata.clad.utils.PreferenceHandler
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class FragmentMyReviewsList : Fragment() {
    private lateinit var titles: Array<String>
    lateinit var binding: FragmentMyReviewsListBinding
    private lateinit var listReviewUnreviewed: ArrayList<Review>
    private lateinit var listReviewed: ArrayList<Review>
    private lateinit var adapterReviewViewPager: AdapterReviewsViewPager
    private lateinit var listFragment: ArrayList<Fragment>
    private lateinit var viewModel: ReviewViewModel
    private var isFetchedApi = false

    companion object {
        var isApiFetched = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentMyReviewsListBinding.inflate(inflater, container, false)
        initAppbar()
        setUpViewModel()
        setTabLayout()
        return binding.root
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            ReviewViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[ReviewViewModel::class.java]
        viewModel.reviewAPI(PreferenceHandler.getToken(requireContext())!!)
    }

    private fun initAppbar() {
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "My Reviews",
            textDescription = ""
        )
        (activity as MainActivity).showBottomNavigation(false)
    }

    private fun setTabLayout() {
        getDataFromApi()
        val fragmentUnReviewed =
            getMyFragment(FragmentMyReviews(), listReviewUnreviewed, isFetchedApi)
        val fragmentReviewed = getMyFragment(FragmentMyReviews(), listReviewed, isFetchedApi)
        listFragment = arrayListOf()
        listFragment.clear()
        listFragment.add(fragmentUnReviewed)
        listFragment.add(fragmentReviewed)
        adapterReviewViewPager = AdapterReviewsViewPager(this)
        adapterReviewViewPager.addFragmentList(listFragment)
        binding.viewPager.adapter = adapterReviewViewPager
        titles =
            arrayOf("Unreviewed", "Reviewed")
        TabLayoutMediator(
            binding.tabLayoutLeadershipBoard, binding.viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                tab.text = titles[position]
            }
        ).attach()
    }

    private fun getDataFromApi() {
        val dataReviewed =
            mutableListOf<ModelReview>(ModelReview("A", true), ModelReview("C", true))
        val dataUnreviewed = mutableListOf<ModelReview>(ModelReview("B", false))
        listReviewUnreviewed = arrayListOf()
        listReviewed = arrayListOf()
        observeGetReview()
//        setData(dataUnreviewed, listReviewUnreviewed)
//        setData(dataReviewed, listReviewed)
    }

    private fun observeGetReview() {
        val livedata = viewModel.observeGetReviewApi()
        livedata
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        hideProgress()
                        hideError()
                        val jsonObject = it.data
                        if (jsonObject != null) {
                            Log.d("listsixe", "observeGetReview: " + jsonObject.toString());
                            try {
                                val reviewResponse: Type =
                                    object : TypeToken<ArrayList<Review?>?>() {}.type
                                val list: ArrayList<Review> = Gson().fromJson(
                                    jsonObject.get("reviews").asJsonArray,
                                    reviewResponse
                                )
                                Log.d("listsixe", "observeGetReview: " + list);
                                if (list.size > 0) {

                                    val unreviewed =
                                        list.filter { it.reviewDetails.isReviewed == false }
                                            .toMutableList()
                                    val reviewed =
                                        list.filter { it.reviewDetails.isReviewed == true }
                                            .toMutableList()
                                    setData(unreviewed, reviewed)
                                } else {

                                    setData(arrayListOf(), arrayListOf())
                                }
                            } catch (e: Exception) {
                                Log.d("catchme", "prepareAPI: ${e.message}")
                                showError("${e.message}")
                            }
                        }
                    }
                    Status.LOADING -> {
                        showProgress()
                        hideError()
                    }
                    Status.ERROR -> {
                        //Handle Error
                        hideProgress()
                        showError(it.message.toString())
                    }
                }
            }
    }


    fun showProgress() {
        binding.progressBar.rootContainer.visibility = View.VISIBLE

    }

    fun hideProgress() {
        binding.progressBar.rootContainer.visibility = View.GONE
    }

    private fun showError(it: String) {
        MyLayoutInflater().onAddField(
            requireContext(),
            binding.root,
            R.layout.layout_error,
            Constants.ERROR_TEXT_DRAWABLE,
            "Error!",
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

    private fun setData(unreviewed: MutableList<Review>, reviewed: MutableList<Review>) {
        isApiFetched = true
        listReviewUnreviewed.clear()
        listReviewed.clear()
        listReviewed.addAll(reviewed)
        listReviewUnreviewed.addAll(unreviewed)
        Log.d("testreviewlist", "setData: reviewed " + listReviewed);
        Log.d("testreviewlist", "setData: unreviewd " + listReviewUnreviewed);

        val currentPosition: Int = 0
        isFetchedApi = true
        Log.d("testreviewlist", "setData: " + isFetchedApi);
        adapterReviewViewPager.notifyDataSetChanged()
        binding.viewPager.adapter = null
        binding.viewPager.adapter = adapterReviewViewPager
        binding.viewPager.currentItem = currentPosition
    }

    private fun getMyFragment(
        fragmentToday: Fragment,
        list: ArrayList<Review>,
        isFetched: Boolean
    ): Fragment {
        val bundle = Bundle()
        bundle.putSerializable("datas", list)
        bundle.putBoolean("isFetched", isFetched)
        Log.d("testdatas here", "getMyFragment: " + isFetched);
        fragmentToday.arguments = bundle
        return fragmentToday
    }

}