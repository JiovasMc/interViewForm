package models

import com.google.firebase.Timestamp

data class Interview(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val edad: Int = 0,
    val telefono: String = "",
    val ciudad: String = "",
    val experienciaLaboral: String = "",
    val nivelEducacion: String = "",
    val areaInteres: String = "",
    val disponibilidadHoraria: String = "",
    val salarioEsperado: String = "",
    val comentarios: String = "",
    val interviewEmail : String = "",
    val fechaCreacion: Timestamp = Timestamp.now()
)