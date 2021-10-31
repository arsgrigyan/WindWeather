package com.southernsunrise.windweather

import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class SearchActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

    }
}