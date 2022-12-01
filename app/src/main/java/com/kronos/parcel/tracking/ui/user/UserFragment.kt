package com.kronos.parcel.tracking.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kronos.core.extensions.binding.fragmentBinding
import com.kronos.core.util.LoadingDialog
import com.kronos.core.util.copyText
import com.kronos.core.util.show
import com.kronos.parcel.tracking.MainState
import com.kronos.parcel.tracking.R
import com.kronos.parcel.tracking.databinding.FragmentUserBinding
import com.kronos.parcel.tracking.ui.user.state.UserState
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class UserFragment : Fragment() {

    private val binding by fragmentBinding<FragmentUserBinding>(R.layout.fragment_user)

    private val viewModel by viewModels<UserViewModel>()

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
        viewModel.state.observe(this.viewLifecycleOwner, ::handleState)
    }

    private fun handleState(state: MainState) {
        when (state) {
            is UserState.Loading -> {
                handleLoading(state.loading)
            }
            is UserState.Error -> {
                handleError(state.error)
            }
            is UserState.UserLogged -> {
                viewModel.user.value.let {
                    viewModel.userLogged.value = View.VISIBLE
                    viewModel.userNotLogged.value = View.GONE
                }
            }
            is UserState.UserNotLogged -> {
                viewModel.userLogged.value = View.GONE
                viewModel.userNotLogged.value = View.VISIBLE
            }
        }
    }

    private fun handleError(hashtable: Hashtable<String, String>) {
        if (hashtable["error"] != null) {
            if (hashtable["error"]!!.isNotEmpty()) {
                show(
                    binding.root,
                    hashtable["error"].orEmpty(),
                    com.kronos.resources.R.color.snack_bar_white,
                    com.kronos.resources.R.color.snack_bar_error_background
                )
            } else {
                show(
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
        binding.userLogged.viewModel = viewModel
        binding.userLogged.lifecycleOwner = this@UserFragment.viewLifecycleOwner
        binding.userNotLogged.viewModel = viewModel
        binding.userNotLogged.lifecycleOwner = this@UserFragment.viewLifecycleOwner
        binding.userNotLogged.buttonLogin.setOnClickListener {
            viewModel.getBoxUser("", "")
        }

        binding.userLogged.imageViewLogOut.setOnClickListener {
            viewModel.deleteUser()
        }

        binding.userLogged.imageCopy.setOnClickListener {
            copyText(requireContext(), binding.userLogged.textViewUserAddress.text.toString())
        }
    }

    private fun initViewModel() {
        viewModel.getUserLocal()
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