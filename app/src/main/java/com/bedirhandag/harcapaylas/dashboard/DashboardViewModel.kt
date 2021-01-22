package com.bedirhandag.harcapaylas.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    val joinedGroups = MutableLiveData<ArrayList<String>>()
}