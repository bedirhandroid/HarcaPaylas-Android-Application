package com.bedirhandag.harcapaylas.grup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bedirhandag.harcapaylas.databinding.ActivityGroupBinding
import com.bedirhandag.harcapaylas.showToast
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPKEY
import kotlinx.android.synthetic.main.activity_group.*

class GroupActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityGroupBinding
    private lateinit var viewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
    }

    private fun setupViewBinding() {
        viewbinding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)
    }
}