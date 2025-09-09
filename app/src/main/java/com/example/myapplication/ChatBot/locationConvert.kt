package com.example.myapplication.ChatBot

import PlacesResponse
import Place
import android.util.Log
import com.example.myapplication.BuildConfig
import gps
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun fetchNearbyRepairShops(
    latitude: Double,
    longitude: Double,
    onResult: (List<Place>) -> Unit, // 콜백 추가
    onError: (String) -> Unit // 에러 처리 콜백 추가
) {
    val location = "$latitude,$longitude"
    val radius = 10000 // 10km 반경
    val type = "car_repair"
    val apiKey = BuildConfig.GPS_API_KEY

    val rankby = "distance"
    val language = "ko"

    gps.getNearbyPlaces(location, rankby, type, language, apiKey).enqueue(object : Callback<PlacesResponse> {
        override fun onResponse(call: Call<PlacesResponse>, response: Response<PlacesResponse>) {
            if (response.isSuccessful) {
                response.body()?.results?.let{
                        places ->
                    if (places.isNotEmpty()) {
                        places.forEach { place ->
                            Log.d("RepairShop", "Name: ${place.name}, Address: ${place.vicinity}")
                        }
                    } else {
                        Log.d("RepairShop", "No repair shops found in the area.")
                    }
                }
                val places = response.body()?.results?.take(5) ?: emptyList()

                onResult(places) // 성공 시 콜백 호출
            } else {
                onError("API Error: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<PlacesResponse>, t: Throwable) {
            onError("Network Error: ${t.message}")
        }
    })
}

