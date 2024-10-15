package com.kotlin.sacalabici.framework.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQBase
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.FAQObjectBase
import com.kotlin.sacalabici.domain.preguntasFrecuentes.DeleteFaqRequirement
import com.kotlin.sacalabici.domain.preguntasFrecuentes.FAQListRequirement
import com.kotlin.sacalabici.domain.preguntasFrecuentes.PostFAQRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FAQViewModel : ViewModel() {
    val faqObjectLiveData = MutableLiveData<List<FAQBase>>()
    private val faqListRequirement = FAQListRequirement()
    val selectedFAQ = MutableLiveData<FAQBase?>()
    private val postFAQRequirement = PostFAQRequirement()
    val permissionsLiveData = MutableLiveData<List<String>>()
    // private val postFAQRequirement = PostFAQRequirement()

    val errorMessage = MutableLiveData<String?>()

    fun getFAQList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: FAQObjectBase? = faqListRequirement()
                val faqresult = result!!.faqs

                if (faqresult.isEmpty()) {
                    errorMessage.postValue("No se encontraron preguntas frecuentes")
                } else {
                    faqObjectLiveData.postValue(faqresult)
                    errorMessage.postValue(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage.postValue("Error al consultar los datos")
                faqObjectLiveData.postValue(emptyList())
            }
        }
    }

    fun selectFAQ(faq: FAQBase) {
        selectedFAQ.postValue(faq)
    }

    fun deleteFAQ(faq: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                DeleteFaqRequirement()(faq)
                getFAQList()
            } catch (e: Exception) {
                throw e
            }
        }
    }
    fun postFAQ(
        pregunta: String,
        respuesta: String,
        tema: String,
        imagen: String?,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Obtener el número de preguntas existentes desde el backend
                val result = faqListRequirement()
                val size = result?.faqs?.size ?: 0

                // Generar el siguiente IdPregunta basado en el tamaño
                val idPregunta = size + 1

                // Crear el objeto FAQBase para el POST
                val nuevaFAQ =
                    FAQBase(
                        id = "",
                        IdPregunta = idPregunta,
                        Pregunta = pregunta,
                        Respuesta = respuesta,
                        Tema = tema,
                        Imagen = imagen,
                    )

                // Llamada para registrar la FAQ
                postFAQRequirement(nuevaFAQ)

                // Notificar que la operación fue exitosa o manejar el resultado si es necesario
                errorMessage.postValue(null)
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage.postValue("Error al registrar la pregunta frecuente")
            }
        }
    }

}


