package com.ayata.clad.profile.reviews.utils

import android.content.Context
import com.esafirm.imagepicker.features.DefaultImagePickerComponents
import com.esafirm.imagepicker.features.imageloader.DefaultImageLoader
import com.esafirm.imagepicker.features.imageloader.ImageLoader
import com.esafirm.sample.GrayscaleImageLoader

class CustomImagePickerComponents(
    context: Context,
    private val useCustomImageLoader: Boolean
) : DefaultImagePickerComponents(context.applicationContext) {
    override val imageLoader: ImageLoader
        get() = if (useCustomImageLoader) {
            GrayscaleImageLoader()
        } else {
            DefaultImageLoader()
        }
}