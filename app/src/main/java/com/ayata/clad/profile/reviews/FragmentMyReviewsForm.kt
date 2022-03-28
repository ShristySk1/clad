package com.ayata.clad.profile.reviews

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayata.clad.MainActivity
import com.ayata.clad.R
import com.ayata.clad.databinding.FragmentMyReviewFormBinding
import com.ayata.clad.productlist.ItemOffsetDecoration
import com.ayata.clad.profile.reviews.adapter.AdapterImageViewType
import com.ayata.clad.profile.reviews.adapter.DataModel
import com.ayata.clad.profile.reviews.utils.CustomImagePickerComponents
import com.esafirm.imagepicker.features.*
import com.esafirm.imagepicker.model.Image
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager


class FragmentMyReviewsForm : Fragment() {
    lateinit var binding: FragmentMyReviewFormBinding
    lateinit var adapterImage: AdapterImageViewType
    private val images = ArrayList<Image>()
    lateinit var imagePickerLauncher: ImagePickerLauncher
    val TAG = "FragmentMyReviewsForm"
    val listImage = mutableListOf<DataModel>()
    val imageString = ArrayList<String>()
    val MY_PHOTO_NUMBER=4

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentMyReviewFormBinding.inflate(inflater, container, false)
        initAppbar()
        initRecyclerview()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llUploadImage.setOnClickListener {
            openGalleryForImages()
        }
    }

    private fun initRecyclerview() {
        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.FLEX_START
        }
        imageString.addAll(listOf<String>())
        adapterImage = AdapterImageViewType(listImage)
        binding.rvImage.apply {
            layoutManager = flexboxLayoutManager
            adapter = adapterImage
            val itemDecoration = ItemOffsetDecoration(context, R.dimen.item_offset_image)
            addItemDecoration(itemDecoration)
        }
        adapterImage.setReviewAddClickListener {
            val newLimit=MY_PHOTO_NUMBER-(listImage.size-1)
            Log.d(TAG, "initRecyclerview: "+newLimit);
            openGalleryForImages()
        }
        adapterImage.setReviewDeleteClickListener { it, pos ->
            listImage.removeAt(pos)
            checkIfCamera()
            adapterImage.notifyItemChanged(pos)
            adapterImage.notifyItemChanged(listImage.size)
            images.removeAt(pos)
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

    private fun initAppbar() {
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setToolbar2(
            isClose = false, isBack = true, isFilter = false, isClear = false,
            textTitle = "Review",
            textDescription = ""
        )
        (activity as MainActivity).showBottomNavigation(false)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK && requestCode == IpCons.RC_IMAGE_PICKER && data != null) {
//            images.clear()
//            images.addAll(ImagePicker.getImages(data) ?: emptyList())
//            Log.d("imhere", "onActivityResult: ");
//            printImages(images)
//            return
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }

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
            Log.d(TAG, "createConfig: "+selectedImages.size);
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
        imageString.clear()
        for (image in images) {
            imageString.add(image.uri.toString())
        }
        setModelData(imageString)
    }

    private fun setModelData(list: ArrayList<String>) {
        val datas = list.map {
            DataModel.Image(it)
        }
        val camera = DataModel.Camera(
            R.drawable.ic_add,
            true
        )
        listImage.clear()
        listImage.addAll(datas)
        listImage.add(camera)
        checkIfCamera()
        adapterImage.notifyDataSetChanged()
        binding.llUploadImage.visibility=View.GONE
    }

    private fun checkIfCamera() {
        Log.d(TAG, "checkIfCamera: " + (listImage.size));
        Log.d(TAG, "checkIfCamera: " + (listImage.size < 5));
        (listImage[listImage.size - 1] as DataModel.Camera).isEnabled = listImage.size < 5
    }

}