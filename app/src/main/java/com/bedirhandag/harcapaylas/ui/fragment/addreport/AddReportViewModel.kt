package com.bedirhandag.harcapaylas.ui.fragment.addreport

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bedirhandag.harcapaylas.model.ReportModel
import com.google.firebase.database.DatabaseReference

class AddReportViewModel : ViewModel() {
    val reportList = MutableLiveData<ArrayList<ReportModel>>().also { it.value = arrayListOf() }
    lateinit var groupKey: String
    lateinit var ref: DatabaseReference
    lateinit var userUID: String
    lateinit var username: String

}