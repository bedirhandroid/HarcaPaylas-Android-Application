package com.bedirhandag.harcapaylas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class KayitActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kayit)

        auth = FirebaseAuth.getInstance()

    }
    fun kayitOl(view: View){
        auth.createUserWithEmailAndPassword(emailText.text.toString(),passwordText.text.toString()).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val intent = Intent(this,GirisActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }
}