package com.udacity.asteroidradar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // set this instead of xml declaration to prevent crash on phone rotation
        findNavController(R.id.nav_host_fragment).setGraph(R.navigation.main_nav_graph)
    }
}
