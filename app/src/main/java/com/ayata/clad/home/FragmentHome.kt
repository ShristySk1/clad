package com.ayata.clad.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.StoryActivity
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentHomeBinding
import com.ayata.clad.home.adapter.*
import com.ayata.clad.home.adapter.AdapterJustDropped
import com.ayata.clad.home.adapter.AdapterPopularBrands
import com.ayata.clad.home.adapter.AdapterPopularMonth
import com.ayata.clad.home.adapter.AdapterRecommended
import com.ayata.clad.home.adapter.AdapterStories
import com.ayata.clad.home.model.*
import com.ayata.clad.home.viewmodel.HomeViewModel
import com.ayata.clad.home.viewmodel.HomeViewModelFactory
import com.ayata.clad.product.FragmentProductDetail
import com.ayata.clad.utils.Constants
import com.ayata.clad.view_all.FragmentViewAllProduct
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations

class FragmentHome : Fragment(),AdapterPopularMonth.OnItemClickListener,AdapterRecommended.OnItemClickListener
    ,AdapterPopularBrands.OnItemClickListener,AdapterJustDropped.OnItemClickListener
    ,AdapterMostPopular.OnItemClickListener, AdapterNewSubscription.OnItemClickListener
    ,AdapterStories.OnItemClickListener{

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel:HomeViewModel

    private var listStory = ArrayList<ModelStory>()
    private lateinit var adapterStories: AdapterStories

    private lateinit var adapterPopularMonth: AdapterPopularMonth
    private var listPopularMonth=ArrayList<ModelPopularMonth>()

    private lateinit var adapterRecommended: AdapterRecommended
    private var listRecommended=ArrayList<ModelRecommended>()
//    private var listRecommendedOne=ArrayList<ModelRecommended>()

    private lateinit var adapterPopularBrands: AdapterPopularBrands
    private var listPopularBrands=ArrayList<ModelPopularBrands>()

    private lateinit var adapterJustDropped: AdapterJustDropped
    private var listJustDropped=ArrayList<ModelJustDropped>()

    private lateinit var adapterMostPopular: AdapterMostPopular
    private var listMostPopular=ArrayList<ModelMostPopular>()

    private lateinit var adapterNewSubscription: AdapterNewSubscription
    private var listNewSubscription=ArrayList<ModelNewSubscription>()

    private lateinit var adapterBanner: AdapterBanner
    private var listBanner=ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        binding= FragmentHomeBinding.inflate(inflater,container,false)
        setUpViewModel()
        initButtonClick()
        initAppbar()
        initRecyclerView()
        return binding.root
    }

    private fun initAppbar(){
        (activity as MainActivity).showBottomNavigation(true)
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar1("",
            isSearch = true,
            isProfile = true,
            isClose = false,
            isLogo = true
        )
    }

    private fun setUpViewModel(){
        viewModel=ViewModelProvider(this,HomeViewModelFactory(ApiRepository(ApiService.getInstance())))
            .get(HomeViewModel::class.java)
    }

    private fun initRecyclerView() {
        //stories view
        adapterStories = AdapterStories(context, listStory,this)
        binding.recyclerStory.apply {
            layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter=adapterStories
        }
        prepareDataForStory()

        //popular this month
        adapterPopularMonth=AdapterPopularMonth(context,listPopularMonth,this)
        binding.recyclerPopularMonth.apply {
            layoutManager=LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
            adapter=adapterPopularMonth
        }
        prepareDataForPopularMonth()

        //recommended == show 1 only
        adapterRecommended= AdapterRecommended(context,listRecommended,this)
        binding.recyclerRecommended.apply {
            layoutManager=LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
            adapter=adapterRecommended
        }
        prepareDataForRecommended()

        //popular brand
        adapterPopularBrands = AdapterPopularBrands(context, listPopularBrands,this)
        binding.recyclerPopularBrands.apply {
            layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter=adapterPopularBrands
        }
        prepareDataForPopularBrands()

        //just dropped
        adapterJustDropped= AdapterJustDropped(context,listJustDropped,this)
        binding.recyclerJustDropped.apply {
            layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter=adapterJustDropped
        }
        prepareDataForJustDropped()

        //most popular
        adapterMostPopular= AdapterMostPopular(context,listMostPopular,this)
        binding.recyclerMostPopular.apply {
            layoutManager=GridLayoutManager(context,1,GridLayoutManager.HORIZONTAL,false)
            adapter=adapterMostPopular
        }
        prepareDataForMostPopular()

        //new subscription
        adapterNewSubscription=AdapterNewSubscription(context,listNewSubscription,this)
        binding.recyclerNewSubscription.apply {
            layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter=adapterNewSubscription
        }
        prepareDataForNewSubscription()


        //banner
        prepareBanner()
        adapterBanner= AdapterBanner(requireContext(),listBanner)
        binding.imageSlider.setSliderAdapter(adapterBanner)
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.imageSlider.startAutoCycle()
    }

    private fun prepareBanner(){
        listBanner.clear()
        listBanner.add("https://www.thoughtco.com/thmb/C7RiS4QG5TXcBG2d_Sh9i4hFpg0=/3620x2036/smart/filters:no_upscale()/close-up-of-clothes-hanging-in-row-739240657-5a78b11f8e1b6e003715c0ec.jpg")
        listBanner.add("https://st.depositphotos.com/1003633/2284/i/600/depositphotos_22848360-stock-photo-fashion-clothes-hang-on-a.jpg")
        listBanner.add("https://cdn.stocksnap.io/img-thumbs/280h/white-sneakers_EA7TDORJBT.jpg")
//        binding.imageSlider.startAutoCycle()
    }

    private fun prepareDataForNewSubscription() {
        listNewSubscription.clear()
        listNewSubscription.add(ModelNewSubscription("https://image.made-in-china.com/202f0j00gqjRIDFdribc/Autumn-and-Winter-Hand-Made-Double-Sided-Woolen-Cashmere-Ladies-Wool-Coat.jpg",
            "Beige bliss"))
        listNewSubscription.add(ModelNewSubscription("https://www.hergazette.com/wp-content/uploads/2020/01/Stylish-Photography-Poses-For-Girls-11.jpg",
            "Silk lure"))

        adapterNewSubscription.notifyDataSetChanged()

    }

    private fun prepareDataForStory() {
        listStory.clear()

        val list2 = arrayListOf<String>(
            "https://www.hergazette.com/wp-content/uploads/2020/01/Stylish-Photography-Poses-For-Girls-11.jpg",
            "https://anninc.scene7.com/is/image/LO/575769_6857?\$plp\$"
        )
        listStory.add(ModelStory("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQK4RVHQArXcaTvfasa8QGHYGFMPk3zJG1nfA&usqp=CAU", "Summer", "Description 2", list2))

        val listString = arrayListOf<String>(
            "https://image.made-in-china.com/202f0j00gqjRIDFdribc/Autumn-and-Winter-Hand-Made-Double-Sided-Woolen-Cashmere-Ladies-Wool-Coat.jpg",
            "https://www.hergazette.com/wp-content/uploads/2020/01/Stylish-Photography-Poses-For-Girls-11.jpg",
            "https://anninc.scene7.com/is/image/LO/575769_6857?\$plp\$"
        )
        listStory.add(ModelStory("https://image.made-in-china.com/202f0j00gqjRIDFdribc/Autumn-and-Winter-Hand-Made-Double-Sided-Woolen-Cashmere-Ladies-Wool-Coat.jpg", "New In", "Description 1", listString))

        val list3=ArrayList<String>()
        list3.add("https://i.pinimg.com/236x/43/c9/58/43c958dc53796581e037d67e0e2025b8.jpg")
        list3.add("https://image.made-in-china.com/202f0j00gqjRIDFdribc/Autumn-and-Winter-Hand-Made-Double-Sided-Woolen-Cashmere-Ladies-Wool-Coat.jpg")
        listStory.add(ModelStory("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBLdseom0mOn2lIbAdoDxwVdEJo4_SxzWpLA&usqp=CAU", "Activewear", "Description 3", list3))

        val list4=ArrayList<String>()
        list4.add(
            0,
            "https://asda.scene7.com/is/image/Asda/5059186277411?hei=684&wid=516&qlt=85&fmt=pjpg&resmode=sharp&op_usm=1.1,0.5,0,0&defaultimage=default_details_George_rd"
        )
        list4.add("https://image.made-in-china.com/202f0j00gqjRIDFdribc/Autumn-and-Winter-Hand-Made-Double-Sided-Woolen-Cashmere-Ladies-Wool-Coat.jpg")

        listStory.add(ModelStory("https://media.istockphoto.com/photos/beautiful-lady-overjoyed-by-warm-spring-breeze-dream-of-romantic-date-picture-id1170648040?k=20&m=1170648040&s=612x612&w=0&h=eOMcjFL2qyKnfvkH3IbIYkAKWXtQXCScCE12ahhqX_w=",
            "Basic", "Description 4", list4))

        val list5=ArrayList<String>()
        list5.add("https://i.pinimg.com/originals/71/4b/cc/714bcc5b6c5171cb82a5cea81e176b89.webp")
        listStory.add(ModelStory("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRs81HZC1Hbu-KVtCSbnyYX3J7CcSYFek0WO-OsK1AdZ3ahJr6E3AHgvKKy8-n08w9qC_U&usqp=CAU",
            "Couple Wear","Description 5",list5))

        adapterStories.notifyDataSetChanged()

    }

    private fun prepareDataForPopularMonth() {
        listPopularMonth.clear()
        listPopularMonth.add(ModelPopularMonth("Cashmere Jacket","Rs. 70.0","https://image.made-in-china.com/202f0j00gqjRIDFdribc/Autumn-and-Winter-Hand-Made-Double-Sided-Woolen-Cashmere-Ladies-Wool-Coat.jpg"))
        listPopularMonth.add(ModelPopularMonth("Cashmere Jacket","Rs. 30.0","https://www.hergazette.com/wp-content/uploads/2020/01/Stylish-Photography-Poses-For-Girls-11.jpg"))
        listPopularMonth.add(ModelPopularMonth("Cashmere Jacket","Rs. 80.0","https://asda.scene7.com/is/image/Asda/5059186277411?hei=684&wid=516&qlt=85&fmt=pjpg&resmode=sharp&op_usm=1.1,0.5,0,0&defaultimage=default_details_George_rd"))
        listPopularMonth.add(ModelPopularMonth("Cashmere Jacket","Rs. 120.0","https://anninc.scene7.com/is/image/LO/575769_6857?\$plp\$"))

        adapterPopularMonth.notifyDataSetChanged()

    }

    private fun prepareDataForRecommended() {
        listRecommended.clear()
        listRecommended.add(ModelRecommended("Sportswear (Red)","“Sporty clothes”","Rs. 30.0",
            "https://i.pinimg.com/236x/43/c9/58/43c958dc53796581e037d67e0e2025b8.jpg"))
        listRecommended.add(ModelRecommended("Cashmere Jacket","Casual Wear","Rs. 70.0",
            "https://image.made-in-china.com/202f0j00gqjRIDFdribc/Autumn-and-Winter-Hand-Made-Double-Sided-Woolen-Cashmere-Ladies-Wool-Coat.jpg"))

//        listRecommendedOne.clear()
//        listRecommendedOne.add(listRecommended[0])
        adapterRecommended.notifyDataSetChanged()

    }

    private var listDrawable= arrayListOf<Int>(R.drawable.brand_aamayra,
        R.drawable.brand_aroan,R.drawable.brand_bishrom,
        R.drawable.brand_caliber,R.drawable.brand_creative_touch,
        R.drawable.brand_fibro,R.drawable.brand_fuloo,
        R.drawable.brand_gofi,R.drawable.brand_goldstar,
        R.drawable.brand_hillsandclouds,R.drawable.brand_jujuwears,
        R.drawable.brand_kasa,R.drawable.brand_ktm_city,R.drawable.brand_logo,
        R.drawable.brand_mode23,R.drawable.brand_newmew,
        R.drawable.brand_phalanoluga,R.drawable.brand_sabah,
        R.drawable.brand_station,R.drawable.brand_tsarmoire)
    private fun prepareDataForPopularBrands() {
        listDrawable.shuffle()
        listPopularBrands.clear()
//        listPopularBrands.add(ModelPopularBrands("https://cdn.britannica.com/94/193794-050-0FB7060D/Adidas-logo.jpg","Adidas","All 106"))
//        listPopularBrands.add(ModelPopularBrands("https://i.pinimg.com/originals/e2/1e/d6/e21ed611fec39f3911d7376723811d8a.jpg","Nike","All 106"))
//        listPopularBrands.add(ModelPopularBrands("https://i.pinimg.com/originals/f4/29/13/f42913698b86ddc1a611163154e71619.jpg","Vans","All 106"))
//        listPopularBrands.add(ModelPopularBrands("https://www.freesvgdownload.com/wp-content/uploads/2021/04/Balenciaga-logo-svg.jpg","Balenciaga","All 106"))
//        listPopularBrands.add(ModelPopularBrands("https://cdn.shopify.com/s/files/1/0249/5892/6941/products/Converse-Logo-Iron-On-Sticker_1890x.jpg?v=1585666919","Converse","All 106"))

        for(i in listDrawable){
            listPopularBrands.add(ModelPopularBrands(i,"Name","All 106"))
        }
        adapterPopularBrands.notifyDataSetChanged()

    }

    private fun prepareDataForJustDropped() {
        listJustDropped.clear()
        listJustDropped.add(ModelJustDropped("https://freepngimg.com/thumb/categories/627.png",
            "Nike ISPA Overreact Sail Multi","Lowest Ask",
        "https://p7.hiclipart.com/preview/595/571/731/swoosh-nike-logo-just-do-it-adidas-nike.jpg"))
        listJustDropped.add(ModelJustDropped("https://images.squarespace-cdn.com/content/v1/566e100d0e4c116bdc11b2c2/1473302788755-FL48S6YFWHYC9KU18K52/245282-ceb4145ac7b646889a16b6f5dbd2f455.png?format=750w",
            "adidas Yeezy Boost 700 MNVN Bone","Lowest Ask",
            "https://www.pngkit.com/png/full/436-4366026_adidas-stripes-png-adidas-logo-without-name.png"))
        listJustDropped.add(ModelJustDropped("https://www.pngkit.com/png/full/70-704028_running-shoes-png-image-running-shoes-clipart-transparent.png",
            "Jordan 11 Retro Low White Concord (W) ","Lowest Ask",
        "https://upload.wikimedia.org/wikipedia/en/thumb/3/37/Jumpman_logo.svg/1200px-Jumpman_logo.svg.png"))

        adapterJustDropped.notifyDataSetChanged()

    }

    private fun prepareDataForMostPopular() {
        listMostPopular.clear()
        listMostPopular.add(
            ModelMostPopular("https://freepngimg.com/thumb/categories/627.png",
            "Jordan 5 Retro Alternate Grape","Lowest Ask","Rs. 5000",
            "https://upload.wikimedia.org/wikipedia/en/thumb/3/37/Jumpman_logo.svg/1200px-Jumpman_logo.svg.png")
        )
        listMostPopular.add(ModelMostPopular("https://www.pngkit.com/png/full/70-704028_running-shoes-png-image-running-shoes-clipart-transparent.png",
            "adidas Yeezy Boost 700 MNVN Bone","Lowest Ask","Rs. 8000",
        "https://upload.wikimedia.org/wikipedia/en/thumb/3/37/Jumpman_logo.svg/1200px-Jumpman_logo.svg.png"))

        listMostPopular.add(ModelMostPopular("https://images.squarespace-cdn.com/content/v1/566e100d0e4c116bdc11b2c2/1473302788755-FL48S6YFWHYC9KU18K52/245282-ceb4145ac7b646889a16b6f5dbd2f455.png?format=750w",
            "Jordan 14 Retro Gym Red Toro","Lowest Ask","Rs. 8000",
            "https://upload.wikimedia.org/wikipedia/en/thumb/3/37/Jumpman_logo.svg/1200px-Jumpman_logo.svg.png"))

        listMostPopular.add(ModelMostPopular("https://freepngimg.com/thumb/categories/627.png",
            "Jordan 5 Retro Alternate Grape","Lowest Ask","Rs. 5000",
            "https://upload.wikimedia.org/wikipedia/en/thumb/3/37/Jumpman_logo.svg/1200px-Jumpman_logo.svg.png")
        )

        adapterMostPopular.notifyDataSetChanged()

    }

    override fun onPopularMonthClicked(data: ModelPopularMonth, position: Int) {
//        Toast.makeText(context, "Popular this month: ${ data.title }",Toast.LENGTH_SHORT).show()
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment,FragmentProductDetail())
            .addToBackStack(null).commit()
    }

    override fun onRecommendedClicked(data: ModelRecommended, position: Int) {
//        Toast.makeText(context,"Recommend: ${data.title}",Toast.LENGTH_SHORT).show()
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment,FragmentProductDetail())
            .addToBackStack(null).commit()
    }

    override fun onPopularBrandsClicked(data: ModelPopularBrands, position: Int) {
        Toast.makeText(context,"Brand: ${data.title}",Toast.LENGTH_SHORT).show()
    }

    override fun onJustDroppedClicked(data: ModelJustDropped, position: Int) {
//        Toast.makeText(context,"Just Dropped: ${data.title}",Toast.LENGTH_SHORT).show()
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment,FragmentProductDetail())
            .addToBackStack(null).commit()
    }

    override fun onMostPopularClicked(data: ModelMostPopular, position: Int) {
//        Toast.makeText(context,"Most Popular: ${data.title}",Toast.LENGTH_SHORT).show()
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment,FragmentProductDetail())
            .addToBackStack(null).commit()
    }

    override fun onNewSubscriptionClicked(data: ModelNewSubscription, position: Int) {
//        Toast.makeText(context,"New Subscription: ${data.title}",Toast.LENGTH_SHORT).show()
        parentFragmentManager.beginTransaction().replace(R.id.main_fragment,FragmentProductDetail())
            .addToBackStack(null).commit()
    }

    override fun onStoryClick(data: ModelStory,position: Int) {
        //story
        StoryActivity.storyIndex=position
        StoryActivity.listStory.clear()
        StoryActivity.listStory.addAll(listStory)
        val i = Intent(requireContext(), StoryActivity::class.java)
        startActivity(i)
    }

    private fun initButtonClick(){

        binding.justDroppedViewBtn.setOnClickListener {
            val bundle=Bundle()
            bundle.putString(Constants.FILTER_HOME,"Just Dropped")
            val fragmentViewAllProduct=FragmentViewAllProduct()
            fragmentViewAllProduct.arguments=bundle
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment,fragmentViewAllProduct)
                .addToBackStack(null).commit()
        }

        binding.mostPopularViewBtn.setOnClickListener {
            val bundle=Bundle()
            bundle.putString(Constants.FILTER_HOME,"Most Popular")
            val fragmentViewAllProduct=FragmentViewAllProduct()
            fragmentViewAllProduct.arguments=bundle
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment,fragmentViewAllProduct)
                .addToBackStack(null).commit()
        }
        binding.popularViewBtn.setOnClickListener {
            val bundle=Bundle()
            bundle.putString(Constants.FILTER_HOME,"Popular This Month")
            val fragmentViewAllProduct=FragmentViewAllProduct()
            fragmentViewAllProduct.arguments=bundle
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment,fragmentViewAllProduct)
                .addToBackStack(null).commit()
        }
        binding.recommendedViewBtn.setOnClickListener {
            val bundle=Bundle()
            bundle.putString(Constants.FILTER_HOME,"Recommended")
            val fragmentViewAllProduct=FragmentViewAllProduct()
            fragmentViewAllProduct.arguments=bundle
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment,fragmentViewAllProduct)
                .addToBackStack(null).commit()
        }
    }

}