package com.kotlin.sacalabici.data.network.preguntasFrecuentes
import com.kotlin.sacalabici.data.models.preguntasFrecuentes.PreguntaFrecuente

// Borrar después -> Provider de preguntas frecuentes provisional
class PreguntaFrecuenteProvider {
    companion object {
        val preguntaFrecuenteList = listOf<PreguntaFrecuente>(
            PreguntaFrecuente(
                1,
                "¿Qué pasa si no tengo bici?",
                "Te podemos rentar",
                "Materiales",
                "https://www.google.com/imagen"
            ),
            PreguntaFrecuente(
                2,
                "¿Cuáles son las rutas más seguras?",
                "Las rutas están previamente evaluadas y contamos con guías expertos.",
                "Seguridad",
                "https://www.google.com/imagen_segura"
            ),
            PreguntaFrecuente(
                3,
                "¿Qué nivel de experiencia necesito para unirme?",
                "Tenemos actividades para principiantes y avanzados.",
                "Niveles",
                "https://www.google.com/imagen_experiencia"
            ),
            PreguntaFrecuente(
                4,
                "¿Qué equipo debo llevar para las rodadas?",
                "Es obligatorio llevar casco. Te sugerimos traer agua y bloqueador solar.",
                "Equipamiento",
                "https://www.google.com/imagen_equipamiento"
            ),
            PreguntaFrecuente(
                5,
                "¿Qué hago si hay mal clima el día de la rodada?",
                "Sigue la rodada, no se cancela.",
                "Condiciones climáticas",
                "https://www.google.com/imagen_clima"
            ),
            PreguntaFrecuente(
                6,
                "¿Cómo puedo inscribirme a un taller?",
                "Puedes inscribirte a través de nuestra app o página web.",
                "Talleres",
                "https://www.google.com/imagen_inscripcion"
            ),
            PreguntaFrecuente(
                7,
                "¿Puedo llevar a niños a las rodadas?",
                "Sí, pero deben ir acompañados por un adulto en todo momento.",
                "Familia",
                "https://www.google.com/imagen_ninos"
            ),
            PreguntaFrecuente(
                8,
                "¿Las rodadas tienen algún costo?",
                "No, son gratuitas.",
                "Costos",
                "https://www.google.com/imagen_costos"
            ),
            PreguntaFrecuente(
                9,
                "¿Qué debo hacer si mi bici se descompone durante una rodada?",
                "Contamos con un equipo de apoyo para resolver problemas mecánicos menores.",
                "Asistencia técnica",
                "https://www.google.com/imagen_asistencia"
            )
        )
    }


}