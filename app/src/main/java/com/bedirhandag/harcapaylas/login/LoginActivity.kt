package com.bedirhandag.harcapaylas.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bedirhandag.harcapaylas.R
import com.bedirhandag.harcapaylas.dashboard.DashboardActivity
import com.bedirhandag.harcapaylas.databinding.ActivityLoginBinding
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

        if (viewModel.guncelKullanici != null) {
            navigateToDashboard()
        }

    }

    private fun initObservers() {
        viewModel.apply {
            isActionLogin.observe(this@LoginActivity, {
                when (it) {
                    true -> updateLoginUI()
                    else -> updateRegisterUI()
                }
            })
        }
    }

    private fun clearUI() {
        viewbinding.apply {
            emailText.setText("")
            passwordText.setText("")
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
        when (viewModel.isActionLogin.value) {
            true -> loginOperation()
            else -> registerOperation()
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
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
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
                var userUid = FirebaseAuth.getInstance().currentUser!!.uid
                ref.child("users").child(userUid).child("email").setValue(emailText.text.toString())
                ref.child("users").child(userUid).child("password")
                    .setValue(passwordText.text.toString())
                ref.child("users").child(userUid).child("uid")
                    .setValue(userUid)


            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun showRegisterSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Kayıt Başarılı!")
            .setMessage("Başarılı bir şekilde kayıt oldunuz. Lütfen giriş yapınız!")
            .setCancelable(false)
            .setPositiveButton("Tamam") { _, _ ->
                viewModel.isActionLogin.value = true
            }
            .create()
            .show()
    }

    //Initialize metodlarımız
    private fun initFirebaseAuth() {
        viewModel.auth = FirebaseAuth.getInstance()
        viewModel.auth.currentUser?.let {
            viewModel.guncelKullanici = it
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