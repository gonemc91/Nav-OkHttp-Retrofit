package com.example.nav_components_2_tabs_exercise.screens.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.utils.viewModelCreator

/**
 * Container for all screens in the app.
 */

class MainActivity : AppCompatActivity() {

    //view-model is used observing username to be displayed in the toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}