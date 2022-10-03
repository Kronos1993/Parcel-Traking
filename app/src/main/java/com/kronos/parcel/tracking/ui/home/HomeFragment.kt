package com.kronos.parcel.traking.ui.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.kronos.zipcargo.domain.model.parcel.ParcelModel
import com.kronos.myparceltraking.R
import com.kronos.myparceltraking.databinding.FragmentHomeBinding
import com.kronos.parcel.traking.ui.home.state.HomeState
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val binding by fragmentBinding<FragmentHomeBinding>(R.layout.fragment_home)

    private val viewModel by activityViewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.run {
        viewModel = this@HomeFragment.viewModel
        lifecycleOwner = this@HomeFragment.viewLifecycleOwner
        initViewModel()
        observeViewModel()
        setListeners()
        initViews()
        root
    }

    private fun setListeners() {
        binding.fabAddNewParcel.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_dialog_add_parcel)
        }

        binding.homeRefreshLayout.setOnRefreshListener {
            viewModel.refreshParcels()
        }
    }

    private fun observeViewModel() {
        viewModel.parcelList.observe(this.viewLifecycleOwner, ::handleParcelList)
        viewModel.loading.observe(this.viewLifecycleOwner, ::handleLoading)
        viewModel.error.observe(this.viewLifecycleOwner, ::handleError)
        viewModel.state.observe(this.viewLifecycleOwner, ::handleHomeState)
    }

    private fun handleHomeState(homeState: HomeState) {
        when (homeState) {
            is HomeState.Loading -> {
                handleLoading(homeState.loading)
            }
            is HomeState.Refreshing -> {
                binding.homeRefreshLayout.isRefreshing = homeState.loading
                if(!homeState.loading)
                    viewModel.getParcels()
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
                SnackBarUtil.show(
                    binding.recyclerViewCurrentParcels,
                    hashtable["error"].orEmpty(),
                    com.kronos.resources.R.color.snack_bar_white,
                    com.kronos.resources.R.color.snack_bar_error_background
                )
            } else {
                SnackBarUtil.show(
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
            binding.recyclerViewCurrentParcels.visibility = View.GONE
            binding.textHome.visibility = View.VISIBLE
        } else {
            binding.recyclerViewCurrentParcels.visibility = View.VISIBLE
            binding.textHome.visibility = View.GONE
        }
        viewModel.parcelAdapter.notifyDataSetChanged()
    }

    private fun initViews() {
        binding.recyclerViewCurrentParcels.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCurrentParcels.setHasFixedSize(false)
        binding.recyclerViewCurrentParcels.adapter = viewModel.parcelAdapter
        viewModel.parcelAdapter.setAdapterItemClick(object : AdapterItemClickListener<ParcelModel> {
            override fun onItemClick(t: ParcelModel, pos: Int) {
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
                viewModel.toHistory(
                    viewModel.parcelAdapter.getItemAt(viewHolder.adapterPosition)
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(
            SwipeToDelete(
                ContextCompat.getDrawable(
                    requireContext(),
                    com.kronos.resources.R.drawable.ic_close
                )!!,
                ColorDrawable(Color.TRANSPARENT),
                itemTouchHelperCallback!!,
                ItemTouchHelper.LEFT
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewCurrentParcels)

    }

    private fun initViewModel() {
        viewModel.getParcels()
    }
}