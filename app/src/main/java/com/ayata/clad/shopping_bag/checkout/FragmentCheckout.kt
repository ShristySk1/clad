package com.ayata.clad.shopping_bag.checkout

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.text.bold
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.DialogShoppingSizeBinding
import com.ayata.clad.databinding.FragmentCartCheckoutBinding
import com.ayata.clad.shop.model.ModelShop
import com.ayata.clad.shopping_bag.FragmentShoppingBag
import com.ayata.clad.shopping_bag.adapter.AdapterCheckout
import com.ayata.clad.shopping_bag.adapter.AdapterCircleText
import com.ayata.clad.shopping_bag.model.ModelCheckout
import com.ayata.clad.shopping_bag.model.ModelCircleText
import com.ayata.clad.shopping_bag.shipping.FragmentShipping
import com.ayata.clad.shopping_bag.viewmodel.CheckoutViewModel
import com.ayata.clad.shopping_bag.viewmodel.CheckoutViewModelFactory
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.wishlist.FragmentWishlist
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

class FragmentCheckout : Fragment() ,AdapterCheckout.OnItemClickListener{

    private lateinit var binding: FragmentCartCheckoutBinding
    private lateinit var viewModel: CheckoutViewModel

    private lateinit var adapterCheckout: AdapterCheckout
    private var listCheckout=ArrayList<ModelCheckout>()
    private lateinit var layoutManagerCheckout: LinearLayoutManager

    private lateinit var adapterCircleSize: AdapterCircleText
    private var listSize=ArrayList<ModelCircleText>()

    private lateinit var adapterCircleQty: AdapterCircleText
    private var listQty=ArrayList<ModelCircleText>()

    companion object{
        private const val TAG="FragmentCheckout"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCartCheckoutBinding.inflate(inflater, container, false)

        setUpViewModel()
//        (parentFragment as FragmentShoppingBag).checkoutPage()
        initAppbar()
        initView()
        setShimmerLayout(false)
        initRecycler()
        setUpView()
        initRefreshLayout()
        getCartAPI()
        return binding.root
    }

    private fun setUpViewModel(){
        viewModel= ViewModelProvider(this,
            CheckoutViewModelFactory(ApiRepository(ApiService.getInstance()))
        )[CheckoutViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
//        (parentFragment as FragmentShoppingBag).checkoutPage()
    }
    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1(getString(R.string.shopping_bag),
            isSearch = false,
            isProfile = true,
            isClose = false
        )
    }

