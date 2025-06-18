package repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import models.ComboOption
import models.Interview


class FirestoreRepository {

    private val db: FirebaseFirestore = Firebase.firestore

    // Guardar entrevista
    suspend fun saveInterview(interview: Interview): Result<String> {
        return try {
            val docRef = db.collection("interviews").document()
            val interviewWithId = interview.copy(id = docRef.id)
            docRef.set(interviewWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener opciones para combos
    suspend fun getCiudades(): Result<List<ComboOption>> {
        return try {
            val snapshot = db.collection("ciudades").get().await()
            val ciudades = snapshot.documents.map { doc ->
                ComboOption(
                    id = doc.id,
                    value = doc.getString("value") ?: "",
                    label = doc.getString("label") ?: ""
                )
            }
            Result.success(ciudades)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNivelesEducacion(): Result<List<ComboOption>> {
        return try {
            val snapshot = db.collection("niveles_educacion").get().await()
            val niveles = snapshot.documents.map { doc ->
                ComboOption(
                    id = doc.id,
                    value = doc.getString("value") ?: "",
                    label = doc.getString("label") ?: ""
                )
            }
            Result.success(niveles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAreasInteres(): Result<List<ComboOption>> {
        return try {
            val snapshot = db.collection("areas_interes").get().await()
            val areas = snapshot.documents.map { doc ->
                ComboOption(
                    id = doc.id,
                    value = doc.getString("value") ?: "",
                    label = doc.getString("label") ?: ""
                )
            }
            Result.success(areas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getUserInterviews(userEmail: String): Result<List<Interview>> {
        return try {
            val snapshot = db.collection("interviews")
                .whereEqualTo("userEmail", userEmail)
                .orderBy("fechaCreacion", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val interviews = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Interview::class.java)
            }

            Result.success(interviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteInterview(interviewId: String): Result<Unit> {
        return try {
            db.collection("interviews").document(interviewId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}