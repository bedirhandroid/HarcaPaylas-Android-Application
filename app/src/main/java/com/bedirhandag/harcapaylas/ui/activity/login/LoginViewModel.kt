package com.bedirhandag.harcapaylas.ui.activity.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel: ViewModel() {

    lateinit var auth: FirebaseAuth
    val isActionLogin = MutableLiveData<Boolean>().also { it.value = true }
    lateinit var guncelKullanici: FirebaseUser


}