package com.bedirhandag.harcapaylas.ui.activity.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bedirhandag.harcapaylas.R
import com.bedirhandag.harcapaylas.ui.activity.dashboard.DashboardActivity
import com.bedirhandag.harcapaylas.databinding.ActivityLoginBinding
import com.bedirhandag.harcapaylas.util.showToast
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_EMAIL
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_PASSWORD
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_UID
import com.bedirhandag.harcapaylas.util.FirebaseKeys.KEY_USERS
import com.bedirhandag.harcapaylas.util.showAlert
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewBinding()
        setupViewModel()
        initFirebaseAuth()
        initObservers()
        initListeners()
        checkSession()
    }

    private fun checkSession() {
        viewModel.auth.currentUser?.let { navigateToDashboard() }
    }

    private fun initObservers() {
        viewModel.apply {
            isActionLogin.observe(this@LoginActivity, {
                when {
                    it -> updateLoginUI()
                    else -> updateRegisterUI()
                }
            })
        }
    }

    private fun clearUI() {
        viewbinding.apply {
            emailText.setText("mrt@gmail.com")
            passwordText.setText("mrtmrt")
        }
    }

    private fun updateLoginUI() {
        viewbinding.apply {
            clearUI()
            btnAction.text = getString(R.string.login)
            btnChangeAction.text = getString(R.string.second_register)
        }
    }

    private fun updateRegisterUI() {
        viewbinding.apply {
            clearUI()
            btnAction.text = getString(R.string.register)
            btnChangeAction.text = getString(R.string.second_login)
        }
    }

    private fun initListeners() {
        viewbinding.apply {
            btnAction.setOnClickListener { startOperation() }
            btnChangeAction.setOnClickListener { changeActionOperation() }
        }
    }

    private fun startOperation() {
        viewbinding.apply {
            if (!emailText.text.isNullOrEmpty() && !passwordText.text.isNullOrEmpty()) {
                when (viewModel.isActionLogin.value) {
                    true -> loginOperation()
                    else -> registerOperation()
                }
            } else showToast("Lütfen Alanları Doldurunuz!")
        }
    }


    private fun changeActionOperation() {
        viewModel.isActionLogin.value = viewModel.isActionLogin.value == false
    }

    private fun loginOperation() {
        viewModel.auth.signInWithEmailAndPassword(
            viewbinding.emailText.text.toString(),
            viewbinding.passwordText.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) navigateToDashboard()
        }.addOnFailureListener { exception ->
            exception.message?.let { showToast(it) }
        }
    }

    private fun navigateToDashboard() {
        Intent(this@LoginActivity, DashboardActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    var ref = FirebaseDatabase.getInstance().reference
    private fun registerOperation() {
        viewModel.auth.createUserWithEmailAndPassword(
            viewbinding.emailText.text.toString(),
            viewbinding.passwordText.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showRegisterSuccessDialog()
                val userUid = FirebaseAuth.getInstance().currentUser!!.uid
                ref.child(KEY_USERS).child(userUid).child(KEY_EMAIL)
                    .setValue(emailText.text.toString())
                ref.child(KEY_USERS).child(userUid).child(KEY_PASSWORD)
                    .setValue(passwordText.text.toString())
                ref.child(KEY_USERS).child(userUid).child(KEY_UID)
                    .setValue(userUid)


            }
        }.addOnFailureListener { exception ->
            exception.message?.let { showToast(it) }
        }
    }

    private fun showRegisterSuccessDialog() {
        showAlert(
            context = this,
            title = getString(R.string.register_success_title),
            msg = getString(R.string.register_success_message),
            iconResId = R.drawable.ic_tick
        ) {
            viewModel.isActionLogin.value = true
        }
    }

    //Initialize metodlarımız
    private fun initFirebaseAuth() {
        viewModel.apply {
            auth = FirebaseAuth.getInstance()
            auth.currentUser?.let {
                guncelKullanici = it
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    private fun setupViewBinding() {
        viewbinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)
    }
}