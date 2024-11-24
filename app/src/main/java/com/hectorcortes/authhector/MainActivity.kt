package com.hectorcortes.authhector

import android.content.Context
import android.content.Intent // Corregido el import de Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout // Asegúrate de que authLayout sea del tipo correcto
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.callbackFlow


class MainActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100  // Declarada como constante
    private val callbackManager = CallbackManager.Factory.create()


    private lateinit var auth: FirebaseAuth
    private lateinit var authLayout: LinearLayout // Inicializar correctamente el layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    // color actionbar de la barra
        val actionBar = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF51834B")))


        // Inicializar Firebase Analytics
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de firebase completa")
        analytics.logEvent("InitScreen", bundle)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inicializar el layout
        authLayout = findViewById(R.id.authLayout)

        // Configuración de los botones y la autenticación
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()
        authLayout.visibility = View.VISIBLE
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)
        if (email != null && provider != null) {
            authLayout.visibility = View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun setup() {
        title = "Autenticación"

        // Inicialización de vistas
        val signUpButton1 = findViewById<Button>(R.id.signUpButton1)
        val loginButton1 = findViewById<Button>(R.id.loginButton1)
        val emailEditText = findViewById<EditText>(R.id.EmailEditText)
        val passwordEditText = findViewById<EditText>(R.id.PasswordeditText)
        val googleButton = findViewById<Button>(R.id.googleButton)
        val facebooButton = findViewById<Button>(R.id.facebooButton)


        // Lógica para el botón de registro
        signUpButton1.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        saveSession(it.result?.user?.email ?: "", ProviderType.BASIC)
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        loginButton1.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                auth.signInWithEmailAndPassword(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        saveSession(it.result?.user?.email ?: "", ProviderType.BASIC)
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        googleButton.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
        //Faceboo boton logica
        facebooButton.setOnClickListener{
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        result?.let {
                            val token = it.accessToken

                            val credential = FacebookAuthProvider.getCredential(token.token)

                            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{

                                if (it.isSuccessful) {
                                    showHome(it.result?.user?.email ?: "", ProviderType.FACEBOOK)
                                } else {
                                    showAlert()
                                }
                            }
                        }
                    }

                    override fun onCancel() {

                    }

                    override fun onError(error: FacebookException) {
                        showAlert()
                    }
                })
        }
    }

    private fun saveSession(email: String, provider: ProviderType) {
        val prefs =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider.name)
        prefs.apply()
    }

    // Función para mostrar la alerta en caso de error
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error en la autenticación")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Función para mostrar la pantalla principal
    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
    try{
        val account = task.getResult(ApiException::class.java)
        if(account != null){
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{

                if (it.isSuccessful) {
                    showHome(it.result?.user?.email ?: "", ProviderType.GOOGLE)
                } else {
                    showAlert()
                }
            }

        }

    } catch (e: ApiException ){
        showAlert()
    }

        }
    }
}
