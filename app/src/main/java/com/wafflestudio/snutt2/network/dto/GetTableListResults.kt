package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// to List
@JsonClass(generateAdapter = true)
data class GetTableListResults(
    @Json(name = "year") val year: Long,
    @Json(name = "semester") val semester: Long,
    @Json(name = "title") val title: String,
    @Json(name = "_id") val _id: String,
    @Json(name = "updated_at") val updatedAt: String
)
