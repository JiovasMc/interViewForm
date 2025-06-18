package com.example.interviewform2.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreSeeder {

    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun seedInitialData() {
        try {
            // Poblar ciudades
            seedCiudades()

            // Poblar niveles de educación
            seedNivelesEducacion()

            // Poblar áreas de interés
            seedAreasInteres()

        } catch (e: Exception) {
            println("Error seeding data: ${e.message}")
        }
    }

    private suspend fun seedCiudades() {
        val ciudades = listOf(
            mapOf("value" to "mexico", "label" to "México"),
            mapOf("value" to "guadalajara", "label" to "Guadalajara"),
        mapOf("value" to "monterrey", "label" to "Monterrey"),
        mapOf("value" to "ecatepec", "label" to "Ecatepec"),
        mapOf("value" to "nezahualcoyotl", "label" to "Nezahualcóyotl"),
        mapOf("value" to "naucalpan", "label" to "Naucalpan"),
        mapOf("value" to "chimalhuacan", "label" to "Chimalhuacán"),
        mapOf("value" to "tlalnepantla", "label" to "Tlalnepantla"),
        mapOf("value" to "cuautitlan", "label" to "Cuautitlán"),
        mapOf("value" to "izcalli", "label" to "Izcalli")
        )

        ciudades.forEach { ciudad ->
            db.collection("ciudades").add(ciudad).await()
        }
    }

    private suspend fun seedNivelesEducacion() {
        val niveles = listOf(
            mapOf("value" to "primaria", "label" to "Primaria"),
            mapOf("value" to "secundaria", "label" to "Secundaria"),
            mapOf("value" to "bachillerato", "label" to "Bachillerato"),
            mapOf("value" to "tsu", "label" to "Técnico Superior"),
            mapOf("value" to "universidad", "label" to "Universidad"),
            mapOf("value" to "posgrado", "label" to "Posgrado"),
            mapOf("value" to "doctorado", "label" to "Doctorado")
        )

        niveles.forEach { nivel ->
            db.collection("niveles_educacion").add(nivel).await()
        }
    }

    private suspend fun seedAreasInteres() {
        val areas = listOf(
            mapOf("value" to "tecnologia", "label" to "Tecnología"),
            mapOf("value" to "marketing", "label" to "Marketing"),
            mapOf("value" to "ventas", "label" to "Ventas"),
            mapOf("value" to "recursos_humanos", "label" to "Recursos Humanos"),
            mapOf("value" to "finanzas", "label" to "Finanzas"),
            mapOf("value" to "administracion", "label" to "Administración"),
            mapOf("value" to "diseno", "label" to "Diseño"),
            mapOf("value" to "educacion", "label" to "Educación")
        )

        areas.forEach { area ->
            db.collection("areas_interes").add(area).await()
        }
    }
}