package com.kotlin.sacalabici.framework.views.activities.Session
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.sacalabici.R
import com.google.android.material.imageview.ShapeableImageView
import com.kotlin.sacalabici.framework.adapters.views.activities.Session.LoginActivity


class RecoverPasswordActivity:AppCompatActivity(){
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        val backButton=findViewById<ShapeableImageView>(R.id.BBack)
        backButton.setOnClickListener{
            val intent=Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
