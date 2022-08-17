package com.caiqueponjjar.cuco.ui.login

import android.animation.ArgbEvaluator
import android.animation.TimeAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.caiqueponjjar.cuco.FirstFragment
import com.caiqueponjjar.cuco.MainActivity

import com.caiqueponjjar.cuco.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.SignInMethodQueryResult

import androidx.annotation.NonNull
import com.caiqueponjjar.cuco.SelectLogin
import com.caiqueponjjar.cuco.helper.usuario

import com.google.android.gms.tasks.OnCompleteListener
import java.lang.Exception


class LoginActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    val auth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // This callback will only be called when MyFragment is at least Started.
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    startActivity(Intent(this@LoginActivity, SelectLogin::class.java))
                }
            }
        this.onBackPressedDispatcher.addCallback(this, callback)
        val loginBtn = findViewById<Button>(R.id.login)
        loginBtn.setOnClickListener {
            email = findViewById<EditText>(R.id.email).text.toString()
            password = findViewById<EditText>(R.id.password).text.toString()
            //check email already exist or not.
            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(OnCompleteListener<SignInMethodQueryResult?> { task ->
                    try {

                    val isNewUser = task.result.signInMethods?.isEmpty()
                    if (isNewUser == true) {

                        createAccount(email)
                    } else {
                        Login(email, password)
                    }
                    }catch (e: Exception){
                        Toast.makeText(this,"email ou senha invalido.", Toast.LENGTH_SHORT)
                    }
                })
        }
        startAnimation(R.drawable.gradientanimated,this);
        val cadastrarBtn = findViewById<Button>(R.id.Cadastrar)
        cadastrarBtn.setOnClickListener {
            username = findViewById<EditText>(R.id.username).text.toString()
            setUsername(username)
        }
    }
    fun setUsername(username:String) {
        try {

        val userFirebase = FirebaseAuth.getInstance().currentUser
        val profileUpdates =
            UserProfileChangeRequest.Builder().setDisplayName(username).build()

        userFirebase!!.updateProfile(profileUpdates)

        //    Toast.makeText(activity, "Ola " + usuario.personEmail, Toast.LENGTH_SHORT).show();

        }finally {
            Login(email, password)
        }
    }
    fun LoadRegisterUsername(){

        //val user = auth.currentUser
        val FormRegister = findViewById<LinearLayout>(R.id.FormRegister2)
        val FormLgn = findViewById<LinearLayout>(R.id.Form)

        val RegisterFadeout: Animation = AnimationUtils.loadAnimation(this, R.anim.fadeout)
        RegisterFadeout.duration = 100
        RegisterFadeout.startOffset = 0
        FormRegister.visibility = View.VISIBLE
        findViewById<TextView>(R.id.username).startAnimation(RegisterFadeout) //Set animation to your ImageView

        FormLgn.visibility = View.GONE


        //Cria a animação
        val myFadeInAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.fadein)
        myFadeInAnimation.duration = 200
        myFadeInAnimation.startOffset = 100
        findViewById<TextView>(R.id.username).startAnimation(myFadeInAnimation) //Set animation to your ImageView
        val myFadeInAnimation1: Animation = AnimationUtils.loadAnimation(this, R.anim.fadein)
        myFadeInAnimation1.duration = 500
        myFadeInAnimation1.startOffset = 300
        findViewById<TextView>(R.id.CucoHello).startAnimation(myFadeInAnimation1) //Set animation to your ImageView
        val myFadeInAnimation2 : Animation = AnimationUtils.loadAnimation(this, R.anim.fadein)
        myFadeInAnimation2.duration = 500
        myFadeInAnimation2.startOffset = 1200
        findViewById<TextView>(R.id.Who).startAnimation(myFadeInAnimation2  ) //Set animation to your ImageView
        val myFadeInAnimation3 : Animation = AnimationUtils.loadAnimation(this, R.anim.fadein)
        myFadeInAnimation3.duration = 500
        myFadeInAnimation3.startOffset = 2000
        findViewById<TextView>(R.id.Cadastrar).startAnimation(myFadeInAnimation3  )
    }
    fun createAccount(email:String) {

        val emailLink = intent.data.toString()
            // Retrieve this from wherever you stored it
           // val email = "caiqueponjjar@domain.com"
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                     //Set animation to your ImageView
                    LoadRegisterUsername()
                   // Toast.makeText(this, "deu certo!!", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Ops, email ou senha inválido.",
                        Toast.LENGTH_SHORT).show()
                }
            }


    }

    fun Login(email: String, password: String ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                        if(usuario().getUsername(this).equals(null)) {

                            LoadRegisterUsername()
                        }else{

                            startActivity(Intent(this, MainActivity::class.java))
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Ops, email ou senha inválido.",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }
    fun startAnimation(view: Int, activity: Activity) {
        val start = Color.parseColor("#E1233F")
        val mid = Color.parseColor("#D8523A")
        val end = Color.parseColor("#E83515")
        val evaluator = ArgbEvaluator()
        val preloader = activity.findViewById<View>(R.id.gradientPreloaderView)
        preloader.visibility = View.VISIBLE
        val gradient = preloader.background as GradientDrawable
        val animator = TimeAnimator.ofFloat(0.0f, 1.0f)
        animator.duration = 3000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener { valueAnimator ->
            val fraction = valueAnimator.animatedFraction
            val newStart = evaluator.evaluate(fraction, start, end) as Int
            val newMid = evaluator.evaluate(fraction, mid, start) as Int
            val newEnd = evaluator.evaluate(fraction, end, mid) as Int
            val newArray = intArrayOf(newStart, newMid, newEnd)
            gradient.colors = newArray
        }
        animator.start()
    }
    }

