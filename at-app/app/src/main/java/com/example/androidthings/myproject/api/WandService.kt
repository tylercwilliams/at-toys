package com.example.androidthings.myproject.api

import com.example.androidthings.myproject.api.model.AccelData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface WandService {
    @POST("/")
    fun sendData(@Body body: AccelData): Call<AccelData>
}