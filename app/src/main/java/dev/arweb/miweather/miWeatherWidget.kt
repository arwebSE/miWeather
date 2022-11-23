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
import com.beust.klaxon.Klaxon
import okhttp3.*
import java.io.IOException
import java.time.LocalDateTime

/**
 * Implementation of App Widget functionality.
 */
class MiWeatherWidget : AppWidgetProvider() {

    private val httpClient = OkHttpClient()

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        Toast.makeText(context, "miWeather created", Toast.LENGTH_LONG).show()
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
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

        val rez = apiCall(context)
        views.setTextViewText(R.id.cTempView, rez)

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
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT  or PendingIntent.FLAG_MUTABLE
        )
    }


    private fun apiCall(context: Context): String {
        Log.d("MIWEATHER", "inside apiCall")

        val apiUrl = "https://miel-api.arwebse.repl.co"
        val city = "stockholm"
        val verify = "5ecf486e3b8d878a4a87"
        val freedom = false
        val id = "android"

        val url = "$apiUrl/weather?q=$city&verify=$verify&freedom=$freedom&id=$id";

        val request = Request.Builder()
            .url(url)
            .build()

        val views = RemoteViews(context.packageName, R.layout.mi_weather_widget)
        views.setTextViewText(R.id.cTempView, "asdd")

        //updateWidgets(context)

        return "test"

        Log.d("MIWEATHER", "set cTempView to 0")

//        httpClient.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                response.use {
//                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
////                    for ((name, value) in response.headers) {
////                        println("$name: $value")
////                    }
//
//                    Log.d("MIWEATHER", "got successfull response!")
//
//                    val jsonData = response.body!!.string()
//                    val rezz = Klaxon().parse<Any>(jsonData)
//
//                    //Log.d("MIWEATHER", "result: ${rezz.geo!!}") <<--- ENDED HERE FMLasd
//
//                    //println(response.body!!.string())
//
//                    //el.cTemp.innerHTML = Math.round(res.current.temp);
//                    // get widget text from shared prefs
//                    //val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
//                    //val tempString = prefs.getString("temperature", "x")
//
//                    val views = RemoteViews(context.packageName, R.layout.mi_weather_widget)
//                    views.setTextViewText(R.id.cTempView, "0dasdasd")
//                    //updateWidgets(context)
//
//                    // Instruct the widget manager to update the widget
//                    appWidgetManager.updateAppWidget(appWidgetId, views)
//
//                    Log.d("MIWEATHER", "set cTempView to 0")
//                }
//            }
//        })
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



