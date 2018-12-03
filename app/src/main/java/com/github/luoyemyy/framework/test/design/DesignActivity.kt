package com.github.luoyemyy.framework.test.design

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.luoyemyy.framework.test.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class DesignActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_design)

        findViewById<BottomNavigationView>(R.id.bottomNavigationView).apply {

        }
    }

}