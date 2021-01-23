package com.bedirhandag.harcapaylas.ui.activity.grup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bedirhandag.harcapaylas.model.ReportModel
import com.google.firebase.database.DatabaseReference

class GroupViewModel : ViewModel() {
    val reportList = MutableLiveData<ArrayList<HashMap<String, ReportModel>>>().also { it.value = arrayListOf() }
    lateinit var groupKey: String
    lateinit var userUID: String
    lateinit var ref: DatabaseReference
}