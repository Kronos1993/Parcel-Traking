package com.kronos.parcel.tracking.ui.parcel_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.extensions.binding.fragmentBinding
import com.kronos.core.util.LoadingDialog
import com.kronos.core.util.show
import com.kronos.domain.model.event.EventModel
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.parcel.tracking.MainState
import com.kronos.parcel.tracking.R
import com.kronos.parcel.tracking.databinding.FragmentParcelDetailsBinding
import com.kronos.parcel.tracking.ui.home.CURRENT_PARCEL
import com.kronos.parcel.tracking.ui.notifications.EventAdapter
import com.kronos.parcel.tracking.ui.parcel_details.state.ParcelDetailState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
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
        viewModel.observeTextChange()
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
        viewModel.eventAdapter.get()?.submitList(list)
        viewModel.eventAdapter.get()?.notifyDataSetChanged()
    }

    private fun initViews() {
        binding.recyclerViewCurrentParcelEvents.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCurrentParcelEvents.setHasFixedSize(false)
        if (viewModel.eventAdapter.get()==null)
            viewModel.eventAdapter = WeakReference(EventAdapter())
        binding.recyclerViewCurrentParcelEvents.adapter = viewModel.eventAdapter.get()
        viewModel.eventAdapter.get()?.setAdapterItemClick(object : AdapterItemClickListener<EventModel> {
            override fun onItemClick(t: EventModel, pos: Int) {
            }

        })
        binding.buttonUpdateParcel.setOnClickListener {
            if (viewModel.validateField()) {
                viewModel.updateParcelFields()
            }
        }

        binding.buttonScanBarCode.setOnClickListener {
            barcodeLauncher.launch(viewModel.createBarcodeOptions())
        }
    }

    private fun initViewModel() {
        val bundle = arguments
        if (bundle?.get(CURRENT_PARCEL) != null) {
            viewModel.postParcel(bundle.get(CURRENT_PARCEL) as ParcelModel)
            viewModel.getEvents()
        } else {
            findNavController().popBackStack()
        }
    }

    override fun onPause() {
        binding.unbind()
        super.onPause()
    }

    override fun onDestroyView() {
        binding.unbind()
        super.onDestroyView()
    }

    // Register the launcher and result handler
    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                viewModel.trackingNumber.set(result.contents)
            }
        }
}