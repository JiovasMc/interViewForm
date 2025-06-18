package com.example.interviewform2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop

class HomeActivity : AppCompatActivity() {

    private lateinit var userNameText: TextView
//    private lateinit var userEmailText: TextView
    private lateinit var userPhotoImage: ImageView
    private lateinit var signOutButton: Button
    private lateinit var interviewButton: Button
    private lateinit var viewInterviewsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        loadUserData()
        setupButtons()
    }

    private fun initViews() {
        userNameText = findViewById(R.id.user_name_text)
//        userEmailText = findViewById(R.id.user_email_text)
        userPhotoImage = findViewById(R.id.user_photo_image)
        signOutButton = findViewById(R.id.sign_out_button)
        interviewButton = findViewById(R.id.interview_button)
        viewInterviewsButton = findViewById(R.id.view_interviews_button)
    }

    private fun loadUserData() {
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)

        val userName = sharedPref.getString("user_name", "Usuario")
        val userEmail = sharedPref.getString("user_email", "email@ejemplo.com")
        val userPhoto = sharedPref.getString("user_photo", null)

        userNameText.text = "¡Hola, $userName!"
//        userEmailText.text = userEmail

        // Cargar foto de perfil si existe
        if (!userPhoto.isNullOrEmpty()) {
            Glide.with(this)
                .load(userPhoto)
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .into(userPhotoImage)
        }
    }

    private fun setupButtons() {
        // Botón para ir al formulario de entrevista
        interviewButton.setOnClickListener {
            val intent = Intent(this, InterviewFormActivity::class.java)
            startActivity(intent)
        }

         //Botón para ver entrevistas guardadas
        viewInterviewsButton.setOnClickListener {
            val intent = Intent(this, InterviewsListActivity::class.java)
            startActivity(intent)
        }

        // Botón para cerrar sesión
        signOutButton.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        // Limpiar datos guardados
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        // Volver a MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}