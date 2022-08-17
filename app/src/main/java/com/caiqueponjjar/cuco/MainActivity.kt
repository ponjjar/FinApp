package com.caiqueponjjar.cuco

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//Remove notification bar
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_main)
        val firstFragment = FirstFragment()
        val friendsFragment = friends()
        //val thirdFragment=()
        setCurrentFragment(firstFragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.Notes -> setCurrentFragment(firstFragment)
                R.id.groups -> setCurrentFragment(firstFragment)
                R.id.friends -> setCurrentFragment(friendsFragment)
            }
            true
        }
        //  supportActionBar?.apply{hide()}
    }

    private fun setCurrentFragment(fragment:Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_view,fragment)
            commit()
        }
}