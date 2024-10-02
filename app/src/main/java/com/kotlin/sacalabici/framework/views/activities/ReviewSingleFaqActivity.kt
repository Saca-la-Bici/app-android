package com.kotlin.sacalabici.framework.views.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.framework.viewmodel.FaqViewModel

class ReviewSingleFaqActivity:AppCompatActivity() {
    private lateinit var viewModel: FaqViewModel

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_faq)

        val IdPregunta = intent.getIntExtra("IdPregunta", -1)

        viewModel = ViewModelProvider(this).get(FaqViewModel::class.java)

        IdPregunta.let {
            viewModel.getPreguntaIndividual(it)
        }

        viewModel.preguntaFrecuente.observe(this,Observer{pregunta ->
            findViewById<TextView>(R.id.preguntaTextView).text = pregunta.Pregunta
            findViewById<TextView>(R.id.respuestaTextView).text = pregunta.Respuesta
        })
    }

}