package com.example.cars24sdui.parser

import android.content.Context
import android.util.Log
import com.example.cars24sdui.model.Component
import com.example.cars24sdui.model.Screen
import org.json.JSONArray
import org.json.JSONObject

class JsonLoader(private val context: Context) {
    fun loadHome(): Result<Screen> = runCatching {
        val root = JSONObject(context.assets.open("home.json").bufferedReader().use { it.readText() })
        val version = root.optInt("schemaVersion", 1)
        Log.d("JsonLoader", "Loading SDUI Screen v$version")
        Screen(root.getJSONArray("components").toComponents())
    }

    private fun JSONArray.toComponents(): List<Component> = (0 until length()).map { index ->
        val item = getJSONObject(index)
        Component(item.getString("type"), item.optJSONObject("props")?.toMap().orEmpty())
    }

    private fun JSONObject.toMap(): Map<String, Any?> = keys().asSequence().associateWith { key ->
        when (val value = opt(key)) {
            is JSONObject -> value.toMap()
            is JSONArray -> value.toList()
            JSONObject.NULL -> null
            else -> value
        }
    }

    private fun JSONArray.toList(): List<Any?> = (0 until length()).map { index ->
        when (val value = opt(index)) {
            is JSONObject -> value.toMap()
            is JSONArray -> value.toList()
            JSONObject.NULL -> null
            else -> value
        }
    }
}
