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
import com.ayata.clad.R
import com.ayata.clad.databinding.ActivityStoryBinding
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


class StoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener,
    AdapterStoryProduct.OnItemClickListener {
    //    StoriesProgressView.StoriesListener
    private var x1: Float = 0f
    private var x2: Float = 0f

    companion object {
        const val TAG: String = "StoryActivityLog"

        //set these from prev fragment/activity
        var storyIndex = 0
        var listStory = ArrayList<Story>()
    }

    var pressTime = 0L
    var limit = 500L

    private var counter = 0
    private val timeEach = 4000L

    private var listImageStory = ArrayList<String>()
    private var listoflistofproducts = ArrayList<ArrayList<ProductDetail>>()
    private var fromPause = false
    private var isImageLoading = false

    private lateinit var adapterStoryProduct: AdapterStoryProduct
    private var listProduct = ArrayList<ProductDetail>()
    private val i by lazy {
        Intent(this, MainActivity::class.java)
    }
//    var imageLoaded:ImageLoadedInterface?=null

    private lateinit var binding: ActivityStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("testmyindex", "onCreate: " + storyIndex);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //default
        i.putExtra(Constants.FROM_STORY, false)
        i.putExtra("data", "")
        //end default
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        GlobalScope.launch(Dispatchers.IO) {
            Glide.get(this@StoryActivity).clearDiskCache()
        }
        initRecyclerView()
        counter = 0
        // adding on click listener for our reverse view.
        binding.reverse.setOnClickListener {
//            if (!fromPause) {
//                fromPause = false
            binding.stories.reverse()
//            }
        }

        // on below line we are calling a set on touch
        // listener method to move towards previous image.
        binding.reverse.setOnTouchListener(handleTouch)

        // on below line we are initializing
        // view to skip a specific story.
        binding.skip.setOnClickListener {
            Log.d(TAG, "onCreate: skip")
//            if (!fromPause) {
//                fromPause = false
            binding.stories.skip()
//            }
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
//        imageLoaded=object :ImageLoadedInterface{
//            override fun onImageLoad(boolean: Boolean) {
//                Log.d("testloading", "onImageLoad: "+boolean)
//                if(boolean){
//                    binding.stories.resume()
//                }else{
//
//                    binding.stories.pause()
//                }
//            }
//
//        }

    }

    private fun initRecyclerView() {
        Log.d("testmyindex", "initRecyclerView: ");
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
        binding.stories.setStoriesCount(data.contents.size)

//        val timeTotal = timeEach * data.contents.size
        val timeTotal = timeEach

        Log.d("mycontent", "setStoryView: " + data.contents.size);
        Log.d("mytotaltime", "setStoryView: " + timeTotal);
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
//        if (!fromPause) {
        Log.d(TAG, "loadImage: pause")
//            binding.stories.pause()
        isImageLoading = true
//            imageLoaded?.let {  it.onImageLoad(true)}
//        }
        Glide.with(this).load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "onLoadFailed: ")
//                    if (!fromPause && isImageLoading) {
//                        binding.stories.resume()
//                    }
                    isImageLoading = false
//                    imageLoaded?.let {  it.onImageLoad(false)}
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
//                    if (!fromPause && isImageLoading) {
                    Log.d(TAG, "onResourceReady: resume")
                    fromPause = false
//                        binding.stories.resume()
//                    }
                    isImageLoading = false
//                    imageLoaded?.let {  it.onImageLoad(false)}
                    return false
                }
            })
            .into(binding.image)
    }

    override fun onNext() {
//        if (isImageLoading) {
//            return
//        }
        Log.d(
            TAG,
            "onNext: loading $isImageLoading" + "counter: " + counter + "lastinded: " + listImageStory.lastIndex
        )
//        counter++;

        if (counter >= listImageStory.lastIndex) {

        } else {
            Log.d(TAG, "onNext: $fromPause")
            fromPause = false
            loadImage(listImageStory[++counter])
        }
    }

    override fun onPrev() {
//        if(isImageLoading){
//            return;
//        }
        Log.d(TAG, "onPrev: Here $fromPause  $counter")
        if ((counter - 1) < 0) {
            if (storyIndex > 0) {
                --storyIndex
                counter = 0
                startMyActivityPrev()
            }
        } else {
            fromPause = false
//            counter--;
            loadImage(listImageStory[--counter])
        }

    }

    override fun onComplete() {
        Log.d(TAG, "onNext: complete")
//            onComplete()
        if (storyIndex < listStory.lastIndex) {
            counter = 0
            ++storyIndex
            binding.stories.destroy()
//            setStoryView(listStory[++storyIndex])
            Log.d("errorhere from next", "onNext: " + storyIndex);
            startMyActivityNext()
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
                x1 = event.getX();
                false
            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP -> {
                // screen this method will skip to next image.
                val now = System.currentTimeMillis()
                fromPause = limit < now - pressTime

                Log.d(TAG, "pressTime: ${limit < now - pressTime}")

                x2 = event.x
                val deltaX: Float = x2 - x1
                Log.d(TAG, "mydeltadata" + deltaX);
                if((deltaX>=0&&deltaX<50) or (deltaX<=0 && deltaX>-50)){
                    Log.d("deltaishere", ": ");
                    binding.stories.resume()
                    limit < now - pressTime

                }else if(deltaX < -50) {
                    //show next activity
                    //SWIPE RIGHT TO LEFT
                    if (storyIndex < listStory.lastIndex) {
                        counter = 0
                        ++storyIndex
                        binding.stories.destroy()
                        startMyActivityNext()
                    } else {
                        onBackPressed()
                    }
                } else if (deltaX > 50) {
                    //SHOW PREVIOUS ACTIVITY
                    //LEFT TO RIGHT SWIPE
                    if ((counter - 1) < 0) {
                        if (storyIndex > 0) {
                            storyIndex--
                            counter = 0
                            Log.d("storyindex", "testmyindex" + storyIndex);
                            startMyActivityPrev()
                        }
                    } else {
                        fromPause = false
                        loadImage(listImageStory[--counter])
                    }
                }
            }
        }
        false
    }

    private fun startMyActivityNext() {
        Log.d("mystoryindex", "startMyActivityNext: " + storyIndex);
        val i = Intent(this, StoryActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
// disable default animation for new intent
//        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//        animationOut(findViewById(R.id.container),
//            windowManager,
//            object : AnimationFinishedListener {
//                override fun onAnimationFinished() {
//                    startActivity(i)
//                    finish()
//                }
//            })
        startActivity(i)
        finish()
        overridePendingTransition(
            R.anim.slide_in_right_without_animatioon,
            R.anim.slide_out_left_without_animation
        );

    }

    private fun startMyActivityPrev() {
        binding.stories.destroy()
        val i = Intent(this, StoryActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//        animationOutReverse(findViewById(R.id.container),
//            windowManager,
//            object : AnimationFinishedListener {
//                override fun onAnimationFinished() {
//                    startActivity(i)
//                    finish()
//                }
//            })
        startActivity(i)
        finish()

        overridePendingTransition(
            R.anim.slide_in_left_without_animation,
            R.anim.slide_out_right_without_animation
        );
        // remember to put it after startActivity, if you put it to above, animation will not working
// document say if we don't want animation we can put 0. However, if we put 0 instead of R.anim.no_animation, the exist activity will become black when animate
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
        i.putExtra(Constants.FROM_STORY, true)
        i.putExtra("data", data as ProductDetail)
        startActivity(i)
    }

}
//interface ImageLoadedInterface{
//    fun onImageLoad(boolean: Boolean)
//}