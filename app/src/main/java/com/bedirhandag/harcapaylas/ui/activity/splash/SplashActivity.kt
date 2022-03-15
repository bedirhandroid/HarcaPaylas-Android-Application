package com.bedirhandag.harcapaylas.ui.activity.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bedirhandag.harcapaylas.databinding.ActivitySplashBinding
import com.bedirhandag.harcapaylas.ui.activity.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        showSplash()

    }

    private fun showSplash(){
        val background = object : Thread() {
            override fun run() {
                try {
                    sleep(1500)
                    navigateLoginActivity()
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        background.start()
    }

    private fun navigateLoginActivity(){
        val intent = Intent(baseContext, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun initViewBinding(){
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}