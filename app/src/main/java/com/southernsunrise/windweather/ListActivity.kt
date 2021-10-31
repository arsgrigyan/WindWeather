package com.southernsunrise.windweather

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.get
import com.southernsunrise.windweather.MainActivity.weatherTask as weatherTask1


class ListActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.list_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.title = "Select the city"
        val listView = findViewById<ListView>(R.id.location_list)
        val locationNames = mutableListOf<String>(
            "Alexandria",
            "Allentown",
            "Amarillo",
            "Anaheim",
            "Anchorage",
            "Ann Arbor",
            "Antioch",
            "Apple Valley",
            "Appleton",
            "Arlington",
            "Arvada",
            "Asheville",
            "Athens",
            "Atlanta",
            "Atlantic City",
            "Augusta",
            "Aurora",
            "Austin",
            "Bakersfield",
            "Baltimore",
            "Barnstable",
            "Baton Rouge",
            "Beaumont",
            "Bel Air",
            "Bellevue",
            "Berkeley",
            "Bethlehem",
            "Billings",
            "Birmingham",
            "Bloomington",
            "Boise",
            "Boise City",
            "Bonita Springs",
            "Boston",
            "Boulder",
            "Bradenton",
            "Bremerton",
            "Bridgeport",
            "Brighton",
            "Brownsville",
            "Bryan",
            "Buffalo",
            "Burbank",
            "Burlington",
            "Cambridge",
            "Canton",
            "Cape Coral",
            "Carrollton",
            "Cary",
            "Cathedral City",
            "Cedar Rapids",
            "Champaign",
            "Chandler",
            "Charleston",
            "Charlotte",
            "Chattanooga",
            "Chesapeake",
            "Moscow"
        )

        val adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_list_item_1, locationNames
        )
        listView.adapter = adapter
        val searchButton = findViewById<FloatingActionButton>(R.id.fab_search)
        searchButton.setOnClickListener {
            intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        listView.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                val text: String = listView.getItemAtPosition(position) as String
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("city_name", text.toString())
                MainActivity.CITY = text
                startActivity(intent)

            }
    }
}








