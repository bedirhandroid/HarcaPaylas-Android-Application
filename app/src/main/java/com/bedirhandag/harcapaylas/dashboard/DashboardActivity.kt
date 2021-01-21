package com.bedirhandag.harcapaylas.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bedirhandag.harcapaylas.grup.GroupActivity
import com.bedirhandag.harcapaylas.R
import com.bedirhandag.harcapaylas.databinding.ActivityDashboardBinding
import com.bedirhandag.harcapaylas.showToast
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPKEY
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPS
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUP_MEMBERS
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_USERS
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_WHICH_GROUP
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    lateinit var userUID: String
    lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        initFirebase()
        setupViewBinding()
        setupViewModel()
        initListeners()
        checkHasGroup()
    }

    private fun checkHasGroup() {
        ref.child(KEY_USERS)
            .child(userUID)
            .child(KEY_WHICH_GROUP)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.value?.let {
                        Intent(this@DashboardActivity, GroupActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            putExtra(KEY_GROUPKEY, it.toString())
                        }.also { _intent ->
                            startActivity(_intent)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun initFirebase() {
        ref = FirebaseDatabase.getInstance().reference
        FirebaseAuth.getInstance().currentUser?.uid?.let { userUID = it }
    }

    private fun initListeners() {
        viewbinding.apply {
            btnGrupKur.setOnClickListener { createGroupOperation() }
            btnGirisGrup.setOnClickListener { joinGroupOperation() }
        }
    }

    private fun joinGroupOperation() {
        val girisGrupKey = viewbinding.etGrupGirisKey.text.toString()

        ref.child(KEY_GROUPS).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                when {
                    p0.hasChildren() -> {
                        p0.children.find { it.key.toString() == girisGrupKey }?.let {

                            ref.child(KEY_GROUPS)
                                .child(girisGrupKey)
                                .child(KEY_GROUP_MEMBERS)
                                .child(userUID)
                                .setValue(userUID)

                            ref.child(KEY_USERS)
                                .child(userUID)
                                .child(KEY_WHICH_GROUP)
                                .setValue(girisGrupKey)

                            Intent(this@DashboardActivity, GroupActivity::class.java).apply {
                                putExtra(KEY_GROUPKEY, girisGrupKey)
                            }.also { _intent ->
                                startActivity(_intent)
                            }

                            ref.removeEventListener(this)
                        } ?: kotlin.run {
                            showToast("Grup ID Hatası!")
                        }
                    }
                    else -> {
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun createGroupOperation() {
        val grupKey = viewbinding.key.text.toString()

        ref.child(KEY_GROUPS).child(grupKey).child(KEY_GROUPKEY)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //snapshot.children.any { it.value== grupKey}.let {


                    if (snapshot.value != null && snapshot.value.toString().isNotEmpty()) {
                        showToast("Bu Grup İsmi Kullanılıyor! Lütfen Farklı Bir Giriş Deneyiniz!")

                    } else {
                        ref.child(KEY_GROUPS)
                            .child(grupKey)
                            .child(KEY_GROUPKEY)
                            .setValue(grupKey)

                        ref.child(KEY_GROUPS)
                            .child(grupKey)
                            .child(KEY_GROUP_MEMBERS)
                            .child(userUID)
                            .setValue(userUID)

                        Intent(this@DashboardActivity, GroupActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            putExtra(KEY_GROUPKEY, grupKey.toString())
                        }.also { _intent ->
                            startActivity(_intent)
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun setupViewBinding() {
        viewbinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
    }
}