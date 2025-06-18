package com.example.interviewform2

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import models.Interview
import java.text.SimpleDateFormat
import java.util.*

class InterviewDetailsDialog(
    context: Context,
    private val interview: Interview,
    private val onEditClick: ((Interview) -> Unit)? = null
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_interview_details)

        // Configurar el dialog
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(true)

        setupViews()
        populateData()
        setupButtons()
    }

    private fun setupViews() {
        // Configurar el tamaño del dialog
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun populateData() {
        // Información personal
        findViewById<TextView>(R.id.detail_nombre).text = "${interview.nombre} ${interview.apellido}"
        findViewById<TextView>(R.id.detail_edad).text = "${interview.edad} años"
        findViewById<TextView>(R.id.detail_telefono).text = interview.telefono.ifEmpty { "No especificado" }
        findViewById<TextView>(R.id.detail_interviewEmail).text = interview.interviewEmail.ifEmpty { "No especificado" }
        findViewById<TextView>(R.id.detail_ciudad).text = interview.ciudad.ifEmpty { "No especificada" }

        // Información laboral
        findViewById<TextView>(R.id.detail_experiencia).text = interview.experienciaLaboral.ifEmpty { "No especificada" }
        findViewById<TextView>(R.id.detail_educacion).text = interview.nivelEducacion.ifEmpty { "No especificado" }
        findViewById<TextView>(R.id.detail_area).text = interview.areaInteres.ifEmpty { "No especificada" }
        findViewById<TextView>(R.id.detail_disponibilidad).text = interview.disponibilidadHoraria.ifEmpty { "No especificada" }
        findViewById<TextView>(R.id.detail_salario).text = if (interview.salarioEsperado.isNotEmpty()) "$${interview.salarioEsperado}" else "No especificado"

        // Comentarios
        findViewById<TextView>(R.id.detail_comentarios).text = interview.comentarios.ifEmpty { "Sin comentarios adicionales" }

        // Fecha
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fechaTexto = dateFormat.format(interview.fechaCreacion.toDate())
        findViewById<TextView>(R.id.detail_fecha).text = fechaTexto
    }

    private fun setupButtons() {
        // Botón cerrar (X)
        findViewById<ImageButton>(R.id.close_button).setOnClickListener {
            dismiss()
        }

        // Botón cerrar
        findViewById<Button>(R.id.close_dialog_button).setOnClickListener {
            dismiss()
        }

        // Botón editar
//        findViewById<Button>(R.id.edit_button).setOnClickListener {
//            onEditClick?.invoke(interview)
//            dismiss()
//        }
    }
}