package com.kronos.parcel.tracking.ui.user

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.parcel.tracking.MainState
import com.kronos.parcel.tracking.R
import com.kronos.parcel.tracking.databinding.FragmentUserBinding
import com.kronos.parcel.tracking.ui.history.state.HistoryState
import com.kronos.parcel.tracking.ui.home.CURRENT_PARCEL
import java.util.*

class UserFragment : Fragment() {

    private val binding by fragmentBinding<FragmentUserBinding>(R.layout.fragment_user)

    private val viewModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.run {
        viewModel = this@UserFragment.viewModel
        lifecycleOwner = this@UserFragment.viewLifecycleOwner
        observeViewModel()
        initViews()
        initViewModel()
        root
    }

    private fun observeViewModel() {
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
                    binding.root,
                    hashtable["error"].orEmpty(),
                    com.kronos.resources.R.color.snack_bar_white,
                    com.kronos.resources.R.color.snack_bar_error_background
                )
            } else {
                SnackBarUtil.show(
                    binding.root,
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

    private fun initViews() {

    }

    private fun initViewModel() {
    }

}