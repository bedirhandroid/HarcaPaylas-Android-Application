package com.bedirhandag.harcapaylas.ui.activity.dashboard

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.bedirhandag.harcapaylas.ui.adapter.GroupsAdapter
import com.bedirhandag.harcapaylas.ui.activity.group.GroupActivity
import com.bedirhandag.harcapaylas.databinding.ActivityDashboardBinding
import com.bedirhandag.harcapaylas.ui.activity.login.LoginActivity
import com.bedirhandag.harcapaylas.util.*
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPKEY
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUPS
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_GROUP_MEMBERS
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_USERNAME
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_USERS
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_WHICH_GROUP
import com.google.firebase.auth.FirebaseAuth
import com.bedirhandag.harcapaylas.R
import com.bedirhandag.harcapaylas.model.GroupMemberDetail
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
        getUsername()
        initObservers()
        initListeners()
        getJoinedGroups()
    }

    private fun getUsername() {
        viewbinding.holderUsername.apply {
            ref.child(KEY_USERS)
                .child(userUID)
                .child(KEY_USERNAME)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        viewModel.username.value = (snapshot.value as String?).toString()
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
        }
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
            username.observe(this@DashboardActivity, {
                it?.let {
                    viewbinding.holderUsername.visible()
                    viewbinding.holderUsername.text = getString(R.string.welcome_username, it)
                } ?: kotlin.run { viewbinding.holderUsername.gone() }
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
                        showConfirmAlert(
                            this@DashboardActivity,
                            title = getString(R.string.exit_group_title),
                            msg = getString(R.string.exit_group_message, _longClickedItem),
                            iconResId = R.drawable.ic_warning
                        ) { _isOkButton ->
                            if (_isOkButton) {
                                updateFirebaseWithRemovedGroup(_longClickedItem)
                            }
                        }
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
            joinExistingGroup()
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

    private fun String.checkGroupKeyIsEmpty(block: (String) -> Unit) = when {
            isNotBlank() -> block(this)
            else -> showToast(getString(R.string.enter_valid_groupname))
        }

    private fun joinExistingGroup() {
        viewbinding.key.text.toString().checkGroupKeyIsEmpty { _groupKey ->
            ref.child(KEY_GROUPS).child(_groupKey).child(KEY_GROUPKEY)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value == null || snapshot.value.toString().isEmpty()) {
                            showToast("Grup bulunamadı!")
                        } else {
                            updateGroupMembers(_groupKey)
                            updateFirebaseWithJoinedGroup(_groupKey)
                            groupsAdapter.addItem(_groupKey)
                            Handler(Looper.getMainLooper()).postDelayed({
                                Intent(this@DashboardActivity, GroupActivity::class.java).apply {
                                    addFlags(FLAG_ACTIVITY_NO_ANIMATION)
                                    putExtra(KEY_GROUPKEY, _groupKey)
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
    }

    private fun createGroupOperation() {
        viewbinding.key.text.toString().checkGroupKeyIsEmpty { _groupKey ->
            ref.child(KEY_GROUPS).child(_groupKey).child(KEY_GROUPKEY)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value != null && snapshot.value.toString().isNotEmpty()) {
                            showToast("Bu Grup İsmi Kullanılıyor! Lütfen Farklı Bir Giriş Deneyiniz!")
                        } else {
                            ref.child(KEY_GROUPS)
                                .child(_groupKey)
                                .child(KEY_GROUPKEY)
                                .setValue(_groupKey)

                            updateGroupMembers(_groupKey)

                            updateFirebaseWithJoinedGroup(_groupKey)
                            groupsAdapter.addItem(_groupKey)
                            Handler(Looper.getMainLooper()).postDelayed({
                                Intent(this@DashboardActivity, GroupActivity::class.java).apply {
                                    addFlags(FLAG_ACTIVITY_NO_ANIMATION)
                                    putExtra(KEY_GROUPKEY, _groupKey)
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
    }

    private fun updateGroupMembers(groupKey: String) {
        ref.child(KEY_GROUPS)
            .child(groupKey)
            .child(KEY_GROUP_MEMBERS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListOf<GroupMemberDetail>().apply {
                        (snapshot.value as ArrayList<HashMap<String, String>>?)?.convertToTransactionDetailList()?.forEach {
                            add(it)
                        }
                        add(
                            GroupMemberDetail(
                                userId = userUID,
                                username = viewModel.username.value.toString(),
                                price = "0"
                            )
                        )
                    }.also { _newList ->
                        ref.child(KEY_GROUPS)
                            .child(groupKey)
                            .child(KEY_GROUP_MEMBERS)
                            .setValue(_newList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun updateFirebaseWithJoinedGroup(groupKey: String) {
        arrayListOf<String>().apply {
            viewModel.joinedGroups.value?.let {
                it.forEach { _value ->
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

    private fun updateFirebaseWithRemovedGroup(groupKey: String) {
        arrayListOf<String>().apply {
            var tempRemoveItem = String()
            viewModel.joinedGroups.value?.forEach { _value ->
                if(_value != groupKey) {
                    add(_value)
                } else {
                    tempRemoveItem = _value
                }
            }
            viewModel.joinedGroups.value?.remove(tempRemoveItem)
            groupsAdapter.removeItem(tempRemoveItem)
        }.also {
            ref.child(KEY_USERS)
                .child(userUID)
                .child(KEY_WHICH_GROUP)
                .setValue(it)
        }

        removeMeFromGroup(groupKey)
    }

    private fun removeMeFromGroup(groupKey: String) {
        ref.child(KEY_GROUPS)
            .child(groupKey)
            .child(KEY_GROUP_MEMBERS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListOf<GroupMemberDetail>().apply {
                        (snapshot.value as ArrayList<HashMap<String, String>>?)?.convertToTransactionDetailList()?.forEach {
                            if(it.userId != userUID) {
                                add(it)
                            }
                        }
                    }.also { _newList ->
                        ref.child(KEY_GROUPS)
                            .child(groupKey)
                            .child(KEY_GROUP_MEMBERS)
                            .setValue(_newList)
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