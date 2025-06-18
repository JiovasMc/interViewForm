package com.example.interviewform2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import models.Interview
import java.text.SimpleDateFormat
import java.util.*

class InterviewsAdapter(
    private val interviews: List<Interview>,
    private val onItemAction: (Interview, String) -> Unit
) : RecyclerView.Adapter<InterviewsAdapter.InterviewViewHolder>() {

    class InterviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val interviewName: TextView = itemView.findViewById(R.id.interview_name)
        val interviewDate: TextView = itemView.findViewById(R.id.interview_date)
        val interviewEmail: TextView = itemView.findViewById(R.id.interview_email)
        val interviewCity: TextView = itemView.findViewById(R.id.interview_city)
        val interviewArea: TextView = itemView.findViewById(R.id.interview_area)
        val viewDetailsButton: Button = itemView.findViewById(R.id.view_details_button)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_interview, parent, false)
        return InterviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: InterviewViewHolder, position: Int) {
        val interview = interviews[position]

        // Configurar datos básicos
        holder.interviewName.text = "${interview.nombre} ${interview.apellido}"
        holder.interviewEmail.text = interview.interviewEmail

        // Formatear fecha
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(interview.fechaCreacion.toDate())
        holder.interviewDate.text = formattedDate

        // Mostrar ciudad (con fallback)
        holder.interviewCity.text = if (interview.ciudad.isNotEmpty()) {
            getCityLabel(interview.ciudad)
        } else {
            "No especificada"
        }

        // Mostrar área de interés (con fallback)
        holder.interviewArea.text = if (interview.areaInteres.isNotEmpty()) {
            getAreaLabel(interview.areaInteres)
        } else {
            "No especificada"
        }

        // Configurar botones
        holder.viewDetailsButton.setOnClickListener {
            onItemAction(interview, "view")
        }

        holder.deleteButton.setOnClickListener {
            onItemAction(interview, "delete")
        }
    }

    override fun getItemCount(): Int = interviews.size

    // Función para convertir el valor de ciudad a etiqueta legible
    private fun getCityLabel(cityValue: String): String {
        return when (cityValue) {
            "madrid" -> "Madrid"
            "barcelona" -> "Barcelona"
            "valencia" -> "Valencia"
            "sevilla" -> "Sevilla"
            "bilbao" -> "Bilbao"
            "malaga" -> "Málaga"
            else -> cityValue.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }
    }

    // Función para convertir el valor de área a etiqueta legible
    private fun getAreaLabel(areaValue: String): String {
        return when (areaValue) {
            "tecnologia" -> "Tecnología"
            "marketing" -> "Marketing"
            "ventas" -> "Ventas"
            "recursos_humanos" -> "Recursos Humanos"
            "finanzas" -> "Finanzas"
            "administracion" -> "Administración"
            "diseno" -> "Diseño"
            "educacion" -> "Educación"
            else -> areaValue.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }
    }
}