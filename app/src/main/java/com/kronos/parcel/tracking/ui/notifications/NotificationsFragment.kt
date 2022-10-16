package com.kronos.parcel.tracking.ui.notifications

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.adapters.SwipeToDelete
import com.kronos.core.extensions.fragmentBinding
import com.kronos.core.util.LoadingDialog
import com.kronos.core.util.show
import com.kronos.domain.model.event.EventModel
import com.kronos.parcel.tracking.R
import com.kronos.parcel.tracking.databinding.FragmentNotificationsBinding
import com.kronos.parcel.tracking.ui.parcel_details.state.ParcelDetailState
import java.util.*

class NotificationsFragment : Fragment() {

    private val binding by fragmentBinding<FragmentNotificationsBinding>(R.layout.fragment_notifications)

    private val viewModel by activityViewModels<NotificationsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.run {
        viewModel = this@NotificationsFragment.viewModel
        lifecycleOwner = this@NotificationsFragment.viewLifecycleOwner
        observeViewModel()
        initViews()
        initViewModel()
        root
    }

    private fun observeViewModel() {
        viewModel.eventList.observe(this.viewLifecycleOwner, ::handleEventList)
        viewModel.loading.observe(this.viewLifecycleOwner, ::handleLoading)
        viewModel.error.observe(this.viewLifecycleOwner, ::handleError)
        viewModel.state.observe(this.viewLifecycleOwner, ::handleEventState)
    }

    private fun handleEventState(state: ParcelDetailState) {
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
                    binding.recyclerViewEvents,
                    hashtable["error"].orEmpty(),
                    com.kronos.resources.R.color.snack_bar_white,
                    com.kronos.resources.R.color.snack_bar_error_background
                )
            } else {
                show(
                    binding.recyclerViewEvents,
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

    private fun handleEventList(list: List<EventModel>) {
        viewModel.eventAdapter.submitList(list)
        viewModel.eventAdapter.notifyDataSetChanged()
    }

    private fun initViews() {
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewEvents.setHasFixedSize(false)
        binding.recyclerViewEvents.adapter = viewModel.eventAdapter
        viewModel.eventAdapter.setAdapterItemClick(object : AdapterItemClickListener<EventModel> {
            override fun onItemClick(t: EventModel, pos: Int) {
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
                viewModel.deleteEvent(
                    viewModel.eventAdapter.getItemAt(viewHolder.adapterPosition)
                )
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
                ItemTouchHelper.LEFT
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewEvents)
    }

    private fun initViewModel() {
        viewModel.getEvents()
    }


}