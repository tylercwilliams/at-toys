package com.example.androidthings.myproject.api.model

import com.google.gson.annotations.SerializedName

data class AccelData(
        @SerializedName("x") val x: Float,
        @SerializedName("y") val y: Float,
        @SerializedName("z") val z: Float)