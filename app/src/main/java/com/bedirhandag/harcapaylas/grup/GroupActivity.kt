package com.bedirhandag.harcapaylas.grup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bedirhandag.harcapaylas.databinding.ActivityGroupBinding
import com.bedirhandag.harcapaylas.util.showToast
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPKEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_group.*

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
        getGrupKey()
    }

    private fun getGrupKey() {
        intent.getStringExtra(KEY_GROUPKEY)?.let {
            grupKey.text = it
            this.showToast("$it Grubuna Hoşgeldin!")
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