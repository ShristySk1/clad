package com.ayata.clad.profile.reviews

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.DialogCustomBinding
import com.ayata.clad.databinding.FragmentMyReviewFormBinding
import com.ayata.clad.productlist.ItemOffsetDecoration
import com.ayata.clad.profile.reviews.adapter.AdapterImageViewType
import com.ayata.clad.profile.reviews.adapter.DataModel
import com.ayata.clad.profile.reviews.imageswipe.FragmentImageSwiper
import com.ayata.clad.profile.reviews.model.Review
import com.ayata.clad.profile.reviews.response.Detail
import com.ayata.clad.profile.reviews.response.ImageUploadResponse
import com.ayata.clad.profile.reviews.utils.CustomImagePickerComponents
import com.ayata.clad.profile.reviews.viewmodel.ReviewViewModel
import com.ayata.clad.profile.reviews.viewmodel.ReviewViewModelFactory
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.utils.ProgressDialog
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.*
import com.esafirm.imagepicker.model.Image
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

const val MY_PHOTO_NUMBER = 4

class FragmentMyReviewsForm : Fragment() {
    lateinit var binding: FragmentMyReviewFormBinding
    private lateinit var progressDialog: ProgressDialog

    //images
    lateinit var adapterImage: AdapterImageViewType
    private val images = ArrayList<Image>()
    lateinit var imagePickerLauncher: ImagePickerLauncher
    val listImage = mutableListOf<DataModel>()
    var myChosenSize = ""
    var myChosenComfort = ""
    var myChosenQuality = 50
    var isLoading = false
    var myDeletePosition = -1
    var isDisabledImagePicker = false
    //imagaes end

    private lateinit var viewModel: ReviewViewModel
    lateinit var item: Review
    val TAG = "FragmentMyReviewsForm"
//    val imageString = ArrayList<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentMyReviewFormBinding.inflate(inflater, container, false)
        initAppbar()
        setUpViewModel()
        setTab(binding.tabSize, binding.tabComfort)
        initRecyclerview()
        initBundle()
        observePostReviewApi()
        observeImageUploadApi()
        observeDeleteImageReviewApi()
        binding.progressBarQuality.addOnChangeListener { slider, value, fromUser -> /* `value` is the argument you need */
            myChosenQuality = value.toInt()
        }
        binding.btnPostReview.setOnClickListener {

            if (!isLoading) {
                Log.d("testclick", "onCreateView: ");
                val rate = binding.ratingBar1.rating
                val desc =
                    binding.tvDescription.text.toString()

                val orderId = item.orderId
                Log.d("testsize", "onCreateView: " + myChosenSize);
                Log.d("testsize", "onCreateView:comfory " + myChosenComfort);
                Log.d("testsize", "onCreateView:quality " + myChosenQuality);
//            val size = RequestBody.create("text/plain".toMediaTypeOrNull(), myChosenSize)
//            val comfort = RequestBody.create("text/plain".toMediaTypeOrNull(), myChosenComfort)

//            val fileList = arrayListOf<MultipartBody.Part>()
//            images.forEach {
//                fileList.add(
//                    MultipartBody.Part.createFormData(
//                        "images",
//                        "img_" + images.indexOf(it) + ".jpg",
//                        RequestBody.create("image/*".toMediaTypeOrNull(), File(it.path))
//                    )
//                )
//            }
//            val subClasses = DataModel::class.sealedSubclasses.filter { clazz -> clazz == DataModel.Image::class }
                Log.d(TAG, "onCreateView: " + listImage);
                val imageFromModel = listImage.filterIsInstance<DataModel.Image>()
                Log.d(TAG, "onCreateView: " + imageFromModel);
                val imgIds = imageFromModel.map { (it as DataModel.Image).id }
                Log.d(TAG, "onCreateView: " + imgIds);
                viewModel.postReviewAPI(
                    PreferenceHandler.getToken(requireContext())!!,
                    desc,
                    rate,
                    orderId,
                    imgIds,
                    myChosenSize,
                    myChosenComfort,
                    myChosenQuality
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please wait! Your photos are being uploaded",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        return binding.root
    }

    private fun setTab(tabSize: TabLayout, tabComfort: TabLayout) {
        tabSize.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                myChosenSize = tabSize.getTabAt(position)?.text.toString()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        tabComfort.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                myChosenComfort = tabComfort.getTabAt(position)?.text.toString()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        myChosenSize=tabSize.getTabAt(tabSize.selectedTabPosition)?.text.toString()
        myChosenComfort=tabComfort.getTabAt(tabComfort.selectedTabPosition)?.text.toString()
    }

    private fun observePostReviewApi() {
        viewModel.observePostReviewApi().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dismiss()
                    isLoading = false
//                    binding.progressBar.rootContainer.visibility = View.GONE
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val message = jsonObject.get("message").asString
//                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//                            showDialog("",message )
                            if(message.contains("Thank you",ignoreCase = true)) {
                                (activity as MainActivity).showSnakbar(message)
                                parentFragmentManager.popBackStackImmediate()
                            }else{
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            }

                        } catch (e: Exception) {

                        }
                    }
                }
                Status.LOADING -> {
                    isLoading = true
//                    binding.progressBar.rootContainer.visibility = View.VISIBLE
                    progressDialog = ProgressDialog.newInstance("", "")
                    progressDialog.show(parentFragmentManager, "post_progress")
                }
                Status.ERROR -> {
                    isLoading = false
                    progressDialog.dismiss()

//                    binding.progressBar.rootContainer.visibility = View.GONE
                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun showDialog(
        title: String,
        message: String
    ) {
        val bind: DialogCustomBinding =
            DialogCustomBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(requireContext(), R.style.CustomDialog)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)
        dialog?.setContentView(bind.root)
        bind.textTitle.text = title
        bind.textMsg.text =
            message
        bind.dialogBtnYes.text = "Ok"
        bind.dialogBtnNo.visibility = View.GONE
        bind.dialogBtnYes.setOnClickListener {
            dialog?.dismiss()

        }
        dialog?.show()
    }

