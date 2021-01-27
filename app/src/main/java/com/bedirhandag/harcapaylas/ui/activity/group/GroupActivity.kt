package com.bedirhandag.harcapaylas.ui.activity.group

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bedirhandag.harcapaylas.R
import com.bedirhandag.harcapaylas.databinding.ActivityGroupBinding
import com.bedirhandag.harcapaylas.model.ReportModel
import com.bedirhandag.harcapaylas.ui.activity.transactiondetails.TransactionDetailsActivity
import com.bedirhandag.harcapaylas.ui.adapter.ReportsAdapter
import com.bedirhandag.harcapaylas.ui.fragment.addreport.AddReportFragment
import com.bedirhandag.harcapaylas.util.showToast
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPKEY
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPS
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_REPORTS
import com.bedirhandag.harcapaylas.util.AddReportCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityGroupBinding
    private lateinit var viewModel: GroupViewModel
    private lateinit var reportsAdapter: ReportsAdapter

    private val isCompletedListener = object: AddReportCompleteListener{
        override fun isCompleted(reportModel: ReportModel) {
            reportsAdapter.addItem(reportModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        getArgParams()
        setupViewBinding()
        initFirebase()
        initObservers()
        initToolbar()
        initListener()
        getReports()
    }

    private fun initObservers() {
        viewModel.apply {
            reportList.observe(this@GroupActivity, {
                initAdapter()
            })
        }
    }

    private fun initAdapter() {
        viewbinding.recyclerView.apply {
            viewModel.reportList.value?.let {
                reportsAdapter = ReportsAdapter(it, {}, {})
                adapter = reportsAdapter
            }
        }
    }

    private fun getReports() {
        viewModel.ref.child(KEY_GROUPS)
            .child(viewModel.groupKey)
            .child(KEY_REPORTS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.value?.let {
                        viewModel.convertToReportModelList((it as ArrayList<HashMap<String, String>>))
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun getArgParams() {
        intent.getStringExtra(KEY_GROUPKEY)?.let {
            viewModel.groupKey = it
        }
    }

    private fun initListener() {
        viewbinding.apply {
            addReport.setOnClickListener { showAddReportPopup() }
            btnTransactionDetails.setOnClickListener { transactionDetailsOperation() }
        }
    }

    private fun transactionDetailsOperation() {
        Intent(this, TransactionDetailsActivity::class.java).apply {
            putExtra(KEY_GROUPKEY, viewModel.groupKey)
        }.also { _intent ->
            startActivity(_intent)
        }
    }

    private fun showAddReportPopup() {
        supportFragmentManager.beginTransaction().apply {
            add(
                R.id.fragmentContainer,
                AddReportFragment.newInstance(viewModel.groupKey, isCompletedListener),
                AddReportFragment::class.java.simpleName
            ).commit()
        }
    }

    private fun initToolbar() {
        viewbinding.activityAppBar.apply {
            pageTitle.text = getString(R.string.placeholder_group, viewModel.groupKey)
            this@GroupActivity.showToast("${viewModel.groupKey} Grubuna Hoşgeldin!")
        }
    }

    private fun initFirebase() {
        viewModel.ref = FirebaseDatabase.getInstance().reference
        FirebaseAuth.getInstance().currentUser?.uid?.let { viewModel.userUID = it }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
    }

    private fun setupViewBinding() {
        viewbinding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)
    }
}