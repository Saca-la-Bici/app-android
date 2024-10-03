package com.kotlin.sacalabici.framework.views.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente
import com.kotlin.sacalabici.databinding.ActivityProfileEditBinding
import com.kotlin.sacalabici.databinding.ActivitySingleFaqBinding
import com.kotlin.sacalabici.domain.preguntasFrecuentes.ReviewFaqRequirement
import com.kotlin.sacalabici.framework.viewmodel.FaqViewModel

class ReviewSingleFaqActivity:AppCompatActivity() {
    private lateinit var viewModel: FaqViewModel
    private lateinit var binding: ActivitySingleFaqBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(FaqViewModel::class.java)

        viewModel.preguntaObjectLiveData.observe(this){pregunta-> pregunta?.let{
            setUpFaqLabel(it)
            }
        }

        initializeBinding()
        viewModel.getPreguntaIndividual(3)

    }

    private fun setUpFaqLabel(pregunta: PreguntaFrecuente){
        val questionLabel = binding.preguntaTextView
        val answerLabel = binding.respuestaTextView

        questionLabel.text = pregunta.Pregunta
        answerLabel.text = pregunta.Respuesta
    }
/*
    private fun setUpQuestionLabel(){
        val questionLabel = binding.preguntaTextView
        questionLabel.setText("caca?")
    }

    private fun setUpAnswerLabel(){
        val answerLabel = binding.respuestaTextView
        answerLabel.text = "sí"
    }
*/
    private fun initializeBinding(){
        binding = ActivitySingleFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}