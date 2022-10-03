package com.kronos.parcel.traking.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.extensions.fragmentBinding
import com.kronos.core.util.LoadingDialog
import com.kronos.core.util.SnackBarUtil
import com.kronos.zipcargo.domain.model.parcel.ParcelModel
import com.kronos.myparceltraking.R
import com.kronos.myparceltraking.databinding.FragmentHistoryBinding
import com.kronos.parcel.traking.ui.history.state.HistoryState
import java.util.*

class HistoryFragment : Fragment() {

    private val binding by fragmentBinding<FragmentHistoryBinding>(R.layout.fragment_history)

    private val viewModel by activityViewModels<HistoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.run {
        viewModel = this@HistoryFragment.viewModel
        lifecycleOwner = this@HistoryFragment.viewLifecycleOwner
        initViewModel()
        observeViewModel()
        initViews()
        root
    }



    private fun observeViewModel() {
        viewModel.parcelList.observe(this.viewLifecycleOwner, ::handleParcelList)
        viewModel.loading.observe(this.viewLifecycleOwner, ::handleLoading)
        viewModel.error.observe(this.viewLifecycleOwner, ::handleError)
        viewModel.state.observe(this.viewLifecycleOwner, ::handleHistoryState)
    }

    private fun handleHistoryState(state: HistoryState) {
        when (state) {
            is HistoryState.Loading -> {
                handleLoading(state.loading)
            }
            is HistoryState.Error -> {
                handleError(state.error)
            }
        }
    }

    private fun handleError(hashtable: Hashtable<String, String>) {
        if (hashtable["error"] != null) {
            if (hashtable["error"]!!.isNotEmpty()) {
                SnackBarUtil.show(
                    binding.recyclerViewParcelsHistory,
                    hashtable["error"].orEmpty(),
                    com.kronos.resources.R.color.snack_bar_white,
                    com.kronos.resources.R.color.snack_bar_error_background
                )
            } else {
                SnackBarUtil.show(
                    binding.recyclerViewParcelsHistory,
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
                com.kronos.resources.R.color.teal_700
            )!!.show()
        } else {
            LoadingDialog.getProgressDialog(
                requireContext(),
                R.string.loading_dialog_text,
                com.kronos.resources.R.color.teal_700
            )!!.dismiss()
        }
    }

    private fun handleParcelList(list: List<ParcelModel>) {
        viewModel.parcelAdapter.submitList(list)
        if (list.isEmpty()) {
            binding.recyclerViewParcelsHistory.visibility = View.GONE
            binding.textHome.visibility = View.VISIBLE
        } else {
            binding.recyclerViewParcelsHistory.visibility = View.VISIBLE
            binding.textHome.visibility = View.GONE
        }
        viewModel.parcelAdapter.notifyDataSetChanged()
    }

    private fun initViews() {
        binding.recyclerViewParcelsHistory.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewParcelsHistory.setHasFixedSize(false)
        binding.recyclerViewParcelsHistory.adapter = viewModel.parcelAdapter
        viewModel.parcelAdapter.setAdapterItemClick(object : AdapterItemClickListener<ParcelModel> {
            override fun onItemClick(t: ParcelModel, pos: Int) {
            }

        })
    }

    private fun initViewModel() {
        viewModel.getParcels()
    }

}