package com.example.androidthings.myproject.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AccelData(
        @SerializedName("x") @Expose val x: Float,
        @SerializedName("y") @Expose val y: Float,
        @SerializedName("z") @Expose val z: Float)