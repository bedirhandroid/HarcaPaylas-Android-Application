package com.bedirhandag.harcapaylas.ui.activity.transactiondetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bedirhandag.harcapaylas.model.GroupMemberDetail
import com.google.firebase.database.DatabaseReference

class TransactionDetailsViewModel: ViewModel() {
    lateinit var groupKey: String
    lateinit var userUID: String
    lateinit var ref: DatabaseReference
    val transactionDetailList = MutableLiveData<ArrayList<GroupMemberDetail>>().also { it.value = arrayListOf() }
}