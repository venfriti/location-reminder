package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    companion object {
        const val TAG = "Authentication Activity"
        const val SIGN_IN_RESULT_CODE = 1001
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.authButton.setOnClickListener{
            launchSignInFlow()
        }

        if (isLoggedIn()){
            navigateToRemindersActivity()
        }
    }

    private fun launchSignInFlow() {
        if (isLoggedIn()) {
            navigateToRemindersActivity()
        } else {
            setupFirebaseUI()
        }
    }

    private fun isLoggedIn(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val response = IdpResponse.fromResultIntent(result.data)

        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            user?.displayName
            navigateToRemindersActivity()
        } else {
            val errorMessage = response?.error?.message
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            launchSignInFlow()
            // Sign-in failed
            // You can handle the error case here, e.g., display an error message
        }
    }

    private fun setupFirebaseUI() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), // Email sign-in
            AuthUI.IdpConfig.GoogleBuilder().build() // Google sign-in
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }




    // TODO: If the user was authenticated, send him to RemindersActivity
    private fun navigateToRemindersActivity() {
        val intent = Intent(this, RemindersActivity::class.java)
        startActivity(intent)
        finish() // Optional: Finish the LoginActivity so that the user cannot navigate back to it using the back button
    }

    // TODO: a bonus is to customize the sign in flow to look nice using :
    //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
}