    private fun observeDeleteImageReviewApi() {
        viewModel.observeDeleteUploadAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    isLoading = false
                    binding.progressBarPhoto.visibility = View.GONE
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val message = jsonObject.get("message").asString
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            listImage.removeAt(myDeletePosition)
                            checkIfCamera()
                            adapterImage.notifyItemChanged(myDeletePosition)
                            adapterImage.notifyItemChanged(listImage.size)
                            images.removeAt(myDeletePosition)
                        } catch (e: Exception) {

                        }
                    }
                }
                Status.LOADING -> {
                    isLoading = true
                    binding.progressBarPhoto.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    isLoading = false
                    binding.progressBarPhoto.visibility = View.GONE

                    //Handle Error
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun initBundle() {
            arguments?.let {
//                FragmentMyReviewsList.initialPositionOfTab = 1
                item = it.getSerializable("datas") as Review
                Log.d("tetstitem", "initBundle: " + item);
                binding.name.text = item.product.name
                binding.itemId.text = "Item ID: ${item.orderCode}"
                Glide.with(requireContext()).load(item.product.image_url).into(binding.image)
                binding.description.text =
                    "${item.product.size?.let { "Size: " + it + "/ " } ?: run { "" }}Colour: ${item.product.color} / Qty: ${item.product.quantity}"

                if (item.reviewDetails.isReviewed) {
                    binding.tvDescription.setText(item.reviewDetails.description)
                    binding.ratingBar1.rating = item.reviewDetails.rate.toFloat()
                    val img = arrayListOf<Image>()
                    if (item.reviewDetails.imageUrl.size > 0) {
                        item.reviewDetails.imageUrl.forEach {
                            img.add(
                                Image(
                                    it.id.toLong(),
                                    "name",
                                    it.imageUrl
                                )
                            )
                        }
                    }
                    val testImage =
                        "https://images.unsplash.com/photo-1612151855475-877969f4a6cc?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8aGQlMjBpbWFnZXxlbnwwfHwwfHw%3D&w=1000&q=80"
                    formatMyTab(item.reviewDetails.size, item.reviewDetails.comfort?:"Uncomfortable")
                    binding.progressBarQuality.value = item.reviewDetails.quality.toFloat()
                    myChosenQuality = item.reviewDetails.quality.toInt()
                    myChosenSize = item.reviewDetails.size
                    myChosenComfort = item.reviewDetails.comfort
//                printImages(img)
                    images.clear()
                    images.addAll(img)
                    setUpImageInList(images, null)
                }else{
//                    FragmentMyReviewsList.initialPositionOfTab = 0
                }
            }
    }

    private fun setUpImageInList(
        imagesFromBundle: java.util.ArrayList<Image>?,
        imagesFromApi: List<Detail>?
    ) {
        if (imagesFromBundle != null) {
            //from bundle
            if (images.size > 0) {
                binding.llUploadImage.visibility = View.GONE
                listImage.clear()
                images.forEach {
                    listImage.add(DataModel.Image(it.id.toInt(), it.path))
                }
            }
        } else {
            //from api
//            listImage.clear() //to clear if response sends all images
            if (listImage.size > 0) {
                var shouldAddCamera = true
                val lastData = listImage.get(listImage.size - 1)
                val lastPos = listImage.size - 1
                if (lastData is DataModel.Camera) {
                    shouldAddCamera = false
                }
                if (!shouldAddCamera) {
                    listImage.removeAt(lastPos)
                }
            }
            imagesFromApi?.forEach {
                listImage.add(DataModel.Image(it.id, it.imageUrl))
            }
        }
        if (!(listImage.size == 0)) {
            binding.llUploadImage.visibility = View.GONE
            val camera = DataModel.Camera(
                R.drawable.ic_camera,
                true
            )
            listImage.add(camera)
            checkIfCamera()

            adapterImage.notifyDataSetChanged()
        } else {
            binding.llUploadImage.visibility = View.VISIBLE
        }
    }

    fun formatMyTab(title: String?, titleComfort: String) {
        val tabStrip3 = binding.tabSize.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip3.childCount) {
//            tabStrip3.getChildAt(i).setOnTouchListener(View.OnTouchListener { v, event -> true })
            if ((binding.tabSize.getTabAt(i)?.text.toString())?.equals(
                    title,
                    ignoreCase = true
                )
            ) {
                binding.tabSize.getTabAt(i)?.select().toString()
            } else {

            }
        }
        val tabStrip = binding.tabComfort.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
//            tabStrip3.getChildAt(i).setOnTouchListener(View.OnTouchListener { v, event -> true })
            if ((binding.tabComfort.getTabAt(i)?.text.toString())?.equals(
                    titleComfort,
                    ignoreCase = true
                )
            ) {
                binding.tabComfort.getTabAt(i)?.select().toString()
            } else {
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llUploadImage.setOnClickListener {
            if (!isDisabledImagePicker)
                openGalleryForImages()
            else
                Toast.makeText(
                    requireContext(),
                    "Image upload is limited to 4 photos",
                    Toast.LENGTH_SHORT
                ).show()

        }
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            ReviewViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[ReviewViewModel::class.java]
    }


    private fun initAppbar() {
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "Review",
            textDescription = ""
        )
        (activity as MainActivity).showBottomNavigation(false)

    }


    private fun initRecyclerview() {
        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.FLEX_START
        }
