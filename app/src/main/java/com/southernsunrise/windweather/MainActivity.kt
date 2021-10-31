package com.southernsunrise.windweather

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import com.bumptech.glide.Glide
import kotlin.collections.ArrayList
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.PersistableBundle
import android.util.Log

import android.view.View.OnFocusChangeListener


class MainActivity : AppCompatActivity() {
    val API = "9050c9ea0f93648270bdad645e8eff47"

    companion object {
        var CITY = "Armavir, am"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<ImageButton>(R.id.btn_add).setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }

        weatherTask(CITY).execute()

    }

/*
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString("city_name", CITY)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        CITY = savedInstanceState.getString("city_name").toString()
    }*/

    inner class weatherTask(var city: String = "armavir, am") : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorMessage).visibility = View.GONE
        }

        override fun doInBackground(vararg p0: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$API")
                        .readText(Charsets.UTF_8)
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText =
                    "Updated at " + SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                        Date(updatedAt * 1000)
                    )
                val temp = main.getString("temp").toDouble().roundToInt().toString() + "°C"
                val tempMin =
                    "↓ " + main.getString("temp_min").toDouble().roundToInt().toString() + "°C"
                val tempMax =
                    "↑ " + main.getString("temp_max").toDouble().roundToInt().toString() + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name")
                val iconCode = weather.getString("icon")





                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text = updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                findViewById<TextView>(R.id.temperature).text = temp
                findViewById<TextView>(R.id.temperature_min).text = tempMin
                findViewById<TextView>(R.id.temperature_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                        Date(sunrise * 1000)
                    )
                findViewById<TextView>(R.id.sunset).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE


            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorMessage).visibility = View.VISIBLE

            }

        }
    }
}