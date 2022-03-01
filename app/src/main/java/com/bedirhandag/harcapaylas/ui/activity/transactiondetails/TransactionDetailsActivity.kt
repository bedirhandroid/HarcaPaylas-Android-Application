package com.bedirhandag.harcapaylas.ui.activity.transactiondetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bedirhandag.harcapaylas.R
import com.bedirhandag.harcapaylas.databinding.ActivityTransactionDetailsBinding
import com.bedirhandag.harcapaylas.ui.adapter.TransactionDetailsAdapter
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPKEY
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPS
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUP_MEMBERS
import com.bedirhandag.harcapaylas.util.convertToTransactionDetailList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TransactionDetailsActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityTransactionDetailsBinding
    private lateinit var viewModel: TransactionDetailsViewModel
    private lateinit var transactionDetailsAdapter: TransactionDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        getArgParams()
        setupViewBinding()
        initFirebase()
        initObservers()
        initToolbar()
        getTransactionDetailList()
    }

    private fun initObservers() {
        viewModel.apply {
            transactionDetailList.observe(this@TransactionDetailsActivity) {
                it.sumBy { _item -> _item.price?.toInt() ?: 0 }.also { _calculatedTotalPrice ->
                    viewbinding.totalPrice.text = getString(
                        R.string.placeholder_total_price,
                        _calculatedTotalPrice.toString()
                    )
                }
                initAdapter()
            }
        }
    }

    private fun getTransactionDetailList() {
        viewModel.ref.child(KEY_GROUPS)
            .child(viewModel.groupKey)
            .child(KEY_GROUP_MEMBERS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModel.transactionDetailList.value =
                        (snapshot.value as ArrayList<HashMap<String, String>>?)?.convertToTransactionDetailList()
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun getArgParams() {
        intent.getStringExtra(KEY_GROUPKEY)?.let {
            viewModel.groupKey = it
        }
    }

    private fun initAdapter() {
        viewbinding.recyclerView.apply {
            viewModel.transactionDetailList.value?.let {
                transactionDetailsAdapter = TransactionDetailsAdapter(it)
                adapter = transactionDetailsAdapter
            }
        }
    }

    private fun initToolbar() {
        viewbinding.detailAppBar.apply {
            pageTitle.text = getString(R.string.placeholder_transaction_details)
        }
    }

    private fun initFirebase() {
        viewModel.ref = FirebaseDatabase.getInstance().reference
        FirebaseAuth.getInstance().currentUser?.uid?.let { viewModel.userUID = it }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(TransactionDetailsViewModel::class.java)
    }

    private fun setupViewBinding() {
        viewbinding = ActivityTransactionDetailsBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)
    }
}