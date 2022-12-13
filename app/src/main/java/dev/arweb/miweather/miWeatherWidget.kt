package dev.arweb.miweather

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import java.io.IOException
import java.time.LocalDateTime

/**
 * Implementation of App Widget functionality.
 */
@JsonClass(generateAdapter = true)
class MiWeatherWidget : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        Toast.makeText(context, "miWeather created", Toast.LENGTH_LONG).show()
        updateWidgets(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    // update all widgets
    private fun updateWidgets(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, javaClass))
        // update each widget
        ids.forEach { id -> updateAppWidget(context, manager, id) }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        Log.d("MIWEATHER", "updating app widget")
        //Toast.makeText(context, "Updating miWeather", Toast.LENGTH_LONG).show()

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.mi_weather_widget)


        apiCall(context)

        // launch pending intent to increase value stored in shared prefs
        views.setOnClickPendingIntent(R.id.refreshButton, pendingIntent(context, "refresh"))

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun pendingIntent(context: Context?, action: String): PendingIntent? {
        val intent = Intent(context, javaClass)
        intent.action = action

        // return pending intent
        return PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun updateTemp(context: Context) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.mi_weather_widget)
        views.setTextViewText(R.id.cTempView, "pidar")
    }

    private fun apiCall(context: Context) {
        Log.d("MIWEATHER", "inside apiCall")

        val apiUrl = "https://miel-api.arwebse.repl.co"
        val city = "stockholm"
        val verify = "5ecf486e3b8d878a4a87"
        val freedom = false
        val id = "android"

        val url = "$apiUrl/weather?q=$city&verify=$verify&freedom=$freedom&id=$id";

        val request = Request.Builder().url(url).build()

        Log.d("MIWEATHER", "set cTempView to 0")

        val okHttpClient = OkHttpClient()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

//                    for ((name, value) in response.headers) {
//                        println("$name: $value")
//                    }

                    Log.d("MIWEATHER", "got successful response!")

                    // Parse the JSON string into an object
                    val jsonData = response.body!!.string()
                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val jsonAdapter = moshi.adapter(Any::class.java)
                    val weatherData = jsonAdapter.fromJson(jsonData)

                    // Create a JsonReader instance
                    val newmoshi = Moshi.reader
                    val jsonReader = moshi.reader().apply { isLenient = true }

                    // Parse the nested object directly
                    jsonReader.beginObject()
                    jsonReader.nextName()
                    val nestedObject = jsonReader.readJsonValue()

                    // Access the object of an object
                    Log.d("MIWEATHER", "got temp: ${nestedObject.current.temp}")

                    //el.cTemp.innerHTML = Math.round(res.current.temp);

                    // get widget text from shared prefs
                    val prefs =
                        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
                    val tempString = prefs.getString("temperature", "x")

                }

            }
        })
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        // On receive intent broadcast
        val action = intent!!.action ?: ""

        if (context != null && action == "refresh") {
            Log.d("MIWEATHER", "got refresh request")
            // refresh weather data

            val views = RemoteViews(context.packageName, R.layout.mi_weather_widget)
            views.setInt(
                R.id.refreshButton, "setImageDrawable",
                R.drawable.ic_launcher_foreground
            )
            views.setInt(R.id.refreshButton, "setImageAlpha", 50)

            val cTime = LocalDateTime.now()
            Log.d("MIWEATHER", "Time: $cTime")

            // update widgets
            updateWidgets(context)
        }
    }

}
