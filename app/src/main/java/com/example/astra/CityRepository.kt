package com.example.astra.data

import android.content.Context
import com.example.astra.model.City
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CityRepository {

    private var cache: List<City>? = null

    fun loadCities(context: Context): List<City> {
        cache?.let { return it }

        val json = context.assets
            .open("cities.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<City>>() {}.type
        val loaded: List<City> = Gson().fromJson(json, type)

        cache = loaded
        return loaded
    }

    fun search(context: Context, query: String, limit: Int = 8): List<City> {
        if (query.isBlank()) return emptyList()

        val q = query.trim().lowercase()
        val all = loadCities(context)

        return all
            .filter {
                it.name.lowercase().startsWith(q) ||
                        it.asciiName.lowercase().startsWith(q)
            }
            .sortedByDescending { it.population }
            .take(limit)
            .ifEmpty {
                all.filter {
                    it.name.lowercase().contains(q) ||
                            it.asciiName.lowercase().contains(q)
                }
                    .sortedByDescending { it.population }
                    .take(limit)
            }
    }
}