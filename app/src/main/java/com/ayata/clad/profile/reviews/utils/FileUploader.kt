package com.ayata.clad.profile.reviews.utils

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class FileUploader(private var uploadInterface: UploadInterface) {
    var fileUploaderCallback: FileUploaderCallback? = null
    private lateinit var files: Array<File>
    var uploadIndex = -1
    private var totalFileLength: Long = 0
    private var totalFileUploaded: Long = 0

    private lateinit var responses: Array<String?>

    interface FileUploaderCallback {
        fun onError()
        fun onFinish(responses: Array<String?>?)
        fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int)
    }

    interface UploadInterface {
        fun uploadFile(file: File, int: Int)
    }

    inner class PRRequestBody(private val mFile: File) : RequestBody() {
        override fun contentType(): MediaType? {
            // i want to upload only images
            return "image/*".toMediaTypeOrNull()
        }

        @Throws(IOException::class)
        override fun contentLength(): Long {
            return mFile.length()
        }

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            val fileLength = mFile.length()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val `in` = FileInputStream(mFile)
            var uploaded: Long = 0
            try {
                var read: Int
                val handler = Handler(Looper.getMainLooper())
                while (`in`.read(buffer).also { read = it } != -1) {

                    // update progress on UI thread
                    handler.post(ProgressUpdater(uploaded, fileLength))
                    uploaded += read.toLong()
                    sink.write(buffer, 0, read)
                }
            } finally {
                `in`.close()
            }
        }


        private  val DEFAULT_BUFFER_SIZE = 2048

    }

    @JvmOverloads
    fun uploadFiles(
        files: Array<File>,
        fileUploaderCallback: FileUploaderCallback?,
    ) {
        this.fileUploaderCallback = fileUploaderCallback
        this.files = files
        uploadIndex = -1
        totalFileUploaded = 0
        totalFileLength = 0
        uploadIndex = -1
        responses = arrayOfNulls(files.size)
        for (i in files.indices) {
            totalFileLength = totalFileLength + files[i].length()
        }
        uploadNext()
    }
    private fun uploadNext() {
        if (files.size > 0) {
            if (uploadIndex != -1) totalFileUploaded =
                totalFileUploaded + files[uploadIndex].length()
            uploadIndex++
            if (uploadIndex < files.size) {
                uploadSingleFile(uploadIndex)
            } else {
                fileUploaderCallback!!.onFinish(responses)
            }
        } else {
            fileUploaderCallback!!.onFinish(responses)
        }
    }

    private fun uploadSingleFile(index: Int) {
        uploadInterface.uploadFile(files[index], index)
    }

    fun success(it: String, index: Int) {
        responses[index] = it
    }

    fun failed(index: Int) {
        fileUploaderCallback!!.onError()
    }
    private inner class ProgressUpdater(private val mUploaded: Long, private val mTotal: Long) :
        Runnable {
        override fun run() {
            val current_percent = (100 * mUploaded / mTotal).toInt()
            val total_percent = (100 * (totalFileUploaded + mUploaded) / totalFileLength).toInt()
            fileUploaderCallback!!.onProgressUpdate(current_percent, total_percent, uploadIndex + 1)
        }
    }
}