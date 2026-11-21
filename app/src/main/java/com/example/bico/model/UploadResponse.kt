package com.example.bico.model

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("url") val url: String
)
