package com.bedirhandag.harcapaylas.ui.activity.grup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bedirhandag.harcapaylas.R
import com.bedirhandag.harcapaylas.databinding.ActivityGroupBinding
import com.bedirhandag.harcapaylas.model.ReportModel
import com.bedirhandag.harcapaylas.ui.adapter.GroupsAdapter
import com.bedirhandag.harcapaylas.ui.fragment.addreport.AddReportFragment
import com.bedirhandag.harcapaylas.util.showToast
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPKEY
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPS
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_REPORTS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityGroupBinding
    private lateinit var viewModel: GroupViewModel
    private lateinit var groupsAdapter: GroupsAdapter

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
        /*viewbinding.recyclerView.apply {
            viewModel.reportList.value?.let {
                groupsAdapter = GroupsAdapter(it) { }
                adapter = groupsAdapter
                applyDivider()
            }
        }*/
    }

    private fun getReports() {
        viewModel.ref.child(KEY_GROUPS)
            .child(viewModel.groupKey)
            .child(KEY_REPORTS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.value?.let {
                        viewModel.reportList.value = (it as ArrayList<HashMap<String, ReportModel>>)
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
        }
    }

    private fun showAddReportPopup() {
        supportFragmentManager.beginTransaction().apply {
            add(
                R.id.fragmentContainer,
                AddReportFragment.newInstance(viewModel.groupKey),
                AddReportFragment::class.java.simpleName
            ).commit()
        }
    }

    private fun initToolbar() {
        viewbinding.activityAppBar.apply {
            pageTitle.text = viewModel.groupKey
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