package com.bedirhandag.harcapaylas.ui.activity.grup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bedirhandag.harcapaylas.R
import com.bedirhandag.harcapaylas.databinding.ActivityGroupBinding
import com.bedirhandag.harcapaylas.ui.fragment.addreport.AddReportFragment
import com.bedirhandag.harcapaylas.util.showToast
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPKEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_group.view.*

class GroupActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityGroupBinding
    private lateinit var viewModel: GroupViewModel
    lateinit var userUID: String
    lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFirebase()
        setupViewBinding()
        setupViewModel()
        initToolbar()
        initListener()
    }

    private fun initListener() {
        viewbinding.apply {
            addReport.setOnClickListener { showAddReportPopup() }
        }
    }

    private fun showAddReportPopup() {
        val manager = supportFragmentManager.beginTransaction()

        manager.add(
            R.id.fragmentContainer,
            AddReportFragment.newInstance(),
            AddReportFragment::class.java.simpleName
        )
        manager.addToBackStack(null)
        manager.commit()

    }

    private fun initToolbar() {
        viewbinding.activityAppBar.apply {
            intent.getStringExtra(KEY_GROUPKEY)?.let {
                pageTitle.text = it
                this@GroupActivity.showToast("$it Grubuna Hoşgeldin!")
            }
        }


    }

    private fun initFirebase() {
        ref = FirebaseDatabase.getInstance().reference
        FirebaseAuth.getInstance().currentUser?.uid?.let { userUID = it }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
    }

    private fun setupViewBinding() {
        viewbinding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)
    }
}