//        imageString.addAll(listOf<String>())
        adapterImage = AdapterImageViewType(listImage)
        binding.rvImage.apply {
            layoutManager = flexboxLayoutManager
            adapter = adapterImage
            val itemDecoration = ItemOffsetDecoration(context, R.dimen.item_offset_image)
            addItemDecoration(itemDecoration)
            setItemAnimator(null)
        }
        //for adding image
        adapterImage.setReviewAddClickListener {
            if (!isDisabledImagePicker)
                openGalleryForImages()
            else
                Toast.makeText(
                    requireContext(),
                    "Image upload is limited to 4 photos",
                    Toast.LENGTH_SHORT
                ).show()
        }
        //for viewing image
        adapterImage.setReviewClickListener { imageList, i ->
            val image=imageList as List<DataModel.Image>
            Log.d("testimage", "inirRecyclerView: "+image.size);
            val frag= FragmentImageSwiper.newInstance(image.map { it.image },i)
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment,frag).addToBackStack(null).commit()
        }
        adapterImage.setReviewDeleteClickListener { it, pos ->
            myDeletePosition = pos
            viewModel.imageDeleteAPI(it.id)
        }
    }

    private fun openGalleryForImages() {
        if (imagePickerLauncher == null) {
            Log.d(TAG, "start:null ");
        } else {
            Log.d(TAG, "start:not null ");
        }
        imagePickerLauncher?.launch(createConfig())
    }

    private fun createConfig(): ImagePickerConfig {
        val returnAfterCapture = false
        val isSingleMode = false
        val useCustomImageLoader = false
        val folderMode = true
        val includeVideo = false
        val onlyVideo = false
        val isExclude = false
        Log.d(TAG, "start:inside config ");

        ImagePickerComponentsHolder.setInternalComponent(
            CustomImagePickerComponents(requireContext(), useCustomImageLoader)
        )

        return ImagePickerConfig {

            mode = if (isSingleMode) {
                ImagePickerMode.SINGLE
            } else {
                ImagePickerMode.MULTIPLE // multi mode (default mode)
            }
//            language = "in" // Set image picker language
            theme = R.style.ImagePickerTheme

            // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
            returnMode = if (returnAfterCapture) ReturnMode.ALL else ReturnMode.NONE

            isFolderMode = folderMode // set folder mode (false by default)
            isIncludeVideo = includeVideo // include video (false by default)
            isOnlyVideo = onlyVideo // include video (false by default)
            arrowColor = Color.WHITE // set toolbar arrow up color
            folderTitle = "Folder" // folder selection title
            imageTitle = "Tap to select" // image selection title
            doneButtonText = "DONE" // done button text
            showDoneButtonAlways = true // Show done button always or not
            limit = MY_PHOTO_NUMBER // max images can be selected (99 by default)
            isShowCamera = false // show camera or not (true by default)
            savePath =
                ImagePickerSavePath("Camera") // captured image directory name ("Camera" folder by default)
            savePath = ImagePickerSavePath(
                Environment.getExternalStorageDirectory().path,
                isRelative = false
            ) // can be a full path

            if (isExclude) {
                excludedImages = images.toFiles() // don't show anything on this selected images
            } else {
                selectedImages = images  // original selected images, used in multi mode
            }
            Log.d(TAG, "createConfig: " + selectedImages.size);
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: ");
        super.onAttach(context)
        imagePickerLauncher = registerImagePicker {
            images.clear()
            images.addAll(it)
            printImages(images)
            Log.d(TAG, "start: register");
        }
    }

    private fun printImages(images: List<Image>?) {
        if (images == null) return
        if (images.size > 0) {
            setModelData()
        }
    }

    private fun setModelData() {
        val fileList = arrayListOf<MultipartBody.Part>()
        images.forEach {
            fileList.add(
                MultipartBody.Part.createFormData(
                    "images",
                    "img_" + images.indexOf(it) + ".jpg",
                    RequestBody.create("image/*".toMediaTypeOrNull(), File(it.path))
                )
            )
        }
        viewModel.imageUploadAPI(fileList)
    }


    private fun observeImageUploadApi() {
        viewModel.observeimageUploadAPI().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.progressBarPhoto.visibility = View.GONE
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val imageResponse =
                                Gson().fromJson(jsonObject, ImageUploadResponse::class.java)
                            if (imageResponse.details != null) {
                                setUpImageInList(null, imageResponse.details)
//                                listImage.clear()
//                                imageResponse.details.forEach {
//                                    listImage.add(DataModel.Image(it.id, it.imageUrl))
//                                }
                            }
//                            val camera = DataModel.Camera(
//                                R.drawable.ic_camera,
//                                true
//                            )
//                            listImage.add(camera)
//                            checkIfCamera()
//                            adapterImage.notifyDataSetChanged()


                        } catch (e: Exception) {

                        }
                    }
                }
                Status.LOADING -> {
                    binding.progressBarPhoto.visibility = View.VISIBLE
                    binding.llUploadImage.visibility = View.GONE

                }
                Status.ERROR -> {
                    //Handle Error
                    binding.llUploadImage.visibility = View.VISIBLE
                    binding.progressBarPhoto.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun checkIfCamera() {
        Log.d(TAG, "checkIfCamera: " + (listImage.size));
        Log.d(TAG, "checkIfCamera: " + (listImage.size < 5));
//        (listImage[listImage.size - 1] as DataModel.Camera).isEnabled = listImage.size < 5
        isDisabledImagePicker = !(listImage.size < 5)

    }

}