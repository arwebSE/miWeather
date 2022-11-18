package dev.arweb.miweather

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class MiWeatherWidget : AppWidgetProvider() {
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

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        // On receive intent broadcast
        val action = intent!!.action ?: ""

        if (context != null && action == "refresh") {
            Log.d("MIWEATHER", "got increase broadcast")
            // refresh weather data

            val views = RemoteViews(context.packageName, R.layout.mi_weather_widget)
            //clock = views.

            //Log.d("MIWEATHER", "Time: "+clock.getText())

            // update widgets
            updateWidgets(context)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created

    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    // update all widgets
    private fun updateWidgets(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, javaClass))
        // update each widget
        ids.forEach { id -> updateAppWidget(context, manager, id) }
    }

    private fun pendingIntent(
        context: Context?,
        action: String
    ): PendingIntent? {
        val intent = Intent(context, javaClass)
        intent.action = action

        // return pending intent
        return PendingIntent.getBroadcast(
            context, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        Log.d("MIWEATHER", "updating app widget")

        // get widget text from shared prefs
        //val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        //val widgetText = prefs.getString("widgetText", "0")

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.mi_weather_widget)
        //views.setTextViewText(R.id.textViewTime, currentTime)

        // launch pending intent to increase value stored in shared prefs
        views.setOnClickPendingIntent(R.id.refreshButton, pendingIntent(context, "refresh"))

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}



