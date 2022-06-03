package com.ayata.clad.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.ayata.clad.CustomExceptionHandler
import com.ayata.clad.R
import com.ayata.clad.databinding.DialogReferalBinding
import com.ayata.clad.databinding.DialogShoppingSizeBinding

class DialogWithData(var type: Int, var handleException: CustomExceptionHandler) :
    DialogFragment() {
    private val DIALOG_TYPE_REFERAL = 0
    private val DIALOG_TYPE_TEST = 1
    private var _binding: Any? = null

    // This property is only valid between onCreateDialog and
    // onDestroyView.
    private val binding get() = _binding!!


    companion object {

        const val TAG = "DialogWithData"

    }

    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            when (type) {
                DIALOG_TYPE_REFERAL -> {
                    _binding = DialogReferalBinding.inflate(LayoutInflater.from(context))
                    return (_binding as DialogReferalBinding).root
                }

                DIALOG_TYPE_TEST -> {
                    _binding = DialogShoppingSizeBinding.inflate(LayoutInflater.from(context))
                    return (_binding as DialogShoppingSizeBinding).root
                }
                else -> {
                    handleException.handleException("Activity cannot be null")
                }
            }
        } ?: { handleException.handleException("Activity cannot be null")}
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        when (type) {
            0 -> setupClickListeners(view)
            1 -> test(view)
            else -> {
            }
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupClickListeners(view: View) {
        (binding as DialogReferalBinding).loginFromReferal.setOnClickListener {
            viewModel.sendName(view.findViewById<EditText>(R.id.code).text.toString())
            dismiss()
        }
    }

    private fun test(view: View) {
        Toast.makeText(context, "test", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}