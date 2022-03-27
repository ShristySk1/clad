package com.ayata.clad.story

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayata.clad.MainActivity
import com.ayata.clad.databinding.ActivityStoryBinding
import com.ayata.clad.home.model.ModelJustDropped
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.home.response.Story
import com.ayata.clad.utils.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable


class StoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener,
    AdapterStoryProduct.OnItemClickListener {
//    StoriesProgressView.StoriesListener

    companion object {
        const val TAG: String = "StoryActivityLog"

        //set these from prev fragment/activity
        var storyIndex = 0
        var listStory = ArrayList<Story>()
    }

    var pressTime = 0L
    var limit = 500L

    private var counter = 0
    private val timeEach = 2000L

    private var listImageStory = ArrayList<String>()
    private var listoflistofproducts = ArrayList<ArrayList<ProductDetail>>()
    private var fromPause = false
    private var isImageLoading = false

    private lateinit var adapterStoryProduct: AdapterStoryProduct
    private var listProduct = ArrayList<ProductDetail>()

    private lateinit var binding: ActivityStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        GlobalScope.launch(Dispatchers.IO) {
            Glide.get(this@StoryActivity).clearDiskCache()
        }
        initRecyclerView()
        counter = 0
        // adding on click listener for our reverse view.
        binding.reverse.setOnClickListener {
            if (!fromPause) {
                fromPause = false
                binding.stories.reverse()
            }
        }

        // on below line we are calling a set on touch
        // listener method to move towards previous image.
        binding.reverse.setOnTouchListener(handleTouch)

        // on below line we are initializing
        // view to skip a specific story.
        binding.skip.setOnClickListener {
            Log.d(TAG, "onCreate: skip")
            if (!fromPause) {
                fromPause = false
                binding.stories.skip()
            }
        }
        // on below line we are calling a set on touch
        // listener method to move to next story.
        binding.skip.setOnTouchListener(handleTouch)
        if (storyIndex >= 0 && storyIndex < listStory.size) {
            fromPause = false
            setStoryView(listStory[storyIndex])
        } else {
            onBackPressed()
        }

        binding.btnClose.setOnClickListener {
            onBackPressed()
        }

    }

    private fun initRecyclerView() {
        adapterStoryProduct = AdapterStoryProduct(context = this, listProduct, this)
        binding.recyclerProduct.apply {
            adapter = adapterStoryProduct
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

    }

    private fun prepareData(listOfProductForSingleImage: ArrayList<ProductDetail>) {
        listProduct.clear()
//        listProduct.add(ModelJustDropped("https://www.onlinepng.com/media/catalog/product/cache/571072fdb4ae023a8c393de549460086/r/i/rin639656_1.jpg",
//            "10% OFF","5000","50",
//            "https://p7.hiclipart.com/preview/595/571/731/swoosh-nike-logo-just-do-it-adidas-nike.jpg"))
//        listProduct.add(ModelJustDropped("https://i.pinimg.com/736x/37/2c/6f/372c6f40eb0835eea3abd13a02a64cd0.jpg",
//            "20% OFF","5000","45",
//            "https://www.pngkit.com/png/full/436-4366026_adidas-stripes-png-adidas-logo-without-name.png"))
//
//        listProduct.shuffle()
        listOfProductForSingleImage.forEach {
            listProduct.add(it)
        }
        adapterStoryProduct.notifyDataSetChanged()

    }

    private fun setStoryView(data: Story) {
//        prepareData()
        //set view
        binding.textTitle.text = data.vendor
        binding.textSubTitle.text = ""
        binding.placeholder.text = data.vendor.substring(0, 1)
        binding.placeholder.visibility = View.VISIBLE
        Glide.with(this).load(data.contents[0].imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.placeholder.visibility = View.VISIBLE
                    binding.imageCircle.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.placeholder.visibility = View.GONE
                    binding.imageCircle.visibility = View.VISIBLE
                    return false
                }
            }).into(binding.imageCircle)

        //set story
        listImageStory.clear()
        listoflistofproducts.clear()
        //filter images
        data.contents.forEach {
            listImageStory.add(it.imageUrl)
            listoflistofproducts.add(it.products as ArrayList<ProductDetail>)
        }
        // on below line we are setting the total count for our stories.
        binding.stories.setStoriesCount(listImageStory.size)


        val timeTotal = timeEach * data.contents.size
        // on below line we are setting story duration for each story.
        binding.stories.setStoryDuration(timeTotal)

        // on below line we are calling a method for set
        // on story listener and passing context to it.
        binding.stories.setStoriesListener(this)

        // below line is use to start stories progress bar.
        binding.stories.startStories()
        // on below line we are setting image to our image view.
        loadImage(listImageStory[counter])
    }

    private fun loadImage(imageUrl: String) {
        val listOfProductForSingleImage = listoflistofproducts[counter]
        prepareData(listOfProductForSingleImage)
        Log.d(TAG, "loadImage: here")
        if (!fromPause) {
            Log.d(TAG, "loadImage: pause")
            binding.stories.pause()
            isImageLoading = true
        }
        Glide.with(this).load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "onLoadFailed: ")
                    if (!fromPause && isImageLoading) {
                        binding.stories.resume()
                    }
                    isImageLoading = false
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "onResourceReady: $fromPause")
                    if (!fromPause && isImageLoading) {
                        Log.d(TAG, "onResourceReady: resume")
                        fromPause = false
                        binding.stories.resume()
                    }
                    isImageLoading = false
                    return false
                }
            })
            .into(binding.image)
    }

    override fun onNext() {
        Log.d(TAG, "onNext: loading $isImageLoading")
        if (isImageLoading) {
            return
        }
        if (counter >= listImageStory.lastIndex) {
            Log.d(TAG, "onNext: complete")
            onComplete()
        } else {
            Log.d(TAG, "onNext: $fromPause")
            fromPause = false
            loadImage(listImageStory[++counter])
        }
    }

    override fun onPrev() {
        Log.d(TAG, "onPrev: Here $fromPause  $counter")
        if ((counter - 1) < 0) {
            if (storyIndex > 0) {
                --storyIndex
                val i = Intent(this, StoryActivity::class.java)
                startActivity(i)
                finish()
            }
        } else {
            fromPause = false
            loadImage(listImageStory[--counter])
        }

    }

    override fun onComplete() {
        Log.d(TAG, "onComplete: $fromPause loading $isImageLoading")
        if (isImageLoading) {
            return
        }
        if (storyIndex < listStory.lastIndex) {
            counter = 0
            ++storyIndex
//            binding.stories.destroy()
//            setStoryView(listStory[++storyIndex])
            val i = Intent(this, StoryActivity::class.java)
            startActivity(i)
            finish()
        } else {
            onBackPressed()
        }
    }

    private val handleTouch = OnTouchListener { v, event ->
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // the story will pause for specific time.
                pressTime = System.currentTimeMillis()
                binding.stories.pause()
                false
            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP -> {
                // screen this method will skip to next image.
                val now = System.currentTimeMillis()
                fromPause = limit < now - pressTime
                binding.stories.resume()
                Log.d(TAG, "pressTime: ${limit < now - pressTime}")
                limit < now - pressTime
            }
        }
        false
    }

    override fun onDestroy() {
        binding.stories.destroy()
        super.onDestroy()
    }

    override fun onPause() {
        binding.stories.pause()
        super.onPause()
    }

    override fun onResume() {
        binding.stories.resume()
        super.onResume()
    }

    override fun onProductClick(data: ProductDetail, position: Int) {
        val i = Intent(this, MainActivity::class.java)
        i.putExtra(Constants.FROM_STORY, true)
        i.putExtra("data",data as ProductDetail)
        startActivity(i)
    }
}