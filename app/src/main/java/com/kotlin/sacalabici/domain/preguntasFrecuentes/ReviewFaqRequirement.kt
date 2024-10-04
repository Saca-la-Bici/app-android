package com.kotlin.sacalabici.domain.preguntasFrecuentes

import android.util.Log
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.data.repositories.PreguntasFrecuentesRepository

class ReviewFaqRequirement {
    private val repository = PreguntasFrecuentesRepository()

    suspend operator fun invoke (id:Int): FAQBase {
        Log.d("ayuda","Entra Requirement")
        return repository.consultarPreguntaFrecuenteInd(id)
    }
}