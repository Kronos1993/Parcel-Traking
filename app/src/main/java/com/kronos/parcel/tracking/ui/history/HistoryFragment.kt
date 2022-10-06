package com.kronos.parcel.tracking.ui.history

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.adapters.SwipeToDelete
import com.kronos.core.extensions.fragmentBinding
import com.kronos.core.util.LoadingDialog
import com.kronos.core.util.SnackBarUtil
import com.kronos.parcel.tracking.R
import com.kronos.parcel.tracking.databinding.FragmentHistoryBinding
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.parcel.tracking.MainState
import com.kronos.parcel.tracking.ui.history.state.HistoryState
import com.kronos.parcel.tracking.ui.home.CURRENT_PARCEL
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
        observeViewModel()
        initViews()
        initViewModel()
        root
    }



    private fun observeViewModel() {
        viewModel.parcelList.observe(this.viewLifecycleOwner, ::handleParcelList)
        viewModel.loading.observe(this.viewLifecycleOwner, ::handleLoading)
        viewModel.error.observe(this.viewLifecycleOwner, ::handleError)
        viewModel.state.observe(this.viewLifecycleOwner, ::handleHistoryState)
    }

    private fun handleHistoryState(state: MainState) {
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

    private fun handleParcelList(list: List<ParcelModel>) {
        viewModel.parcelAdapter.submitList(list)
        viewModel.parcelAdapter.notifyDataSetChanged()
    }

    private fun initViews() {
        binding.recyclerViewParcelsHistory.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewParcelsHistory.setHasFixedSize(false)
        binding.recyclerViewParcelsHistory.adapter = viewModel.parcelAdapter
        viewModel.parcelAdapter.setAdapterItemClick(object : AdapterItemClickListener<ParcelModel> {
            override fun onItemClick(t: ParcelModel, pos: Int) {
                var bundle = Bundle()
                bundle.putSerializable(CURRENT_PARCEL, t)
                findNavController().navigate(R.id.navigation_parcel_details, bundle)
            }

        })

        val itemTouchHelperCallback: ItemTouchHelper.Callback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.LEFT->{
                        viewModel.deleteParcel(
                            viewModel.parcelAdapter.getItemAt(viewHolder.adapterPosition)
                        )
                    }
                    ItemTouchHelper.RIGHT->{
                        viewModel.restoreParcel(viewModel.parcelAdapter.getItemAt(viewHolder.adapterPosition))
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(
            SwipeToDelete(
                ContextCompat.getDrawable(
                    requireContext(),
                    com.kronos.resources.R.drawable.ic_delete
                )!!,
                ColorDrawable(ContextCompat.getColor(requireContext(),com.kronos.resources.R.color.snack_bar_error_background)),
                ContextCompat.getDrawable(
                    requireContext(),
                    com.kronos.resources.R.drawable.ic_unarchive
                )!!,
                ColorDrawable(ContextCompat.getColor(requireContext(),com.kronos.resources.R.color.green)),
                itemTouchHelperCallback,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewParcelsHistory)
    }

    private fun initViewModel() {
        viewModel.getParcels()
    }

}