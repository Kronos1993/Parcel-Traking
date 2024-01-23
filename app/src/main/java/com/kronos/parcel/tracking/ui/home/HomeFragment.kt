package com.kronos.parcel.tracking.ui.home

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.adapters.SwipeToDelete
import com.kronos.core.extensions.binding.fragmentBinding
import com.kronos.core.notification.INotifications
import com.kronos.core.notification.NotificationGroup
import com.kronos.core.notification.NotificationType
import com.kronos.core.util.LoadingDialog
import com.kronos.core.util.show
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.parcel.tracking.MainState
import com.kronos.parcel.tracking.R
import com.kronos.parcel.tracking.databinding.FragmentHomeBinding
import com.kronos.parcel.tracking.notification.ParcelTrackingNotifications
import com.kronos.parcel.tracking.ui.home.state.HomeState
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

const val CURRENT_PARCEL = "current_parcel"

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val binding by fragmentBinding<FragmentHomeBinding>(R.layout.fragment_home)

    private val viewModel by activityViewModels<HomeViewModel>()

    @Inject
    lateinit var notificationManager : INotifications

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.run {
        viewModel = this@HomeFragment.viewModel
        lifecycleOwner = this@HomeFragment.viewLifecycleOwner
        root
    }


    override fun onResume() {
        super.onResume()
        observeViewModel()
        setListeners()
        initViews()
        initViewModel()
    }

    private fun setListeners() {
        viewModel.observeTextChange()

        binding.homeRefreshLayout.setOnRefreshListener {
            viewModel.refreshParcels()
        }

        binding.fabAddNewParcel.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_dialog_add_parcel)
        }
    }

    private fun observeViewModel() {
        viewModel.parcelList.observe(this.viewLifecycleOwner, ::handleParcelList)
        viewModel.loading.observe(this.viewLifecycleOwner, ::handleLoading)
        viewModel.state.observe(this.viewLifecycleOwner, ::handleHomeState)
    }

    private fun handleHomeState(homeState: MainState) {
        when (homeState) {
            is HomeState.Loading -> {
                handleLoading(homeState.loading)
            }
            is HomeState.Refreshing -> {
                binding.homeRefreshLayout.isRefreshing = homeState.loading
            }
            is HomeState.Search -> {
                viewModel.getParcels()
            }
            is HomeState.Error -> {
                handleError(homeState.error)
            }
        }
    }

    private fun handleError(hashtable: Hashtable<String, String>) {
        if (hashtable["error"] != null) {
            if (hashtable["error"]!!.isNotEmpty()) {
                show(
                    binding.recyclerViewCurrentParcels,
                    hashtable["error"].orEmpty(),
                    com.kronos.resources.R.color.snack_bar_white,
                    com.kronos.resources.R.color.snack_bar_error_background
                )
            } else {
                show(
                    binding.recyclerViewCurrentParcels,
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
        viewModel.parcelAdapter.get()?.submitList(list)
        viewModel.parcelAdapter.get()?.notifyItemChanged(0,list.size)
    }

    private fun initViews() {
        binding.homeRefreshLayout.isRefreshing = false
        binding.recyclerViewCurrentParcels.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCurrentParcels.setHasFixedSize(false)
        if (viewModel.parcelAdapter.get()==null)
            viewModel.parcelAdapter = WeakReference(ParcelAdapter())
        binding.recyclerViewCurrentParcels.adapter = viewModel.parcelAdapter.get()
        viewModel.parcelAdapter.get()?.setUrlProvider(viewModel.urlProvider)
        viewModel.parcelAdapter.get()?.setAdapterItemClick(object :
            AdapterItemClickListener<ParcelModel> {
            override fun onItemClick(t: ParcelModel, pos: Int) {
                if (viewModel.state.value is HomeState.Idle) {
                    val bundle = Bundle()
                    bundle.putSerializable(CURRENT_PARCEL, t)
                    findNavController().navigate(R.id.navigation_parcel_details, bundle)
                }
            }

        })

        val itemTouchHelperCallback: ItemTouchHelper.Callback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (viewModel.state.value is HomeState.Idle) {
                    viewModel.toHistory(
                        viewModel.parcelAdapter.get()!!.getItemAt(viewHolder.adapterPosition)
                    )
                }else{
                    viewModel.parcelAdapter.get()?.notifyItemChanged(viewHolder.adapterPosition)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(
            SwipeToDelete(
                ContextCompat.getDrawable(
                    requireContext(),
                    com.kronos.resources.R.drawable.ic_archive
                )!!,
                ColorDrawable(
                    ContextCompat.getColor(
                        requireContext(),
                        com.kronos.resources.R.color.green
                    )
                ),
                ContextCompat.getDrawable(
                    requireContext(),
                    com.kronos.resources.R.drawable.ic_archive
                )!!,
                ColorDrawable(
                    ContextCompat.getColor(
                        requireContext(),
                        com.kronos.resources.R.color.green
                    )
                ),
                itemTouchHelperCallback,
                ItemTouchHelper.LEFT
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewCurrentParcels)
    }
    private fun initViewModel() {
        viewModel.getParcels()
    }

    override fun onDestroyView() {
        binding.unbind()
        super.onDestroyView()
    }

    override fun onPause() {
        binding.unbind()
        super.onPause()
    }
}