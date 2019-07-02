package com.app.feirapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.GoogleAuthProvider
import android.content.Intent
import android.graphics.Color
import android.os.Parcelable
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import com.google.android.gms.common.api.GoogleApiClient
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.signin_layout.*
import com.google.firebase.database.Logger.Level


class ShareActivity : AppCompatActivity() {

    // Firebase Auth Object.
    lateinit var firebaseAuth: FirebaseAuth

    // Google API Client object.
    lateinit var googleApiClient: GoogleApiClient

    // Sing out button.
    lateinit var SignOutButton: Button

    // Google Sign In button .
    lateinit var signInButton: SignInButton

    // TextView to Show Login User Email and Name.
    lateinit var LoginUserName: TextView

    private var database: FirebaseDatabase? = null
    private var persistenceInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_layout)

        database = FirebaseDatabase.getInstance()

        if (!persistenceInitialized) {
            database!!.setPersistenceEnabled(true)
            persistenceInitialized = true
        }

        database!!.setLogLevel(Level.DEBUG)

        // altera a cor da status bar
        val w = this.window
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        w.statusBarColor = Color.parseColor("#F57C00")

        signInButton = findViewById(R.id.sign_in_button) as SignInButton
        SignOutButton = findViewById(R.id.buttonLogout) as Button
        LoginUserName = findViewById(R.id.textViewName) as TextView
        signInButton = findViewById(R.id.sign_in_button) as SignInButton

        // Getting Firebase Auth Instance into firebaseAuth object.
        firebaseAuth = FirebaseAuth.getInstance()

        // Hiding the TextView on activity start up time.
        loginSucessView.visibility = View.GONE

        // Creating and Configuring Google Sign In object.
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        // Creating and Configuring Google Api Client.
        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this  /* OnConnectionFailedListener */) { }
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build()

        // Adding Click listener to User Sign in Google button.
        signInButton.setOnClickListener { UserSignInMethod() }

        // Adding Click Listener to User Sign Out button.
        SignOutButton.setOnClickListener { UserSignOutFunction() }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser

        updateUI(currentUser)
    }

    // Sign In function Starts From Here.
    fun UserSignInMethod() {
        // Passing Google Api Client into Intent.
        val intentAuth = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)

        startActivityForResult(intentAuth, RequestSignInCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestSignInCode) {
            val googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            if (googleSignInResult.isSuccess) {
                val googleSignInAccount = googleSignInResult.signInAccount
                FirebaseUserAuth(googleSignInAccount)
            }
        }
    }

    fun FirebaseUserAuth(googleSignInAccount: GoogleSignInAccount?) {

        val authCredential = GoogleAuthProvider.getCredential(googleSignInAccount!!.idToken, null)

        Toast.makeText(this, "" + authCredential.provider, Toast.LENGTH_LONG).show()

        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this) { AuthResultTask ->
                    if (AuthResultTask.isSuccessful) {
                        // Getting Current Login user details.
                        val firebaseUser = firebaseAuth.currentUser

                        updateUI(firebaseUser!!)

                        userEmail!!.setOnEditorActionListener { v, actionId, event ->
                            if (actionId == EditorInfo.IME_ACTION_SEND) {
                                compartilharLista(firebaseUser, userEmail.text.toString())
                                true
                            } else {
                                false
                            }
                        }

                    } else {
                        Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun compartilharLista(firebaseUser: FirebaseUser, userEmail: String) {
        val main = this.intent
        val produtosArrayList = main.getParcelableArrayListExtra<Parcelable>("produtosArrayList") as ArrayList<Produto>

        database!!.reference.child(firebaseUser.email.toString()+"_shares").child(userEmail).setValue(produtosArrayList)
    }

    fun UserSignOutFunction() {

        // Sing Out the User.
        firebaseAuth.signOut()

        val currentUser = firebaseAuth.currentUser

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback {
            // Write down your any code here which you want to execute After Sign Out.

            // Printing Logout toast message on screen.
            Toast.makeText(this@ShareActivity, "Logout Successfully", Toast.LENGTH_LONG).show()
        }

        updateUI(currentUser)

    }

    private fun updateUI(user: FirebaseUser?) {

        if (user == null) {
            noLoginView.visibility = View.VISIBLE
            loginSucessView.visibility = View.GONE
        } else {
            noLoginView.visibility = View.GONE
            loginSucessView.visibility = View.VISIBLE
            LoginUserName.text = "Olá " + user!!.displayName!!.toString() + ","
        }
    }

    companion object {

        // TAG is for show some tag logs in LOG screen.
        val TAG = "ShareActivity"

        // Request sing in code. Could be anything as you required.
        val RequestSignInCode = 7
    }

}