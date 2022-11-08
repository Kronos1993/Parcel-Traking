package com.kronos.parcel.tracking.ui.parcel_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.extensions.fragmentBinding
import com.kronos.core.util.LoadingDialog
import com.kronos.core.util.show
import com.kronos.domain.model.event.EventModel
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.parcel.tracking.MainState
import com.kronos.parcel.tracking.R
import com.kronos.parcel.tracking.databinding.FragmentParcelDetailsBinding
import com.kronos.parcel.tracking.ui.home.CURRENT_PARCEL
import com.kronos.parcel.tracking.ui.parcel_details.state.ParcelDetailState
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ParcelDetailsFragment : Fragment() {

    private val binding by fragmentBinding<FragmentParcelDetailsBinding>(R.layout.fragment_parcel_details)

    private val viewModel by activityViewModels<ParcelDetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.run {
        viewModel = this@ParcelDetailsFragment.viewModel
        lifecycleOwner = this@ParcelDetailsFragment.viewLifecycleOwner
        observeViewModel()
        initViews()
        initViewModel()
        root
    }

    private fun observeViewModel() {
        viewModel.parcel.observe(this.viewLifecycleOwner, ::handleParcel)
        viewModel.eventList.observe(this.viewLifecycleOwner, ::handleEventList)
        viewModel.loading.observe(this.viewLifecycleOwner, ::handleLoading)
        viewModel.state.observe(this.viewLifecycleOwner, ::handleState)
    }

    private fun handleState(state: MainState) {
        when (state) {
            is ParcelDetailState.Loading -> {
                handleLoading(state.loading)
            }
            is ParcelDetailState.Error -> {
                handleError(state.error)
            }
        }
    }

    private fun handleError(hashtable: Hashtable<String, String>) {
        if (hashtable["error"] != null) {
            if (hashtable["error"]!!.isNotEmpty()) {
                show(
                    binding.imageStatus,
                    hashtable["error"].orEmpty(),
                    com.kronos.resources.R.color.snack_bar_white,
                    com.kronos.resources.R.color.snack_bar_error_background
                )
            } else {
                show(
                    binding.imageStatus,
                    hashtable["error"].orEmpty(),
                    com.kronos.resources.R.color.snack_bar_white,
                    com.kronos.resources.R.color.snack_bar_success_background
                )
            }
        }
    }

    private fun handleLoading(b: Boolean) {
        if (b) {
            LoadingDialog.getProgressDialog(
                requireContext(),
                R.string.loading_dialog_text,
                com.kronos.resources.R.color.colorSecondaryVariant
            )!!.show()
        } else {
            LoadingDialog.getProgressDialog(
                requireContext(),
                R.string.loading_dialog_text,
                com.kronos.resources.R.color.colorSecondaryVariant
            )!!.dismiss()
        }
    }

    private fun handleParcel(parcel: ParcelModel) {
        Glide.with(requireContext()).load(viewModel.getImageUrl(parcel)).into(binding.imageStatus)
    }

    private fun handleEventList(list: List<EventModel>) {
        viewModel.eventAdapter?.submitList(list)
        viewModel.eventAdapter?.notifyDataSetChanged()
    }

    private fun initViews() {
        binding.recyclerViewCurrentParcelEvents.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCurrentParcelEvents.setHasFixedSize(false)
        binding.recyclerViewCurrentParcelEvents.adapter = viewModel.eventAdapter
        viewModel.eventAdapter?.setAdapterItemClick(object : AdapterItemClickListener<EventModel> {
            override fun onItemClick(t: EventModel, pos: Int) {
            }

        })
        binding.buttonUpdateParcel.setOnClickListener{
            if (validateField()){
                viewModel.updateParcelFields(
                    binding.editTextTrackingNumber.text.toString(),
                    binding.editTextName.text.toString()
                )
            }
        }
    }

    private fun initViewModel() {
        var bundle = arguments
        if (bundle?.get(CURRENT_PARCEL) != null) {
            viewModel.postParcel(bundle.get(CURRENT_PARCEL) as ParcelModel)
            viewModel.getEvents()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun validateField() : Boolean{
        var valid = true
        if (binding.editTextTrackingNumber.text!!.isEmpty()){
            valid = false
            binding.textInputLayoutTrackingNumber.error = getString(com.kronos.resources.R.string.required_field)
        }else{
            binding.textInputLayoutTrackingNumber.error = null
        }
        if (binding.editTextName.text!!.isEmpty()){
            valid = false
            binding.textInputLayoutParcelName.error = getString(com.kronos.resources.R.string.required_field)
        }else{
            binding.textInputLayoutParcelName.error = null
        }
        return valid
    }

    override fun onPause() {
        viewModel.eventAdapter = null
        binding.unbind()
        super.onPause()
    }

    override fun onDestroyView() {
        viewModel.eventAdapter = null
        binding.unbind()
        super.onDestroyView()
    }
}