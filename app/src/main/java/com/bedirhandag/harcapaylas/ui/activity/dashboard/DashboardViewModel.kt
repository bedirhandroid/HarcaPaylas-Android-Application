package com.bedirhandag.harcapaylas.ui.activity.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bedirhandag.harcapaylas.model.GroupMemberDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class DashboardViewModel : ViewModel() {
    lateinit var auth: FirebaseAuth
    val joinedGroups = MutableLiveData<ArrayList<String>>().also { it.value = arrayListOf() }
    val username = MutableLiveData<String>()
}