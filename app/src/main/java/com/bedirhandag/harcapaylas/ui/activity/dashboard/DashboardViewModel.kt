package com.bedirhandag.harcapaylas.ui.activity.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    val joinedGroups = MutableLiveData<ArrayList<String>>().also { it.value = arrayListOf() }
}