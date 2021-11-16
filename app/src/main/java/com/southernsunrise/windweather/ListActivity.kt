package com.southernsunrise.windweather

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.get
import java.lang.StringBuilder
import com.southernsunrise.windweather.MainActivity.weatherTask as weatherTask1
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import androidx.core.view.isVisible
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class ListActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
            }
        }
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.list_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.title = "Add / Select the city"
        val listView = findViewById<ListView>(R.id.location_list)
        val locationNames = ArrayList<String>()
        val prefs = getSharedPreferences("PREFS", MODE_PRIVATE)
        val wholeCityNames = prefs.getString("city_names", "")
        val cityNames = wholeCityNames?.split(",")
        if (cityNames != null && cityNames.isNotEmpty()) {
            for (i in cityNames) {
                if (i.isNotBlank()) {
                    locationNames.add(i)
                }
            }
        }
        val hashSet = HashSet<String>()
        hashSet.addAll(locationNames)
        locationNames.clear()
        locationNames.addAll(hashSet)
        locationNames.sort()
        val adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_list_item_1, locationNames
        )
        listView.adapter = adapter
        val addButton = findViewById<FloatingActionButton>(R.id.fab_add)
        val editText: EditText = findViewById<EditText>(R.id.addCity_editText)

        addButton.setOnClickListener {

            if (editText.visibility == View.GONE) {
                editText.visibility = View.VISIBLE
                editText.requestFocus()
            } else {
                val text = editText.text.toString()
                val intent = Intent(this, MainActivity::class.java)
                val sharedPref: SharedPreferences =
                    getSharedPreferences("CITY_NAME_SAVE", MODE_PRIVATE)
                val sharedPrefEdit = sharedPref.edit()
                MainActivity.CITY = text
                sharedPrefEdit.putString("CITY_NAME", MainActivity.CITY)
                sharedPrefEdit.apply()
                locationNames.add(text)
                adapter.notifyDataSetChanged()
                val stringBuilder = StringBuilder()
                if (locationNames.size != 0) {
                    for (s in locationNames) {
                        stringBuilder.append(s)
                        stringBuilder.append(",")
                    }
                }
                val prefs = getSharedPreferences("PREFS", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = prefs.edit()
                editor.putString("city_names", stringBuilder.toString())
                editor.commit()

                Toast.makeText(
                    this,
                    sharedPref.getString("CITY_NAME", "armavir, am"),
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(intent)
            }
        }

        listView.onItemLongClickListener = OnItemLongClickListener { arg0, arg1, pos, arg3 ->
            Toast.makeText(this, "removed", Toast.LENGTH_LONG).show()
            locationNames.removeAt(pos)
            adapter.notifyDataSetChanged()
            val stringBuilder = StringBuilder()
            if (locationNames.size != 0) {
                for (s in locationNames) {
                    stringBuilder.append(s)
                    stringBuilder.append(",")
                }
            }
            val prefs = getSharedPreferences("PREFS", 0)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putString("city_names", stringBuilder.toString())
            editor.commit()

            true
        }
        listView.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                val text: String = listView.getItemAtPosition(position) as String
                val intent = Intent(this, MainActivity::class.java)
                MainActivity.CITY = text
                val sharedPref: SharedPreferences =
                    getSharedPreferences("CITY_NAME_SAVE", MODE_PRIVATE)
                val sharedPrefEdit = sharedPref.edit()
                sharedPrefEdit.putString("CITY_NAME", MainActivity.CITY)
                sharedPrefEdit.apply()
                Toast.makeText(
                    this,
                    sharedPref.getString("CITY_NAME", "armavir, am"),
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(intent)
            }

    }

}