    private fun initRefreshLayout(){
        //refresh layout on swipe
        binding.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getCartAPI()
            binding.swipeRefreshLayout.isRefreshing = false
        })
        //Adding ScrollListener to activate swipe refresh layout
        binding.shimmerView.root.setOnScrollChangeListener(View.OnScrollChangeListener { view, i, i1, i2, i3 ->
            binding.swipeRefreshLayout.isEnabled = i1 == 0
        })

        // Adding ScrollListener to getting whether we're on First Item position or not
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.swipeRefreshLayout.isEnabled = layoutManagerCheckout.findFirstVisibleItemPosition()==0
            }
        })

    }

    private fun setUpView(){
        if(listCheckout.isEmpty()){
            binding.layoutEmpty.visibility=View.VISIBLE
            binding.layoutMain.visibility=View.GONE
        }else{
            binding.layoutEmpty.visibility=View.GONE
            binding.layoutMain.visibility=View.VISIBLE
        }
    }

    private fun initView(){
        binding.textTotal.text=SpannableStringBuilder().bold { append("Total ") }.append("(incl. VAT)")

        binding.btnCheckout.setOnClickListener {
//            fragment_shopping
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment,FragmentShipping())
                .addToBackStack("checkout").commit()
        }

        binding.btnBrowse.setOnClickListener {
            (activity as MainActivity).openFragmentShop()
        }

        binding.checkBoxAll.setOnCheckedChangeListener { buttonView, isChecked ->
           if (isChecked){
               for(item in listCheckout){
                   item.isSelected=true
               }
               adapterCheckout.notifyDataSetChanged()
               calculatePrice()
           }
        }

        binding.totalPrice.text="Rs. 7800"
        binding.textItemSelected.text="1/2 ITEMS Selected"
    }

    private fun initRecycler(){

        layoutManagerCheckout=LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        adapterCheckout= AdapterCheckout(requireContext(),listCheckout,this)
        binding.recyclerView.apply {
            adapter=adapterCheckout
            layoutManager=layoutManagerCheckout
        }
        prepareList()

    }

    private fun prepareList(){
        listCheckout.clear()
        listCheckout.add(ModelCheckout("Nike Air Jordan",784569,8790.0,80.0,"A",2,true,
            "https://freepngimg.com/thumb/categories/627.png"))
        listCheckout.add(ModelCheckout("Nike Air Jordan",784579,9000.0,180.0,"A",1,false,
            "https://www.pngkit.com/png/full/70-704028_running-shoes-png-image-running-shoes-clipart-transparent.png"))
        listCheckout.add(ModelCheckout("Nike Air Jordan",784577,8790.0,80.0,"A",2,true,
            "https://freepngimg.com/thumb/categories/627.png"))
        listCheckout.add(ModelCheckout("Nike Air Jordan",784599,8790.0,80.0,"A",2,true,
            "https://freepngimg.com/thumb/categories/627.png"))

        adapterCheckout.notifyDataSetChanged()
        calculatePrice()

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
//        Toast.makeText(requireContext(),"Checkbox $isChecked++++$position",Toast.LENGTH_SHORT).show()
        for(item in listCheckout){
            if(item == data){
                item.isSelected=isChecked
            }
        }
        isCheckAll()
        calculatePrice()
    }

    private fun isCheckAll(){
        var isAllChecked=true
        for(item in listCheckout){
           if(!item.isSelected){
               isAllChecked=false
               break
           }
        }
        binding.checkBoxAll.isChecked = isAllChecked
    }

    private fun calculatePrice(){
        var total_price=0.0
        var selected=0
        for(item in listCheckout){
            if(item.isSelected){
                total_price += if(PreferenceHandler.getCurrency(context).equals("npr",true)){
                    (item.priceNPR*item.qty)
                }else{
                    (item.priceUSD*item.qty)
                }
                selected++
            }
        }
        binding.totalPrice.text=if(PreferenceHandler.getCurrency(context).equals("npr",true)){
            "${getString(R.string.rs)} $total_price"
        }else{
            "${getString(R.string.usd)} $total_price"
        }
        binding.textItemSelected.text="$selected/${listCheckout.count()} ITEMS Selected"
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

        dialogBinding.btnSave.setOnClickListener {
            saveSizeAPI(data,data.size)
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
                    data.qty=1
                }
                adapterCheckout.notifyItemChanged(position)
                calculatePrice()
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

        dialogBinding.btnSave.setOnClickListener {
            saveQuantityAPI(data,data.qty.toString())
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

    private fun setShimmerLayout(isVisible:Boolean){
        if(isVisible){
            binding.layoutMain.visibility=View.GONE
            binding.layoutEmpty.visibility=View.GONE
            binding.shimmerFrameLayout.visibility=View.VISIBLE
            binding.shimmerFrameLayout.startShimmer()
        }else{
            binding.layoutMain.visibility=View.VISIBLE
            binding.layoutEmpty.visibility=View.GONE
            binding.shimmerFrameLayout.visibility=View.GONE
            binding.shimmerFrameLayout.stopShimmer()
        }
    }

    private fun getCartAPI(){
//        listCheckout.clear()
        setShimmerLayout(true)
        viewModel.cartListAPI(PreferenceHandler.getToken(context).toString())
        viewModel.getCartListAPI().observe(viewLifecycleOwner,{
            when (it.status) {
                Status.SUCCESS -> {
                    setShimmerLayout(false)
                    setUpView()
                    Log.d(TAG, "getCartAPI: ${it.data}")
                    val jsonObject=it.data
                    if(jsonObject!=null){
                        try{
                            val message=jsonObject.get("message").asString
                            if(message.contains("empty.",true)){
//                                setUpView()
                            }
                        }catch (e:Exception){
                            Log.d(TAG, "getCartAPI:Error ${e.message}")
                        }
                    }
                    adapterCheckout.notifyDataSetChanged()
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    setShimmerLayout(false)
//                    listCheckout.clear()
//                    setUpView()
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "getCartAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun saveSizeAPI(product:ModelCheckout,sizeSelected:String){
        viewModel.saveSizeAPI(PreferenceHandler.getToken(context).toString(),product.itemId,sizeSelected)
        viewModel.getSizeAPI().observe(viewLifecycleOwner,{
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "saveSizeAPI: ${it.data}")
                    val jsonObject=it.data
                    if(jsonObject!=null){
                        showSnackBar("Size Updated")
                        try{

                        }catch (e:Exception){
                            Log.d(TAG, "saveSizeAPI:Error ${e.message}")
                        }
                    }
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "saveSizeAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun saveQuantityAPI(product:ModelCheckout,quantitySelected:String){
        viewModel.saveQuantityAPI(PreferenceHandler.getToken(context).toString(),product.itemId,quantitySelected)
        viewModel.getQuantityAPI().observe(viewLifecycleOwner,{
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "saveQuantityAPI: ${it.data}")
                    val jsonObject=it.data
                    if(jsonObject!=null){
                        showSnackBar("Quantity Updated")
                        try{

                        }catch (e:Exception){
                            Log.d(TAG, "saveQuantityAPI:Error ${e.message}")
                        }
                    }
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "saveQuantityAPI:Error ${it.message}")
                }
            }
        })
    }

    private fun showSnackBar(msg:String){
        val snackbar = Snackbar
            .make(binding.root, msg, Snackbar.LENGTH_SHORT)
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.show()
    }
}