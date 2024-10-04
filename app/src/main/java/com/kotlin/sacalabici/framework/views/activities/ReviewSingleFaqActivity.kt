package com.kotlin.sacalabici.framework.views.activities


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.databinding.ActivitySingleFaqBinding
import com.kotlin.sacalabici.framework.viewmodel.FAQViewModel

class ReviewSingleFaqActivity:AppCompatActivity() {
    private lateinit var viewModel: FAQViewModel
    private lateinit var binding: ActivitySingleFaqBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(FAQViewModel::class.java)

        viewModel.faqObjectLiveData.observe(this){pregunta-> pregunta?.let{
            setUpFaqLabel(it)
            }
        }

        initializeBinding()
        viewModel.getPreguntaIndividual(3)

    }

    private fun setUpFaqLabel(pregunta: FAQBase){
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