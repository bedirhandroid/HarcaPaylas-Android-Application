package com.bedirhandag.harcapaylas.ui.activity.group

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bedirhandag.harcapaylas.model.ReportModel
import com.google.firebase.database.DatabaseReference

class GroupViewModel : ViewModel() {
    val reportList = MutableLiveData<ArrayList<ReportModel>>().also { it.value = arrayListOf() }
    lateinit var groupKey: String
    lateinit var userUID: String
    lateinit var ref: DatabaseReference

    fun convertToReportModelList(data: ArrayList<HashMap<String,String>>) {
        val reportArrayList = arrayListOf<ReportModel>()
        data.forEach {
            ReportModel(
                it["userUID"],
                it["username"],
                it["description"],
                it["price"]
            ).also { _model ->
                reportArrayList.add(_model)
            }
        }
        reportList.value = reportArrayList
    }
}