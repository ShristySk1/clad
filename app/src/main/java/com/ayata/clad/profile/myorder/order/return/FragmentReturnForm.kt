package com.ayata.clad.profile.myorder.order.`return`

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.ayata.clad.R
import com.ayata.clad.data.network.ApiService
import com.ayata.clad.data.network.Status
import com.ayata.clad.data.repository.ApiRepository
import com.ayata.clad.databinding.FragmentCancelFormBinding
import com.ayata.clad.databinding.FragmentReturnFormBinding
import com.ayata.clad.productlist.ItemOffsetDecoration
import com.ayata.clad.profile.address.CustomArrayAdapter
import com.ayata.clad.profile.address.ModelTest
import com.ayata.clad.profile.myorder.order.cancel.CancelViewModel
import com.ayata.clad.profile.myorder.order.response.Order
import com.ayata.clad.profile.myorder.viewmodel.OrderViewModel
import com.ayata.clad.profile.myorder.viewmodel.OrderViewModelFactory
import com.ayata.clad.profile.reviews.adapter.AdapterImageViewType
import com.ayata.clad.profile.reviews.adapter.DataModel
import com.ayata.clad.profile.reviews.imageswipe.FragmentImageSwiper
import com.ayata.clad.profile.reviews.response.Detail
import com.ayata.clad.profile.reviews.response.ImageUploadResponse
import com.ayata.clad.profile.reviews.utils.CustomImagePickerComponents
import com.ayata.clad.utils.PreferenceHandler
import com.ayata.clad.utils.ProgressDialog
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.*
import com.esafirm.imagepicker.model.Image
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

const val MY_PHOTO_NUMBER = 4

class FragmentReturnForm : Fragment() {
    private val TAG: String="FragmentReturnForm"
    lateinit var binding: FragmentReturnFormBinding
    private lateinit var viewModel: OrderViewModel
    lateinit var cancelViewmodel: CancelViewModel
    lateinit var o: Order
    private var reason_ = "";
    lateinit var progressDialog: ProgressDialog

    //images
    lateinit var adapterImage: AdapterImageViewType
    private val images = ArrayList<Image>()
    lateinit var imagePickerLauncher: ImagePickerLauncher
    val listImage = mutableListOf<DataModel>()
    var isLoading = false
    var myDeletePosition = -1
    var isDisabledImagePicker = false
    //images end

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentReturnFormBinding.inflate(inflater, container, false)
        setSpinner()
        initView()
        setUpViewModel()
        initRecyclerview()
        observeCancelOrder()
        observeImageUploadApi()
        observeDeleteImageReviewApi()
        binding.btnReturnOrder.setOnClickListener {
            if(!isLoading) {
//            val comment = binding.tvDescription.text.toString()
                val imageFromModel = listImage.filterIsInstance<DataModel.Image>()
                Log.d(TAG, "onCreateView: " + imageFromModel);
                val imgIds = imageFromModel.map { (it).id }
                Log.d("myimageids", "onCreateView: " + imgIds);
//            viewModel.cancelOrderApi(
//                PreferenceHandler.getToken(context)!!,
//                o.orderId,
//                reason_,
//                comment
//            )
                Toast.makeText(requireContext(), "Api in progress", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private fun observeCancelOrder() {
        viewModel.observeCancelOrderResponse().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {

                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {

                            Toast.makeText(
                                requireContext(),
                                jsonObject.get("message").toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            cancelViewmodel.setCancelDetails(true)
                            progressDialog.dismiss()
                            //directly go to order list fragment
                            parentFragmentManager.popBackStack(
                                "order_list",
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
                        } catch (e: Exception) {
                        }
                    }

                }
                Status.LOADING -> {
                    progressDialog = ProgressDialog.newInstance("", "")
                    progressDialog.show(parentFragmentManager, "cancel_progress")

                }
                Status.ERROR -> {
                    progressDialog.dismiss()
                    //Handle Error
                    if (it.message.equals("Unauthorized")) {

                    } else {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()

                    }
                    Log.d("", "home: ${it.message}")
                }
            }
        })
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            OrderViewModelFactory(ApiRepository(ApiService.getInstance(requireContext())))
        )[OrderViewModel::class.java]
        cancelViewmodel = ViewModelProviders.of(requireActivity()).get(CancelViewModel::class.java)

    }

    private fun initView() {
        arguments?.let {
            o = it?.getSerializable("order") as Order
            if (PreferenceHandler.getCurrency(context).equals("npr", true)) {
                binding.price.text = getString(R.string.rs) + "${o.products.variant.nprPrice}"
            } else {
                binding.price.text = getString(R.string.usd) + "${o.products.variant.dollarPrice}"
            }
            binding.name.text = o.products.name
            binding.itemId.text = "Item ID: ${o.products.productId}"
            Glide.with(requireContext()).load(o.products.imageUrl).into(binding.image)
            binding.description.text =
                "${o.products.variant.size?.let { "Size: " + it + "/ " } ?: run { "" }}Colour: ${o.products.variant.color} / Qty: ${o.products.quantity}"
        }
        //image
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

    private fun setSpinner() {
        val listReason =
            arrayListOf<ModelTest>(
                ModelTest(arrayListOf(), "Damaged Product"),
                ModelTest(arrayListOf(), "Wrong Product")
            )
        if (binding.spinner != null) {
            val adapter =
                CustomArrayAdapter(
                    requireContext(),
                    R.layout.spinner_custom,
                    R.id.text_view,
                    listReason
                )
            binding.spinner.adapter = adapter
            binding.spinner.setPrompt("Select Reason");
            binding.spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {
                    reason_ = listReason[position].state
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

    }


    //images
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
        adapterImage.setReviewDeleteClickListener { it, pos ->
            myDeletePosition = pos
            viewModel.imageDeleteAPI(it.id)
        }
        //for viewing image
        adapterImage.setReviewClickListener { imageList, i ->
            val image=imageList as List<DataModel.Image>
            Log.d("testimage", "inirRecyclerView: "+image.size);
            val frag= FragmentImageSwiper.newInstance(image.map { it.image },i)
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment,frag).addToBackStack(null).commit()
        }
    }

    private fun openGalleryForImages() {
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
                    isLoading=false
                    binding.progressBarPhoto.visibility = View.GONE
                    val jsonObject = it.data
                    if (jsonObject != null) {
                        try {
                            val imageResponse =
                                Gson().fromJson(jsonObject, ImageUploadResponse::class.java)
                            if (imageResponse.details != null) {
                                setUpImageInList(null, imageResponse.details)
                            }
                        } catch (e: Exception) {

                        }
                    }
                }
                Status.LOADING -> {
                    binding.progressBarPhoto.visibility = View.VISIBLE
                    binding.llUploadImage.visibility = View.GONE
                    isLoading=true

                }
                Status.ERROR -> {
                    //Handle Error
                    isLoading=false
                    binding.llUploadImage.visibility = View.VISIBLE
                    binding.progressBarPhoto.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

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

    private fun checkIfCamera() {
        isDisabledImagePicker = !(listImage.size < 5)

    }
}