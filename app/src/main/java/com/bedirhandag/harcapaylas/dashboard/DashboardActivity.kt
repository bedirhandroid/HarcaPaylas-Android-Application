package com.bedirhandag.harcapaylas.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.bedirhandag.harcapaylas.GrupActivity
import com.bedirhandag.harcapaylas.R
import com.bedirhandag.harcapaylas.databinding.ActivityDashboardBinding
import com.bedirhandag.harcapaylas.showToast
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
        ref.child("users")
            .child(userUID)
            .child("hangiGrubaUye")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.value?.let {
                        Intent(this@DashboardActivity, GrupActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            putExtra("grupKey", it.toString())
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
            btnGirisGrup.setOnClickListener { joinGroupOperation()}
        }
    }

    private fun joinGroupOperation() {
        val girisGrupKey = viewbinding.etGrupGirisKey.text.toString()

        ref.child("gruplar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                when {
                    p0.hasChildren() -> {
                        p0.children.find { it.key.toString() == girisGrupKey }?.let {

                            ref.child("gruplar")
                                .child(girisGrupKey)
                                .child("grupUyeleri")
                                .child(userUID)
                                .setValue(userUID)

                            ref.child("users")
                                .child(userUID)
                                .child("hangiGrubaUye")
                                .setValue(girisGrupKey)

                            Intent(this@DashboardActivity, GrupActivity::class.java).apply {
                                putExtra("grupKey", girisGrupKey)
                            }.also { _intent ->
                                startActivity(_intent)
                            }

                            ref.removeEventListener(this)
                        } ?: kotlin.run {
                            showToast("Grup ID Hatası!")
                        }
                    }
                    else -> {}
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun createGroupOperation() {
        val grupKey = viewbinding.key.text.toString()

        ref.child("gruplar")
            .child(grupKey)
            .child("grupKey")
            .setValue(grupKey)

        ref.child("gruplar")
            .child(grupKey)
            .child("grupUyeleri")
            .child(userUID)
            .setValue(userUID)

        Intent(this@DashboardActivity, GrupActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            putExtra("grupKey", grupKey.toString())
        }.also { _intent ->
            startActivity(_intent)
        }
    }

    private fun setupViewBinding() {
        viewbinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
    }
}