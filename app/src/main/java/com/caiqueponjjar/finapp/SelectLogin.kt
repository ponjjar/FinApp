package com.caiqueponjjar.finapp

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.caiqueponjjar.finapp.R.*
import com.caiqueponjjar.finapp.helper.usuario
import com.caiqueponjjar.finapp.ui.login.LoginActivity
import android.content.SharedPreferences
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog


class SelectLogin : AppCompatActivity() {

    private lateinit var videoview : VideoView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar()?.hide(); //<< this
        setContentView(layout.activity_select_login)
        val currentUser = usuario().getUser()
        if((currentUser != null || usuario().getAccount(this) != null) && ( usuario().getUsername(this) != null && usuario().getUsername(this)!!.trim() != "")){
            println("Usuario logado:" + usuario().getUsername(this))
            startActivity(Intent(this, MainActivity::class.java))
        }

        videoview = findViewById<View>(id.backgroundVideo) as VideoView
        val uri: Uri = Uri.parse("android.resource://" + packageName + "/" + raw.cuckoofootage)
        videoview.setVideoURI(uri)
        val metrics = DisplayMetrics()
        this.getWindowManager().getDefaultDisplay().getRealMetrics(metrics)
        val preferences: SharedPreferences = this.getSharedPreferences("", MODE_PRIVATE)
        if(!preferences.getBoolean("firstTime",false)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Termos e condições de uso")
            builder.setMessage("Ao utilizar o aplicativo você concorda com os termos e condições de uso")
            builder.setNeutralButton("Ver termos") { dialog, which ->
                //Toast.makeText(applicationContext,"ver termos",Toast.LENGTH_SHORT).show()
                // start a browser with the url
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://superponjjar.blogspot.com/2023/06/terms-conditions-finapp.html"))
                startActivity(browserIntent)
                finish()
            }
            builder.setPositiveButton("Continuar") { dialog, which ->
                //Toast.makeText(applicationContext,"continuar",Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                preferences.edit().putBoolean("firstTime", true).apply()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }

        // (getContext() as Activity).windowManager.defaultDisplay.getRealMetrics(metrics)
        //windowManager.defaultDisplay.getMetrics(metrics)
        val params = videoview.getLayoutParams()
        //params.leftMargin = 0
        videoview.setOnPreparedListener { mp: MediaPlayer ->

            val mVideoHeight = 720

            val mVideoWidth = 1280

            mp.setLooping(true)
            mp.setVolume(0.0F ,0.0F)
            if(metrics.heightPixels > metrics.widthPixels) {
                params.height =metrics.heightPixels
                params.width = metrics.heightPixels * ( metrics.heightPixels/ mVideoHeight)
            }else{
                params.height = metrics.widthPixels * metrics.widthPixels / mVideoHeight
                params.width =  metrics.widthPixels
            }
            videoview.setLayoutParams(params)

        }

        videoview.start()
        val myFadeInAnimation: Animation = AnimationUtils.loadAnimation(this, anim.fadein)
        findViewById<ImageView>(id.LogoImage).startAnimation(myFadeInAnimation) //Set animation to your ImageView
        findViewById<LinearLayout>(id.linearLayout).startAnimation(myFadeInAnimation) //Set animation to your ImageView

        val WithGoogleButton = findViewById<Button>(id.withGoogle)
        WithGoogleButton.setOnClickListener {
            startActivity(Intent(this, withGoogle::class.java))

        }
        val LoginButton = findViewById<Button>(id.login)
            LoginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        //Reproduz animação
        val fadeInDuration = 500 // Configure time values here

    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = usuario().getUser()
        if((currentUser != null || usuario().getAccount(this) != null) && ( usuario().getUsername(this) != null && usuario().getUsername(this)!!.trim() != "")){
            println("Usuario logado:" + usuario().getUsername(this))
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
    override fun onResume() {
        super.onResume()
        videoview.start()
    }

}