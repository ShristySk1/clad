package com.ayata.clad.filter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.DialogFilterBinding
import com.ayata.clad.databinding.DialogFilterRangeBinding
import com.ayata.clad.databinding.FragmentFilterBinding
import com.ayata.clad.filter.filterdialog.AdapterFilterContent
import com.ayata.clad.filter.filterdialog.MyFilterContentViewItem
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.productlist.viewmodel.ProductListViewModel
import com.ayata.clad.productlist.viewmodel.ProductListViewModelFactory
import com.ayata.clad.profile.account.AccountViewModel
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.utils.removeBracket
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson

class FragmentFilter : Fragment() {
    companion object{
        val defaultMinPrice = "0"
        val defaultMaxPrice = "100000"
        var MY_LIST= giveMyOrginalList()
        fun giveMyOrginalList()=listOf(
            MyFilterRecyclerViewItem.Title(1, "Sort by", "All"),
            MyFilterRecyclerViewItem.Title(3, "Brand", "All"),
            MyFilterRecyclerViewItem.Title(4, "Size", "All"),
            MyFilterRecyclerViewItem.Color(7, "Color", listOf(MyColor("All", null),)),
            MyFilterRecyclerViewItem.Title(6, "Price Range", "Rs. ${defaultMinPrice} - Rs. ${defaultMaxPrice}")
        )
    }
    private lateinit var binding: FragmentFilterBinding
    lateinit var myAdapter: AdapterFilter
    lateinit var myCurrentFilterList:MutableList<MyFilterRecyclerViewItem>
    lateinit var sizeList: List<MyFilterContentViewItem.MultipleChoice>
    lateinit var colorList: List<MyFilterContentViewItem.MultipleChoiceColor>
    lateinit var brandList: List<MyFilterContentViewItem.MultipleChoice>
    lateinit var sortList: List<MyFilterContentViewItem.SingleChoice>

    var newMax = defaultMaxPrice
    var newMin = defaultMinPrice
    private lateinit var viewModel: ProductListViewModel
    private lateinit var testViewModel: AccountViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        initAppbar()

