package com.hectorcortes.authhector

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

enum class ProviderType {
    BASIC,
    GOOGLE,
    FACEBOOK
}

class HomeActivity : AppCompatActivity() {
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setContentView(R.layout.activity_home)


        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?: "", provider ?: "")


        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
    }

    private fun setup(email: String, provider: String) {

        val emailTextView = findViewById<TextView>(R.id.emailTextView)
        val providerTextView = findViewById<TextView>(R.id.providerTextView)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val lastnameEditText = findViewById<EditText>(R.id.lastNameEditText)
        val matriculaEditText = findViewById<EditText>(R.id.matriculaEditText)
        val facultadEditText = findViewById<EditText>(R.id.facultadEditText)
        val semestreEditText = findViewById<EditText>(R.id.semestreEditText)


        val logOutButton = findViewById<Button>(R.id.logOutButton)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val getButton = findViewById<Button>(R.id.getButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)

       val MenuButton = findViewById<Button>(R.id.MenuButton)


        title = "Inicio"
        emailTextView.text = email
        providerTextView.text = provider

        MenuButton.setOnClickListener {
           val Menuintent = Intent(this, menu::class.java)
            startActivity(Menuintent)
        }

        logOutButton.setOnClickListener {
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            if (provider == ProviderType.FACEBOOK.name) {
                LoginManager.getInstance().logOut()
            }

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

        saveButton.setOnClickListener {
            db.collection("users").document(email).set(
                hashMapOf(
                    "provider" to provider,
                    "name" to nameEditText.text.toString(),
                    "lastname" to lastnameEditText.text.toString(),
                    "matricula" to matriculaEditText.text.toString(),
                    "facultad" to facultadEditText.text.toString(),
                    "semestre" to semestreEditText.text.toString()
                )
            )
        }

        getButton.setOnClickListener {
            db.collection("users").document(email).get().addOnSuccessListener {
                nameEditText.setText(it.getString("name"))
                lastnameEditText.setText(it.getString("lastname"))
                matriculaEditText.setText(it.getString("matricula"))
                facultadEditText.setText(it.getString("facultad"))
                semestreEditText.setText(it.getString("semestre"))
            }
        }

        deleteButton.setOnClickListener {
            db.collection("users").document(email).delete()
        }
    }
}
