package com.kronos.parcel.tracking.ui.add_parcel

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kronos.core.extensions.binding.fragmentBinding
import com.kronos.parcel.tracking.R
import com.kronos.parcel.tracking.databinding.FragmentDialogAddParcelBinding
import com.kronos.parcel.tracking.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class AddParcelFragment : BottomSheetDialogFragment() {

    private val binding by fragmentBinding<FragmentDialogAddParcelBinding>(R.layout.fragment_dialog_add_parcel)

    private val homeViewModel by activityViewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.run {
        viewModel = this@AddParcelFragment.homeViewModel
        lifecycleOwner = this@AddParcelFragment.viewLifecycleOwner
        setListeners()
        root
    }

    private fun setListeners() {
        binding.buttonAddParcel.setOnClickListener {
            if (homeViewModel.validateField()){
                hideDialog()
                homeViewModel.getNewParcel()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpDialog()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    private fun setUpDialog() {
        this.isCancelable = false
        binding.imageView.setOnClickListener {
            hideDialog()
        }
    }

    private fun hideDialog() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroy() {
        binding.unbind()
        super.onDestroy()
    }

}