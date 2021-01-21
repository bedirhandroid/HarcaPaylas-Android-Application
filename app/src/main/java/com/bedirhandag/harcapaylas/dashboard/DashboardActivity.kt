package com.bedirhandag.harcapaylas.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bedirhandag.harcapaylas.GrupActivity
import com.bedirhandag.harcapaylas.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_dashboard.*


class DashboardActivity : AppCompatActivity() {

    var userUID = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        var ref = FirebaseDatabase.getInstance().reference



        btnGrupKur.setOnClickListener {

            var grupKey = key.text.toString()

            ref.child("gruplar")
                .child(grupKey)
                .child("grupKey")
                .setValue(grupKey)

            ref.child("gruplar")
                .child(grupKey)
                .child("grupUyeleri")
                .child(userUID)
                .setValue(userUID)

            Intent(this@DashboardActivity, GrupActivity::class.java).apply {
                putExtra("grupKey", grupKey)
            }.also {
                startActivity(intent)
            }


        }

        btnGirisGrup.setOnClickListener {
            var girisGrupKey = etGrupGirisKey.text.toString()

            ref.child("gruplar").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {


                    if (p0.hasChildren()) {
                        for (grup in p0.children) {
                            Log.e("grup", grup.key.toString())
                            if (grup.key.toString() == girisGrupKey) {
                                Toast.makeText(
                                    this@DashboardActivity,
                                    "${girisGrupKey} Grubuna Hoşgeldin!",
                                    Toast.LENGTH_LONG
                                ).show()
                                ref.child("gruplar").child(girisGrupKey).child("grupUyeleri")
                                    .child(userUID).setValue(userUID)
                                ref.child("users").child(userUID).child("hangiGrubaUye")
                                    .setValue(girisGrupKey)

                                var intent =
                                    Intent(this@DashboardActivity, GrupActivity::class.java)
                                intent.putExtra("grupKey", girisGrupKey)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@DashboardActivity,
                                    "Grup ID Hatası!",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
        }

        ref.child("users").child(userUID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        (p0.child("hangiGrubaUye").value as String?)?.let {
                            Intent(this@DashboardActivity, GrupActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                putExtra("grupKey", it)
                            }.also {
                                startActivity(intent)
                            }
                        } ?: kotlin.run {
                            Toast.makeText(this@DashboardActivity, "Lütfen Bir Gruba Üye Olunuz!", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }
}