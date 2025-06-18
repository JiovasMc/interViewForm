package com.example.interviewform2
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import models.Interview
import repository.FirestoreRepository
import kotlinx.coroutines.launch
import com.example.interviewform2.InterviewDetailsDialog

class InterviewsListActivity : AppCompatActivity() {

    private lateinit var repository: FirestoreRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InterviewsAdapter
    private lateinit var emptyState: LinearLayout
    private lateinit var loadingOverlay: RelativeLayout
    private lateinit var loadingText: TextView
    private lateinit var backButton: ImageButton
    private lateinit var createInterviewButton: Button

    private var interviews = mutableListOf<Interview>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interviews_list)

        repository = FirestoreRepository()
        initViews()
        setupRecyclerView()
        setupButtons()
        loadInterviews()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.interviews_recycler_view)
        emptyState = findViewById(R.id.empty_state)
        loadingOverlay = findViewById(R.id.loading_overlay)
        loadingText = findViewById(R.id.loading_text)
        backButton = findViewById(R.id.back_button)
        createInterviewButton = findViewById(R.id.create_interview_button)
    }

    private fun setupRecyclerView() {
        adapter = InterviewsAdapter(interviews) { interview, action ->
            when (action) {
                "view" -> viewInterviewDetails(interview)
                "delete" -> deleteInterview(interview)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupButtons() {
        backButton.setOnClickListener {
            finish()
        }

        createInterviewButton.setOnClickListener {
            startActivity(Intent(this, InterviewFormActivity::class.java))
        }
    }

    private fun showLoading(show: Boolean, message: String = "Cargando...") {
        if (show) {
            loadingOverlay.visibility = View.VISIBLE
            loadingText.text = message
        } else {
            loadingOverlay.visibility = View.GONE
        }
    }

    private fun loadInterviews() {
        showLoading(true, "🔄 Cargando entrevistas...")

        lifecycleScope.launch {
            try {
                val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                val userEmail = sharedPref.getString("user_email", "") ?: ""

                repository.getUserInterviews(userEmail).fold(
                    onSuccess = { interviewsList ->
                        interviews.clear()
                        interviews.addAll(interviewsList)

                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                            showEmptyState(interviews.isEmpty())
                            showLoading(false)
                        }
                    },
                    onFailure = { exception ->
                        runOnUiThread {
                            Toast.makeText(this@InterviewsListActivity,
                                "Error cargando entrevistas: ${exception.message}",
                                Toast.LENGTH_LONG).show()
                            showEmptyState(true)
                            showLoading(false)

                            //Log.i("Error cargando entrevistas", "loadInterviews Error cargando entrevistas: ${exception.message} ")
                        }
                    }
                )

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@InterviewsListActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG).show()
                    showEmptyState(true)
                    showLoading(false)
                }
            }
        }
    }

    private fun showEmptyState(show: Boolean) {
        if (show) {
            emptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun viewInterviewDetails(interview: Interview) {
        // Aquí puedes crear una nueva Activity para mostrar detalles
        // o mostrar un diálogo con la información completa
        //Toast.makeText(this, "Ver detalles de ${interview.nombre}", Toast.LENGTH_SHORT).show()
        //InterviewDetailsDialog(this, interview)
        showInterviewDetails(interview)
    }

    private fun deleteInterview(interview: Interview) {
        // Confirmar eliminación
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Eliminar Entrevista")
            .setMessage("¿Estás seguro de que quieres eliminar la entrevista de ${interview.nombre}?")
            .setPositiveButton("Eliminar") { _, _ ->
                performDelete(interview)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun performDelete(interview: Interview) {
        showLoading(true, "🗑️ Eliminando entrevista...")

        lifecycleScope.launch {
            repository.deleteInterview(interview.id).fold(
                onSuccess = {
                    runOnUiThread {
                        interviews.remove(interview)
                        adapter.notifyDataSetChanged()
                        showEmptyState(interviews.isEmpty())
                        showLoading(false)
                        Toast.makeText(this@InterviewsListActivity,
                            "✅ Entrevista eliminada",
                            Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = { exception ->
                    runOnUiThread {
                        showLoading(false)
                        Toast.makeText(this@InterviewsListActivity,
                            "❌ Error eliminando: ${exception.message}",
                            Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }

    private fun showInterviewDetails(interview: Interview) {
        val dialog = InterviewDetailsDialog(
            context = this,
            interview = interview,
            onEditClick = { interviewToEdit ->
                // Aquí puedes abrir la pantalla de edición
                openEditInterview(interviewToEdit)
            }
        )
        dialog.show()
    }

    private fun openEditInterview(interview: Interview) {
        // Abrir la pantalla de edición con los datos pre-cargados
        val intent = Intent(this, InterviewFormActivity::class.java)
        intent.putExtra("interview_id", interview.id)
        intent.putExtra("edit_mode", true)
        startActivity(intent)
    }


    override fun onResume() {
        super.onResume()
        // Recargar entrevistas cuando regrese de crear una nueva
        loadInterviews()
    }
}