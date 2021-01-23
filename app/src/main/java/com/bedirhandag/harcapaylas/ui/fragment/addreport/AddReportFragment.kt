package com.bedirhandag.harcapaylas.ui.fragment.addreport

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.bedirhandag.harcapaylas.R
import com.bedirhandag.harcapaylas.databinding.ActivityLoginBinding
import com.bedirhandag.harcapaylas.databinding.FragmentAddReportBinding
import com.bedirhandag.harcapaylas.ui.activity.login.LoginViewModel

class AddReportFragment : Fragment() {

    private lateinit var viewModel: AddReportViewModel
    private lateinit var viewBinding: FragmentAddReportBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setupViewBinding()
        setupViewModel()
        initToolbar()
    }

    private fun initToolbar() {
        viewBinding.addReportAppBar.apply {
            pageTitle.text = "Harcama Bildir"
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(AddReportViewModel::class.java)
    }

    private fun View.setupViewBinding() {
        viewBinding = FragmentAddReportBinding.bind(this)
    }


    companion object {
        fun newInstance() = AddReportFragment()
    }
}