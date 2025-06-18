package com.example.interviewform2
import models.ComboOption
import models.Interview
import repository.FirestoreRepository
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.interviewform2.utils.FirestoreSeeder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class InterviewFormActivity : AppCompatActivity() {

    private lateinit var repository: FirestoreRepository
    private lateinit var seeder: FirestoreSeeder

    // Views
    private lateinit var nombreInput: TextInputEditText
    private lateinit var apellidoInput: TextInputEditText
    private lateinit var edadInput: TextInputEditText
    private lateinit var telefonoInput: TextInputEditText
    private lateinit var ciudadSpinner: Spinner
    private lateinit var experienciaInput: TextInputEditText
    private lateinit var educacionSpinner: Spinner
    private lateinit var areaInteresSpinner: Spinner
    private lateinit var disponibilidadInput: TextInputEditText
    private lateinit var salarioInput: TextInputEditText
    private lateinit var comentariosInput: TextInputEditText
    private lateinit var guardarButton: Button
    private lateinit var cancelarButton: Button
    private lateinit var loadingOverlay: RelativeLayout
    private lateinit var loadingText: TextView
    private lateinit var mainContent: ScrollView
    private lateinit var interviewerEmailInput: TextInputEditText

    // Datos para spinners
    private var ciudades = listOf<ComboOption>()
    private var nivelesEducacion = listOf<ComboOption>()
    private var areasInteres = listOf<ComboOption>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interview_form)

        repository = FirestoreRepository()
        seeder = FirestoreSeeder()

        initViews()
        showLoading(true, "🔄 Cargando datos del formulario...")
        loadComboData()
        setupButtons()
    }

    private fun initViews() {
        nombreInput = findViewById(R.id.nombre_input)
        apellidoInput = findViewById(R.id.apellido_input)
        edadInput = findViewById(R.id.edad_input)
        telefonoInput = findViewById(R.id.telefono_input)
        ciudadSpinner = findViewById(R.id.ciudad_spinner)
        experienciaInput = findViewById(R.id.experiencia_input)
        educacionSpinner = findViewById(R.id.educacion_spinner)
        areaInteresSpinner = findViewById(R.id.area_interes_spinner)
        disponibilidadInput = findViewById(R.id.disponibilidad_input)
        salarioInput = findViewById(R.id.salario_input)
        comentariosInput = findViewById(R.id.comentarios_input)
        guardarButton = findViewById(R.id.guardar_button)
        cancelarButton = findViewById(R.id.cancelar_button)
        loadingOverlay = findViewById(R.id.loading_overlay)
        loadingText = findViewById(R.id.loading_text)
        mainContent = findViewById(R.id.main_content)
        interviewerEmailInput = findViewById(R.id.interviewEmail_input)
    }

    private fun showLoading(show: Boolean, message: String = "Cargando...") {
        if (show) {
            loadingOverlay.visibility = View.VISIBLE
            loadingText.text = message
            mainContent.alpha = 0.3f
        } else {
            loadingOverlay.visibility = View.GONE
            mainContent.alpha = 1.0f
        }
    }

    private fun loadComboData() {
        lifecycleScope.launch {
            try {
                Log.d("InterviewForm", "Iniciando carga de datos...")

                // Cargar ciudades
                val ciudadesResult = repository.getCiudades()
                ciudadesResult.fold(
                    onSuccess = { data ->
                        if (data.isEmpty()) {
                            Log.d("InterviewForm", "No hay ciudades, poblando datos iniciales...")
                            showLoading(true, "📊 Configurando base de datos...")
                            seeder.seedInitialData()
                            loadCiudadesAgain()
                        } else {
                            Log.d("InterviewForm", "Ciudades cargadas: ${data.size}")
                            ciudades = data
                            setupSpinner(ciudadSpinner, data)
                        }
                    },
                    onFailure = { error ->
                        Log.e("InterviewForm", "Error cargando ciudades: ${error.message}")
                        useDefaultCiudades()
                    }
                )

                // Cargar niveles de educación
                val educacionResult = repository.getNivelesEducacion()
                educacionResult.fold(
                    onSuccess = { data ->
                        if (data.isNotEmpty()) {
                            Log.d("InterviewForm", "Educación cargada: ${data.size}")
                            nivelesEducacion = data
                            setupSpinner(educacionSpinner, data)
                        } else {
                            useDefaultEducacion()
                        }
                    },
                    onFailure = { error ->
                        Log.e("InterviewForm", "Error cargando educación: ${error.message}")
                        useDefaultEducacion()
                    }
                )

                // Cargar áreas de interés
                val areasResult = repository.getAreasInteres()
                areasResult.fold(
                    onSuccess = { data ->
                        if (data.isNotEmpty()) {
                            Log.d("InterviewForm", "Áreas cargadas: ${data.size}")
                            areasInteres = data
                            setupSpinner(areaInteresSpinner, data)
                        } else {
                            useDefaultAreas()
                        }
                    },
                    onFailure = { error ->
                        Log.e("InterviewForm", "Error cargando áreas: ${error.message}")
                        useDefaultAreas()
                    }
                )

                showLoading(false)

            } catch (e: Exception) {
                Log.e("InterviewForm", "Error general: ${e.message}")
                showLoading(false)
                useAllDefaults()
            }
        }
    }

    private suspend fun loadCiudadesAgain() {
        kotlinx.coroutines.delay(2000)

        val result = repository.getCiudades()
        result.fold(
            onSuccess = { data ->
                if (data.isNotEmpty()) {
                    ciudades = data
                    setupSpinner(ciudadSpinner, data)
                } else {
                    useDefaultCiudades()
                }
            },
            onFailure = {
                useDefaultCiudades()
            }
        )
    }

    private fun useDefaultCiudades() {
        val defaultCiudades = listOf(
            ComboOption("1", "mexico", "México")
        )
        ciudades = defaultCiudades
        setupSpinner(ciudadSpinner, defaultCiudades)
        Log.d("InterviewForm", "Usando ciudades por defecto")
    }

    private fun useDefaultEducacion() {
        val defaultEducacion = listOf(
            ComboOption("1", "secundaria", "Educación Secundaria"),
            ComboOption("2", "bachillerato", "Bachillerato"),
            ComboOption("3", "universitario", "Universitario"),
            ComboOption("4", "posgrado", "Posgrado")
        )
        nivelesEducacion = defaultEducacion
        setupSpinner(educacionSpinner, defaultEducacion)
        Log.d("InterviewForm", "Usando educación por defecto")
    }

    private fun useDefaultAreas() {
        val defaultAreas = listOf(
            ComboOption("1", "tecnologia", "Tecnología"),
            ComboOption("2", "marketing", "Marketing"),
            ComboOption("3", "ventas", "Ventas"),
            ComboOption("4", "recursos_humanos", "Recursos Humanos")
        )
        areasInteres = defaultAreas
        setupSpinner(areaInteresSpinner, defaultAreas)
        Log.d("InterviewForm", "Usando áreas por defecto")
    }

    private fun useAllDefaults() {
        useDefaultCiudades()
        useDefaultEducacion()
        useDefaultAreas()
        Toast.makeText(this, "⚠️ Usando datos por defecto. Revisa la conexión.", Toast.LENGTH_LONG).show()
    }

    private fun setupSpinner(spinner: Spinner, options: List<ComboOption>) {
        val labels = options.map { it.label }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        Log.d("InterviewForm", "Spinner configurado con ${labels.size} opciones")
    }

    private fun setupButtons() {
        guardarButton.setOnClickListener {
            if (validateForm()) {
                saveInterview()
            }
        }

        cancelarButton.setOnClickListener {
            finish()
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (nombreInput.text.toString().trim().isEmpty()) {
            nombreInput.error = "El nombre es requerido"
            isValid = false
        }

        if (apellidoInput.text.toString().trim().isEmpty()) {
            apellidoInput.error = "El apellido es requerido"
            isValid = false
        }

        if (edadInput.text.toString().trim().isEmpty()) {
            edadInput.error = "La edad es requerida"
            isValid = false
        }

        return isValid
    }

    private fun saveInterview() {
        showLoading(true, "💾 Guardando entrevista...")

        lifecycleScope.launch {
            try {
                val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                val userId = sharedPref.getString("user_email", "") ?: ""
                val userName = sharedPref.getString("user_name", "") ?: ""
                val userEmail = sharedPref.getString("user_email", "") ?: ""

                val interview = Interview(
                    userId = userId,
                    userName = userName,
                    userEmail = userEmail,
                    nombre = nombreInput.text.toString().trim(),
                    apellido = apellidoInput.text.toString().trim(),
                    edad = edadInput.text.toString().toIntOrNull() ?: 0,
                    telefono = telefonoInput.text.toString().trim(),
                    interviewEmail = interviewerEmailInput.text.toString().trim(),
                    ciudad = if (ciudadSpinner.selectedItemPosition >= 0 && ciudades.isNotEmpty())
                        ciudades[ciudadSpinner.selectedItemPosition].value else "",
                    experienciaLaboral = experienciaInput.text.toString().trim(),
                    nivelEducacion = if (educacionSpinner.selectedItemPosition >= 0 && nivelesEducacion.isNotEmpty())
                        nivelesEducacion[educacionSpinner.selectedItemPosition].value else "",
                    areaInteres = if (areaInteresSpinner.selectedItemPosition >= 0 && areasInteres.isNotEmpty())
                        areasInteres[areaInteresSpinner.selectedItemPosition].value else "",
                    disponibilidadHoraria = disponibilidadInput.text.toString().trim(),
                    salarioEsperado = salarioInput.text.toString().trim(),
                    comentarios = comentariosInput.text.toString().trim()
                )

                repository.saveInterview(interview).fold(
                    onSuccess = { documentId ->
                        showLoading(false)
                        Toast.makeText(this@InterviewFormActivity, "✅ Entrevista guardada correctamente", Toast.LENGTH_LONG).show()
                        finish()
                    },
                    onFailure = { exception ->
                        showLoading(false)
                        Toast.makeText(this@InterviewFormActivity, "❌ Error al guardar: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
                )

            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(this@InterviewFormActivity, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}