package com.ayata.clad.search

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentSearchBinding
import com.ayata.clad.databinding.LayoutErrorPagingBinding
import com.ayata.clad.home.FragmentHome
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.search.adapter.AdapterRecentSearch
import com.ayata.clad.utils.Constants
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.utils.ProductLoadStateAdapter
import com.ayata.clad.utils.hideKeyboard
import com.ayata.clad.view_all.adapter.AdapterViewAllProduct
import com.ayata.clad.view_all.model.ModelViewAllProduct
import com.ayata.clad.view_all.paging.ProductDetailViewAllAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FragmentSearch : Fragment(), AdapterViewAllProduct.OnItemClickListener,
    AdapterRecentSearch.OnItemClickListener, ProductDetailViewAllAdapter.onItemClickListener {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var mergeBinding: LayoutErrorPagingBinding

    private var listItem = ArrayList<ModelViewAllProduct?>()
    private lateinit var adapterRecycler: AdapterViewAllProduct

    private lateinit var adapterRecentSearch: AdapterRecentSearch
    private var listRecent = ArrayList<String>()
    var isLoading = false
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapterPaging: ProductDetailViewAllAdapter


    private var listImage = arrayListOf<String>(
        "https://freepngimg.com/thumb/categories/627.png",
        "https://images.squarespace-cdn.com/content/v1/566e100d0e4c116bdc11b2c2/1473302788755-FL48S6YFWHYC9KU18K52/245282-ceb4145ac7b646889a16b6f5dbd2f455.png?format=750w",
        "https://www.pngkit.com/png/full/70-704028_running-shoes-png-image-running-shoes-clipart-transparent.png"
    )

    var title: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        mergeBinding = LayoutErrorPagingBinding.bind(binding.root)

        val bundle = arguments
        if (bundle != null) {
            title = bundle.getString(Constants.FILTER_HOME, "")
        }
        initAppbar()
        initRecycler()
       searachObserver()
        initView()
        binding.etSearch.on(
            EditorInfo.IME_ACTION_SEARCH
        ) {
            val oldList = PreferenceHandler.getRecentSearchList(requireContext())?.toMutableList()
            oldList?.let {
                if (oldList.size <= 2) {
                    oldList.add(binding.etSearch.text.toString())
                    oldList.reverse()
                } else {
                    oldList.clear()
                    oldList.add(binding.etSearch.text.toString())
                }
                PreferenceHandler.setRecentSearchList(requireContext(), oldList.toSet())
            }
            getSearchProducts(binding.etSearch.text.toString())
            hideKeyboard()

        }

        return binding.root
    }

    private fun searachObserver() {
        viewModel.productList.observe(viewLifecycleOwner, {
            populateRecentSearch()
            adapterPaging.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[SearchViewModel::class.java]

    }

    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(false)
    }

    private fun initView() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
        binding.clearAllBtn.setOnClickListener {
            val oldList = PreferenceHandler.getRecentSearchList(requireContext())?.toMutableList()
            if (oldList != null) {
                oldList.clear()
                PreferenceHandler.setRecentSearchList(requireContext(), oldList.toSet())
            }
            listRecent.clear()
            adapterRecentSearch.notifyDataSetChanged()
        }

        binding.btnClose.visibility = View.GONE
        binding.btnClose.setOnClickListener {
            binding.textInputSearch.editText!!.text.clear()
            binding.btnClose.visibility = View.GONE
        }

        var job: Job? = null
        binding.etSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                binding.btnClose.visibility = View.VISIBLE
                if (count == 0) {
                    binding.btnClose.visibility = View.GONE
                }
                job?.cancel()
                job = MainScope().launch {
                    delay(500L)
                    binding.etSearch?.let {
                        if (it.toString().isNotEmpty()) {
                            getSearchProducts(s.toString())
                            Log.d("testsearch", "onViewCreated: ${it.toString()}");
                        }
                    }
                }
            }
        })
    }

    fun EditText.on(actionId: Int, func: () -> Unit) {
        setOnEditorActionListener { _, receivedActionId, _ ->

            if (actionId == receivedActionId) {
                func()
            }

            true
        }
    }

    private fun initRecycler() {
        adapterPaging = ProductDetailViewAllAdapter(this)
        binding.apply {
            recyclerView.apply {
                layoutManager = GridLayoutManager(context, 2)

                itemAnimator = null
                setHasFixedSize(true)
                adapter = adapterPaging.withLoadStateHeaderAndFooter(
                    header = ProductLoadStateAdapter {
                        adapterPaging.retry()
                    },
                    footer = ProductLoadStateAdapter {
                        adapterPaging.retry()
                    }
                )
            }
            mergeBinding.buttonRetry.setOnClickListener {
                adapterPaging.retry()
            }
        }
        //load state
        adapterPaging.addLoadStateListener { loadState ->
            binding.apply {
                mergeBinding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                mergeBinding.textViewError.isVisible = loadState.source.refresh is LoadState.Error
                mergeBinding.buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                //for empty view
                if (loadState.source.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached
                    && adapterPaging.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    mergeBinding.textViewEmpty.isVisible = true

                } else {
                    mergeBinding.textViewEmpty.isVisible = false
                }

            }
        }


        adapterRecentSearch = AdapterRecentSearch(requireContext(), listRecent, this)
        binding.recyclerViewRecent.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = adapterRecentSearch
        }
        populateRecentSearch()
    }

    private fun populateRecentSearch() {
        listRecent.clear()
        val oldList = PreferenceHandler.getRecentSearchList(requireContext())?.toMutableList()
        if (oldList != null) {
            listRecent.addAll(oldList)
        }
        adapterRecentSearch.notifyDataSetChanged()
    }

    private fun getSearchProducts(filter: String) {
        viewModel.searchProduct(
            filter,
            PreferenceHandler.getToken(context).toString()
        )
    }


    private fun initScrollListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager?
                if (!isLoading) {
                    if (layoutManager != null &&
                        layoutManager.findLastCompletelyVisibleItemPosition() == listItem.size - 1
                    ) {
                        //bottom of list!
                        loadMore()
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        listItem.add(null)
        adapterRecycler.notifyItemInserted(listItem.size - 1)
        val handler = Handler()
        handler.postDelayed({
            listItem.removeAt(listItem.size - 1)
            val scrollPosition: Int = listItem.size
            adapterRecycler.notifyItemRemoved(scrollPosition)
            var currentSize = scrollPosition
            val nextLimit = currentSize + 9
            Log.d("infinitescroll", "loadMore: $scrollPosition --$nextLimit")
            while (currentSize - 1 < nextLimit) {
                listItem.add(
                    ModelViewAllProduct(
                        "Product $currentSize", "500.0", "80",
                        "Company ABC", false, listImage.random()
                    )
                )
                currentSize++
            }
            adapterRecycler.notifyDataSetChanged()
            isLoading = false
        }, 2000)
    }

    override fun onProductClickListener(data: ModelViewAllProduct) {
        parentFragmentManager.beginTransaction().replace(
            R.id.main_fragment,
            FragmentProductDetail()
        ).addToBackStack(null).commit()
    }

    override fun onWishListClicked(data: ModelViewAllProduct, position: Int) {
        val isOnWishList = listItem[position]!!.isOnWishList
        val snackBar = if (isOnWishList) {
            Snackbar
                .make(binding.root, "Product removed from wishlist", Snackbar.LENGTH_SHORT)
        } else {
            Snackbar
                .make(binding.root, "Product added to wishlist", Snackbar.LENGTH_SHORT)
        }
//                .setAction("RETRY") { }
        snackBar.setActionTextColor(Color.WHITE)
        snackBar.show()
        listItem[position]!!.isOnWishList = (!isOnWishList)
        adapterRecycler.notifyItemChanged(position)
    }

    override fun onCloseClicked(data: String, position: Int) {
        val oldList = PreferenceHandler.getRecentSearchList(requireContext())?.toMutableList()
        if (oldList != null) {
            oldList.removeAt(position)
            PreferenceHandler.setRecentSearchList(requireContext(), oldList.toSet())
        }
        listRecent.removeAt(position)
        adapterRecentSearch.notifyDataSetChanged()
    }

    override fun onRecentSearchClicked(data: String, position: Int) {
    }

    override fun onItemClick(product: ProductDetail, position: Int) {
        val bundle = Bundle()
        bundle.putSerializable(FragmentHome.PRODUCT_DETAIL, product)
        val fragmentProductDetail = FragmentProductDetail()
        fragmentProductDetail.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, fragmentProductDetail)
            .addToBackStack(null).commit()
    }

    override fun onWishListClick(product: ProductDetail, position: Int) {

    }

}