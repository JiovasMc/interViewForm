package com.example.interviewform2

import GoogleSignInHelper
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;

import androidx.credentials.PasswordCredential;
import androidx.lifecycle.lifecycleScope
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {


    //lateinit var gsc: GoogleSignInClient
    //lateinit var gso: GoogleSignInOptions
    lateinit var usuarioInp : EditText;
    lateinit var contraseniaInp : EditText;
    lateinit var btnIngresar : Button;

    //lateinit var  btnFacebook : ShapeableImageView;
    //private lateinit var contraseniaInput: EditText
    private var isPasswordVisible = false;
    private lateinit var googleBtn: ShapeableImageView
    private lateinit var googleSignInHelper: GoogleSignInHelper
    private val webClientId = "814016581878-fn38qs5p3s4om4f5k8fvp3c8osb09o4a.apps.googleusercontent.com"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        usuarioInp = findViewById(R.id.username_input)
        contraseniaInp = findViewById(R.id.contrasenia_input)
        btnIngresar = findViewById(R.id.ingresar_btn)
        googleBtn = findViewById(R.id.google_btn)
        //btnFacebook = findViewById(R.id.facebook_btn)
        googleSignInHelper = GoogleSignInHelper(this, webClientId)

        btnIngresar.setOnClickListener{
            val usuario = usuarioInp.text.toString()
            val contrasenia = contraseniaInp.text.toString()
            if(usuario == "admin" && contrasenia == "admin"){
                Toast.makeText(this@MainActivity, "Login Correcto",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@MainActivity, "Login Incorrecto",Toast.LENGTH_SHORT).show()
            }
            //Log.i("Test Credenciales","Usuario: $usuario, Contraseña: $contrasenia")
        }

//        googleBtn.setOnClickListener{
//            Log.i("Test Boton","Google")
//        }
//        btnFacebook.setOnClickListener{
//            Log.i("Test Boton","Facebook")
//        }


        //llama a la funcion para la animación de la contraseña
        setupDrawableEndClick()

        //llama a la función de login de google
        setupGoogleSignIn()

    }

    //Aplica animación para mostrar o esconder la contraseña
    private fun setupDrawableEndClick() {
        contraseniaInp.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = contraseniaInp.compoundDrawables[2]
                if (drawableEnd != null) {
                    val drawableWidth = drawableEnd.bounds.width()
                    val touchX = event.x
                    val editTextWidth = contraseniaInp.width
                    val paddingEnd = contraseniaInp.paddingEnd

                    if (touchX >= (editTextWidth - drawableWidth - paddingEnd)) {
                        togglePasswordVisibility()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }
    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Ocultar contraseña
            contraseniaInp.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            contraseniaInp.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.icon_passkey_wh, 0, R.drawable.ic_eye_off_white, 0
            )
            isPasswordVisible = false
        } else {
            // Mostrar contraseña
            contraseniaInp.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            contraseniaInp.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.icon_passkey_wh, 0, R.drawable.ic_eye_white, 0
            )
            isPasswordVisible = true
        }

        // Mantener el cursor al final
        contraseniaInp.setSelection(contraseniaInp.text.length)
    }

    private fun setupGoogleSignIn() {
        googleBtn.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        lifecycleScope.launch {
            googleSignInHelper.signIn(
                onSuccess = { credential ->
                    // Autenticación exitosa
                    val idToken = credential.idToken
                    val displayName = credential.displayName
                    val email = credential.id
                    val profilePicture = credential.profilePictureUri

                    Log.d("GoogleSignIn", "Success: $displayName, $email")
                    Toast.makeText(this@MainActivity, "¡Bienvenido $displayName!", Toast.LENGTH_SHORT).show()

                    // Aquí puedes navegar a otra actividad o guardar los datos
                    handleSuccessfulSignIn(credential)
                },
                onError = { exception ->
                    Log.e("GoogleSignIn", "Error: ${exception.message}")
                    Toast.makeText(this@MainActivity, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun handleSuccessfulSignIn(credential: com.google.android.libraries.identity.googleid.GoogleIdTokenCredential) {
        // Guardar datos del usuario
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_name", credential.displayName)
            putString("user_email", credential.id)
            putString("user_photo", credential.profilePictureUri?.toString())
            putBoolean("is_logged_in", true)
            apply()
        }
        // Navegar a HomeActivity
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
        // Navegar a la siguiente pantalla
        // startActivity(Intent(this, HomeActivity::class.java))
        // finish()
    }

}