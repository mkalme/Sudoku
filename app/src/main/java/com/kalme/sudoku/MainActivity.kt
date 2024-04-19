package com.kalme.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

// The main entry point of the app can be considered 'SudokuView'. Everything is drawn there
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
    }
}