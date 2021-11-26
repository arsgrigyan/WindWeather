package com.southernsunrise.windweather

import android.Manifest
import android.accounts.AccountManager.get
import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.text.Layout
import android.view.*
import android.webkit.WebView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewTreeViewModelStoreOwner.get
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.nio.file.Paths.get
import java.security.AccessController.getContext
import java.text.DecimalFormat
import kotlin.math.roundToLong


class MainActivity : AppCompatActivity() {

    val APIKEY = BuildConfig.API_KEY

    companion object {
        var CITY = "Yerevan, am"
        var UNITS = "metric"
        var TEMP_UNIT = "°C"
        var windWithMeters = false
        var windWithMiles = false
        var pressure_mmHg = false
        var pressure_inHg = false

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<ImageButton>(R.id.btn_add).setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
     
        // change city name to the last saved city name
        val sharedPref: SharedPreferences = getSharedPreferences("CITY_NAME_SAVE", MODE_PRIVATE)
        val cityName = sharedPref.getString("CITY_NAME", CITY)
        if (cityName != null) {
            CITY = cityName
        }
        
        //getting default shared preferences to show descriptions in appropriate units
        
        // change temperature units' boolean variable depending on deafault shared preferences from settings
        val sharedPreference: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        val tempValue = sharedPreference.getString("TEMPERATURE_UNIT", "1")
        when (tempValue?.toInt()) {
            1 -> {
                UNITS = "metric"
                TEMP_UNIT = "°C"
            }
            else -> {
                UNITS = "imperial"
                TEMP_UNIT = "°F"
            }
        }
        
         // change wind speed units' boolean variable depending on deafault shared preferences from settings
        val windSpeed = sharedPreference.getString("WIND_UNIT", "1")
        when (windSpeed?.toInt()) {
            1 -> {
                windWithMeters = true
                windWithMiles = false
            }
            else -> {
                windWithMiles = true
                windWithMeters = false
            }

        }
        
         // change pressure units' boolean variable depending on deafault shared preferences from settings
        val pressureUnit = sharedPreference.getString("PRESSURE_UNIT", "1")
        when (pressureUnit?.toInt()) {
            1 -> {
                pressure_inHg = false
                pressure_mmHg = false
            }
            2 -> {
                pressure_mmHg = true
                pressure_inHg = false
            }
            3 -> {
                pressure_inHg = true
                pressure_mmHg = false
            }

        }
        weatherTask(CITY).execute()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.opt_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.opt_share -> {
                val sharedDescriptions =
                    "   ${findViewById<TextView>(R.id.address).text.toString()}\n temperature - ${
                        findViewById<TextView>(
                            R.id.temperature
                        ).text
                    }\n minimum - ${
                        findViewById<TextView>(R.id.temperature_min).text
                    }\n maximum - ${findViewById<TextView>(R.id.temperature_max).text}\n Humidity - ${
                        findViewById<TextView>(
                            R.id.humidity
                        ).text
                    }\n pressure - ${findViewById<TextView>(R.id.pressure).text}\n Wind - ${
                        findViewById<TextView>(
                            R.id.wind
                        ).text
                    } "
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, sharedDescriptions)
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "Share to:"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

      // the class wich will request and get all the resources from API
    inner class weatherTask(var city: String = "Yerevan") : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            //findViewById<ImageView>(R.id.app_icon).visibility = View.VISIBLE
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorMessage).visibility = View.GONE
        }

        override fun doInBackground(vararg p0: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=$UNITS&appid=$APIKEY")
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
                var temp = main.getString("temp").toDouble().roundToInt().toString() + TEMP_UNIT
                var tempMin =
                    "↓ " + main.getString("temp_min").toDouble().roundToInt().toString() + TEMP_UNIT
                var tempMax =
                    "↑ " + main.getString("temp_max").toDouble().roundToInt().toString() + TEMP_UNIT
                var pressure = main.getDouble("pressure")
                val humidity = main.getString("humidity")
                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                var windSpeed = wind.getDouble("speed")
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name")
                val iconCode = weather.getString("icon")
                val iconUrl = URL("http://openweathermap.org/img/w/$iconCode.png")
                val imageView: ImageView = findViewById(R.id.weatherIcon)
                
                // change units according to boolean unit variables 
                if (UNITS == "metric" && windWithMiles) {
                    windSpeed = wind.getDouble("speed") / 0.44704
                } else if (UNITS == "metric" && windWithMeters) {
                    windSpeed = wind.getDouble("speed")
                }

                if (UNITS == "imperial" && windWithMeters) {
                    windSpeed = (wind.getDouble("speed") * 0.44704)
                } else if (UNITS == "imperial" && windWithMiles) {
                    windSpeed = wind.getDouble("speed")
                }
                
                pressure = when {
                    pressure_inHg -> {
                        (main.getDouble("pressure")) * 0.029529983071445
                    }
                    pressure_mmHg -> {
                        (main.getDouble("pressure")) * 0.750061683
                    }
                    else -> main.getDouble("pressure")
                }

                /* Picasso.get().load("https://openweathermap.org/img/w/$iconCode.png")
                     .into(imageView)*/
                  // using weather description icons from drawable folder depending on icon code from API   
                val iconsId: Int =
                    getResources().getIdentifier("w$iconCode", "drawable", getPackageName());
                val backgroundsId = getResources().getIdentifier("b$iconCode", "drawable", getPackageName());
                imageView.setImageResource(iconsId)
                findViewById<RelativeLayout>(R.id.main).setBackgroundResource(backgroundsId)

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
                findViewById<TextView>(R.id.wind).text = DecimalFormat("##.##").format(windSpeed)
                findViewById<TextView>(R.id.pressure).text = DecimalFormat("##").format(pressure)
                findViewById<TextView>(R.id.humidity).text = humidity

                // findViewById<ImageView>(R.id.app_icon).visibility = View.GONE
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
                findViewById<Toolbar>(R.id.toolbar).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorMessage).visibility = View.VISIBLE
                findViewById<TextView>(R.id.address).visibility = View.GONE
                findViewById<TextView>(R.id.updated_at).visibility = View.GONE
            }

        }
    }
}