        binding.btnApplyFilter.setOnClickListener {
            MY_LIST=myCurrentFilterList.toList()
            val listColor =
                (myAdapter.items).filterIsInstance(MyFilterRecyclerViewItem.Color::class.java)
            var mycolorFilter = ""
            var mySizeFilter = ""
            var myBrandFilter = ""
            var mysortByFilter = ""
            listColor.forEach {
                mycolorFilter = it.colorList.map { it.color }.toString().removeBracket().toString()
            }
            mysortByFilter =
                (myAdapter.items.get(0) as MyFilterRecyclerViewItem.Title).slug.toString()
            myBrandFilter =
                (myAdapter.items.get(1) as MyFilterRecyclerViewItem.Title).slug.toString()
            mySizeFilter =
                (myAdapter.items.get(2) as MyFilterRecyclerViewItem.Title).subTitle.toString()
            Log.d("testmycolor", "onCreateView: " + mycolorFilter);
            testViewModel.setProductListFromCategory(
                PreferenceHandler.getToken(requireContext())!!,
                (activity as MainActivity).getFilterSlug(),
                refactorAllFromList(mySizeFilter),
                refactorAllFromList(myBrandFilter),
                refactorAllFromList(mycolorFilter),
                refactorAllFromList(mysortByFilter),
                refactorAllFromList(newMin),
                refactorAllFromList(newMax)
            )
            parentFragmentManager.popBackStackImmediate()
        }
        //set initial value
        myCurrentFilterList= mutableListOf<MyFilterRecyclerViewItem>()
        myCurrentFilterList.addAll(MY_LIST)
        sizeList = listOf(
            MyFilterContentViewItem.MultipleChoice("M", false),
            MyFilterContentViewItem.MultipleChoice("S", false),
            MyFilterContentViewItem.MultipleChoice("L", false),
            MyFilterContentViewItem.MultipleChoice("XL", false),
        )
        colorList = listOf(
            MyFilterContentViewItem.MultipleChoiceColor("Blue", false, "#0000FF"),
            MyFilterContentViewItem.MultipleChoiceColor("Black", false, "#000000"),
            MyFilterContentViewItem.MultipleChoiceColor("Red", false, "#FF0000"),
            MyFilterContentViewItem.MultipleChoiceColor("Yellow", false, "#FFFF00"),
        )
        sortList = listOf(
            MyFilterContentViewItem.SingleChoice("All", true, "most_popular"),
            MyFilterContentViewItem.SingleChoice("Most Popular", false, "most_popular"),
            MyFilterContentViewItem.SingleChoice("Cheapest (low - high)", false, "cheapest"),
            MyFilterContentViewItem.SingleChoice("Most Expensive (high - low)", false, "expensive"),
            MyFilterContentViewItem.SingleChoice("Latest", false, "latest"),
        )
        brandList = listOf(
            MyFilterContentViewItem.MultipleChoice("Goldstar", false, "goldstar"),
            MyFilterContentViewItem.MultipleChoice("Bishrom", false, "bishrom"),
            MyFilterContentViewItem.MultipleChoice("Hills & Clouds", false, "hills-and-clouds"),
            MyFilterContentViewItem.MultipleChoice("Newmew", false, "newmew"),
            MyFilterContentViewItem.MultipleChoice("Phalano Luga", false, "phalano-luga"),
            MyFilterContentViewItem.MultipleChoice("Fibro", false, "fibro"),
            MyFilterContentViewItem.MultipleChoice("Creative Touch", false, "creative-touch"),
            MyFilterContentViewItem.MultipleChoice("Caliber", false, "caliber"),
            MyFilterContentViewItem.MultipleChoice("Fuloo", false, "fuloo"),
            MyFilterContentViewItem.MultipleChoice("Gofi", false, "gofi"),

            )
        setUpRecyclerView()
        (activity as MainActivity).setClearAllListener {
            Log.d("testclear", "onCreateView: here clear one"+myAdapter.items);
            Log.d("testclear", "onCreateView: here clear current "+Gson().toJson(myCurrentFilterList));
            Log.d("testclear", "onCreateView: here clear current "+Gson().toJson(giveMyOrginalList()));
            myCurrentFilterList.clear()
            myCurrentFilterList.addAll(giveMyOrginalList())
            myAdapter.notifyDataSetChanged()
            Log.d("testclear", "onCreateView: here clear"+Gson().toJson(myAdapter.items));

        }
        return binding.root
    }

    fun refactorAllFromList(filterString: String): String {
        return if (filterString.equals(
                "All",
                ignoreCase = true
            ) or (filterString.isEmpty()) or (filterString == null) or filterString.equals("null")
        ) {
            ""
        } else {
            filterString
        }
    }
    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            ProductListViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[ProductListViewModel::class.java]
        testViewModel = ViewModelProviders.of(requireActivity()).get(AccountViewModel::class.java)

    }
    private fun initAppbar() {
        (activity as MainActivity).showBottomNavigation(false)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = true, isBack = false, isFilter = false, isClear = true,
            textTitle = getString(R.string.refine_filter),
            textDescription = ""
        )
    }
    private fun setUpRecyclerView() {
        myAdapter = AdapterFilter(listOf())
        binding.rvColors.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = myAdapter
            myAdapter.items =myCurrentFilterList

            myAdapter.setFilterClickListener { it, pos ->
                Log.d("testcolor", "setUpRecyclerView: " + it.id);
                when (it.id) {
                    1 -> {//Sort by
                        showDialogSingleChoice(it.title, sortList, pos)
                    }
                    2 -> {//Product Type

                    }
                    3 -> {//Brand
                        showDialogMultipleChoice(it.title, brandList, pos)
                    }
                    4 -> {//Size
                        showDialogMultipleChoice(it.title, sizeList, pos)
                    }
                    5 -> {//Discount

                    }
                    6 -> {//Price Range
                        showDialogRange(it.title, pos, newMin, newMax)

                    }
                }
            }
            myAdapter.setFilterColorClickListener { it, pos ->

                showDialogMultipleChoiceColor(it.title, colorList, pos)
            }
        }
    }

    private fun showDialogSingleChoice(
        title: String,
        list: List<MyFilterContentViewItem.SingleChoice>,
        pos: Int
    ) {
        val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)

        val adapterfilterContent = AdapterFilterContent(
            context, list
        ).also { adapter ->
            adapter.setCircleClickListener { data ->
                for (item in list) {
                    item.isSelected = item.equals(data)
                }
                (myAdapter.items.get(pos) as MyFilterRecyclerViewItem.Title).apply {
                    subTitle = data.title
                    slug = data.slug
                }
                myAdapter.notifyItemChanged(pos)
                adapter.notifyDataSetChanged()
            }
        }
        dialogBinding.title.text = title
        dialogBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterfilterContent
        }

        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun showDialogMultipleChoiceColor(
        title: String,
        list: List<MyFilterContentViewItem.MultipleChoiceColor>, pos: Int
    ) {
        val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        val adapterfilterContent = AdapterFilterContent(
            context, list
        ).also { adapter ->
            adapter.setFilterContentMultipleColorClickListener { data ->
                Log.d("testfilter", "showDialogMultipleChoice: " + data);
                for (item in list) {
                    if (item.equals(data)) {
                        item.isSelected = !item.isSelected
                        adapter.notifyItemChanged(list.indexOf(item))
                        break
                    }

                }
                val mylist = list.filter { it.isSelected }
                    .map {
                        MyColor(it.title, it.colorHex!!)
                    }
                if (mylist.isEmpty()) {
                    (myAdapter.items.get(pos) as MyFilterRecyclerViewItem.Color).colorList = listOf(
                        MyColor("All", "#ffffff")
                    )
                } else {
                    (myAdapter.items.get(pos) as MyFilterRecyclerViewItem.Color).colorList = mylist
                }

                myAdapter.notifyItemChanged(pos)
            }
        }
        dialogBinding.title.text = title
        dialogBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterfilterContent
        }

        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun showDialogMultipleChoice(
        title: String,
        list: List<MyFilterContentViewItem.MultipleChoice>, pos: Int
    ) {
        val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        val adapterfilterContent = AdapterFilterContent(
            context, list
        ).also { adapter ->
            adapter.setFilterContentMultipleClickListener { data ->
                Log.d("testfilter", "showDialogMultipleChoice: " + data);
                for (item in list) {
                    if (item.equals(data)) {
                        item.isSelected = !item.isSelected
                        adapter.notifyItemChanged(list.indexOf(item))
                        break
                    }
                }
                val myList = list.filter { it.isSelected }
                    .map { it.title }.toString().removeBracket()
                if (myList.isEmpty()) {
                    (myAdapter.items.get(pos) as MyFilterRecyclerViewItem.Title).subTitle = "All"
                } else {

                    (myAdapter.items.get(pos) as MyFilterRecyclerViewItem.Title).apply {
                        subTitle = myList
                        slug=data.slug
                    }
                }
                myAdapter.notifyItemChanged(pos)
            }
        }
        dialogBinding.title.text = title
        dialogBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterfilterContent
        }

        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun showDialogRange(
        title: String,
        pos: Int,
        min: String,
        max: String
    ) {
        val dialogBinding = DialogFilterRangeBinding.inflate(LayoutInflater.from(requireContext()))
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        dialogBinding.title.text = title
        dialogBinding.max.setText(newMax)
        dialogBinding.min.setText(newMin)
        dialogBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        dialogBinding.btnOk.setOnClickListener {
//            if(!validateTextField(dialogBinding.min) or !validateTextField(dialogBinding.max)){
//                return@setOnClickListener
//            }
            newMin = dialogBinding.min.text.toString()
            newMax = dialogBinding.max.text.toString()
            if (newMax.isEmpty()) {
                newMax = defaultMaxPrice
            }
            if (newMin.isEmpty()) {
                newMin = defaultMinPrice
            }
            bottomSheetDialog.dismiss()
            val newPriceRange = "Rs. ${newMin} - Rs. ${newMax}"
            (myAdapter.items.get(pos) as MyFilterRecyclerViewItem.Title).subTitle = newPriceRange
            myAdapter.notifyItemChanged(pos)
        }
        bottomSheetDialog.show()
    }

    private fun validateTextField(textField: TextInputEditText): Boolean {
        val data = textField!!.text.toString().trim()
        return if (data.isEmpty() or (data == " ")) {
            textField.error = "This field can't be empty"
            false
        } else {
            textField.error = null
            true
        }
    }


}
