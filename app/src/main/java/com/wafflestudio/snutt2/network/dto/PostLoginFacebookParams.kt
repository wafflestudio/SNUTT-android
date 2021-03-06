package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostLoginFacebookParams(
    @Json(name = "fb_id") val facebookId: String,
    @Json(name = "fb_token") val facebookToken: String
)
