package com.bedirhandag.harcapaylas.ui.activity.dashboard

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.bedirhandag.harcapaylas.ui.adapter.GroupsAdapter
import com.bedirhandag.harcapaylas.ui.activity.grup.GroupActivity
import com.bedirhandag.harcapaylas.databinding.ActivityDashboardBinding
import com.bedirhandag.harcapaylas.ui.activity.login.LoginActivity
import com.bedirhandag.harcapaylas.util.showToast
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPKEY
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPS
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUP_MEMBERS
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_USERS
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_WHICH_GROUP
import com.bedirhandag.harcapaylas.util.applyDivider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var groupsAdapter: GroupsAdapter
    lateinit var userUID: String
    lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        initFirebase()
        setupViewBinding()
        initToolbar()
        initObservers()
        initListeners()
        getJoinedGroups()
    }

    private fun initToolbar() {
        viewbinding.dashboardAppBar.apply {
            pageTitle.text = "HarcaPaylaş"
            logout.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                navigateToLoginActivity()
            }
            }
        }

    private fun initObservers() {
        viewModel.apply {
            joinedGroups.observe(this@DashboardActivity, {
                initAdapter()
            })
        }
    }

    private fun initAdapter() {
        viewbinding.recyclerView.apply {
            viewModel.joinedGroups.value?.let {
                groupsAdapter = GroupsAdapter(
                    it,
                    { _clickedItem ->
                        navigateToGroupActivity(_clickedItem)
                    },
                    { _longClickedItem ->
                        //Todo : Bedo uzun basıldığında itemi firebase'den silelim
                    }
                )
                adapter = groupsAdapter
                applyDivider()
            }
        }
    }

    private fun getJoinedGroups() {
        ref.child(KEY_USERS)
            .child(userUID)
            .child(KEY_WHICH_GROUP)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.value?.let {
                        viewModel.joinedGroups.value = (it as ArrayList<String>)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun initFirebase() {
        ref = FirebaseDatabase.getInstance().reference
        viewModel.auth = FirebaseAuth.getInstance()
        FirebaseAuth.getInstance().currentUser?.uid?.let { userUID = it }
    }

    private fun initListeners() {
        viewbinding.apply {
            btnGrupKur.setOnClickListener { createGroupOperation() }
            btnGrubaKatil.setOnClickListener { joinGroupOperation() }
        }
    }

    private fun joinGroupOperation() {
        viewModel.joinedGroups.value?.find { it == viewbinding.key.text.toString() }?.let {
            navigateToGroupActivity(it)
        } ?: kotlin.run {
            showToast("Grup bulunamadı!")
        }
    }

    private fun navigateToGroupActivity(groupKey: String) {
        Intent(this@DashboardActivity, GroupActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_NO_ANIMATION)
            putExtra(KEY_GROUPKEY, groupKey)
        }.also { _intent ->
            startActivity(_intent)
        }
    }

    private fun navigateToLoginActivity() {
        Intent(this@DashboardActivity, LoginActivity::class.java).also { _intent ->
            startActivity(_intent)
        }
    }

    private fun createGroupOperation() {
        val grupKey = viewbinding.key.text.toString()

        ref.child(KEY_GROUPS).child(grupKey).child(KEY_GROUPKEY)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
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

                        updateFirebaseWithJoinedGroup(grupKey)
                        groupsAdapter.addItem(grupKey)
                        Handler(Looper.getMainLooper()).postDelayed({
                            Intent(this@DashboardActivity, GroupActivity::class.java).apply {
                                addFlags(FLAG_ACTIVITY_NO_ANIMATION)
                                putExtra(KEY_GROUPKEY, grupKey)
                            }.also { _intent ->
                                viewbinding.key.setText(String())
                                startActivity(_intent)
                            }
                        }, 500)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun updateFirebaseWithJoinedGroup(groupKey: String) {
        arrayListOf<String>().apply {
            viewModel.joinedGroups.value?.let {
                it.forEach {  _value ->
                    add(_value)
                }
            }
            add(groupKey)
        }.also {
            ref.child(KEY_USERS)
                .child(userUID)
                .child(KEY_WHICH_GROUP)
                .setValue(it)
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