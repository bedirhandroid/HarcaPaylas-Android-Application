package com.bedirhandag.harcapaylas.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel: ViewModel() {

    lateinit var auth : FirebaseAuth
    val isActionLogin = MutableLiveData<Boolean>().also { it.value = true }

}