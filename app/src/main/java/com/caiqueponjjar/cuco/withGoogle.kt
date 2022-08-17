package com.caiqueponjjar.cuco

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast

import androidx.annotation.NonNull
import androidx.constraintlayout.widget.Constraints.TAG
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.games.Games
import com.google.android.gms.games.GamesClient

import com.google.android.gms.tasks.OnFailureListener

import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import android.R
import android.os.PersistableBundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.caiqueponjjar.cuco.helper.usuario
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseUser





class withGoogle : AppCompatActivity() {

    //google sign in client
    var mGoogleSignInClient: GoogleSignInClient? = null

    private val RC_SIGN_IN = 111 //google sign in request code


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.caiqueponjjar.cuco.R.layout.activity_with_google)
        configureGoogleSignIn();
        doSignInSignOut()
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    startActivity(Intent(this@withGoogle, SelectLogin::class.java))
                    finish()
                }
            }
        this.onBackPressedDispatcher.addCallback(this, callback)
    }
    private fun configureGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail() //request email id
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = this?.let { getClient(it, gso) }
    }

    private fun doSignInSignOut() {

        //get the last sign in account
        val account = this?.let { GoogleSignIn.getLastSignedInAccount(it) }

        //if account doesn't exist do login else do sign out
        if (account == null) doGoogleSignIn() //else //doGoogleSignOut()
    }

    private fun doGoogleSignIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN) //pass the declared request code here
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            //method to handle google sign in result
            handleSignInResult(task)
        }
    }
    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            getProfileInformation(account);

            //show toast
            //Toast.makeText(this, "Google Sign In Successful.", Toast.LENGTH_SHORT).show();

        } catch (e: ApiException ) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

            //show toast
            Toast.makeText(this, "Failed to do Sign In : " + e.getStatusCode(), Toast.LENGTH_SHORT).show();

            //update Ui for this
            getProfileInformation(null);
        }
    }
    override fun onStart() {
        super.onStart()
        //SILENT SIGN IN
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = this?.let { GoogleSignIn.getLastSignedInAccount(it) }
        //update the UI if user has already sign in with the google for this app
        if(account != null) {
            getProfileInformation(account)
        }
    }
    /**
     * method to fetch user profile information from GoogleSignInAccount
     *
     * @param acct googleSignInAccount
     */
    private fun getProfileInformation(acct: GoogleSignInAccount?) {
        //if account is not null fetch the information

        if (acct != null) {
        //    Toast.makeText(activity, "Ola " + usuario.personEmail, Toast.LENGTH_SHORT).show();
            val fragment = FirstFragment();
            val bundle = Bundle()

            //user display name
            bundle.putString("username",acct.displayName)

            //user first name
            bundle.putString("personGivenName",acct.givenName)

            //user email id
            bundle.putString("personEmail",acct.email)
            //user unique id
            bundle.putString("personId",acct.id)

            startActivity(Intent(this, MainActivity::class.java))
        } else {
            doGoogleSignIn()
        }
    }


}