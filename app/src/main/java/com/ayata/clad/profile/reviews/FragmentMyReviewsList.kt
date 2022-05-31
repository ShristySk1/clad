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
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentMyReviewsListBinding
import com.ayata.clad.profile.reviews.adapter.AdapterReviewsViewPager
import com.ayata.clad.profile.reviews.model.ModelReview
import com.ayata.clad.profile.reviews.model.Review
import com.ayata.clad.profile.reviews.viewmodel.ReviewViewModel
import com.ayata.clad.profile.reviews.viewmodel.ReviewViewModelFactory
import com.ayata.clad.utils.*
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
    var initialPositionOfTab=0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentMyReviewsListBinding.inflate(inflater, container, false)
        initAppbar()
        initRefreshLayout()
        viewModel.reviewAPI(PreferenceHandler.getToken(requireContext())!!)
        setTabLayout()
        observeGetReview()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }
    private fun initRefreshLayout() {
        //refresh layout on swipe
        binding.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            viewModel.reviewAPI(PreferenceHandler.getToken(requireContext())!!)
            binding.swipeRefreshLayout.isRefreshing = false
        })
        //Adding ScrollListener to activate swipe refresh layout
//        binding.shimmerView.root.setOnScrollChangeListener(View.OnScrollChangeListener { view, i, i1, i2, i3 ->
//            binding.swipeRefreshLayout.isEnabled = i1 == 0
//        })
//
//        // Adding ScrollListener to getting whether we're on First Item position or not
//        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                binding.swipeRefreshLayout.isEnabled =
//                    layoutManagerCheckout.findFirstVisibleItemPosition() == 0
//            }
//        })

    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            ReviewViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[ReviewViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        isApiFetched=false
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
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                initialPositionOfTab=position
                super.onPageSelected(position)
            }
        })
    }

    private fun getDataFromApi() {
        val dataReviewed =
            mutableListOf<ModelReview>(ModelReview("A", true), ModelReview("C", true))
        val dataUnreviewed = mutableListOf<ModelReview>(ModelReview("B", false))
        listReviewUnreviewed = arrayListOf()
        listReviewed = arrayListOf()

//        setData(dataUnreviewed, listReviewUnreviewed)
//        setData(dataReviewed, listReviewed)
    }

    private fun observeGetReview() {
        val livedata = viewModel.observeGetReviewApi()
        livedata
            .observe(viewLifecycleOwner) {
                Log.d("teststatus", "observeGetReview: "+it.status);
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
                                    Log.d("calledme", "setting reviewed and unreviewd: ");
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
//        MyLayoutInflater().onAddField(
//            requireContext(),
//            binding.rootContainer,
//            R.layout.layout_error,
//            Constants.ERROR_TEXT_DRAWABLE,
//            "Error!",
//            it
//        )

        Caller().error(Constants.ERROR_TEXT,it,requireContext(),binding.rootContainer)

    }

    private fun hideError() {
        Caller().hideErrorEmpty(binding.rootContainer)

    }

    private fun setData(unreviewed: MutableList<Review>, reviewed: MutableList<Review>) {
        isApiFetched = true
        listReviewUnreviewed.clear()
        listReviewed.clear()
        listReviewed.addAll(reviewed)
        listReviewUnreviewed.addAll(unreviewed)
        Log.d("testreviewlist", "setData: reviewed " + listReviewed);
        Log.d("testreviewlist", "setData: unreviewd " + listReviewUnreviewed);

//        val currentPosition: Int = 0
        isFetchedApi = true
        Log.d("testmybooloean", "setData: " + isApiFetched);
        Log.d("testnumber", "setData: " + initialPositionOfTab);
        adapterReviewViewPager.notifyDataSetChanged()
        binding.viewPager.adapter = null
        binding.viewPager.adapter = adapterReviewViewPager
        if(initialPositionOfTab==1) {
            binding.viewPager.setCurrentItem(initialPositionOfTab, false)
        }
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