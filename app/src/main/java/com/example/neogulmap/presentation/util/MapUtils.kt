package com.example.neogulmap.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object MapUtils {
    fun openKakaoMap(context: Context, lat: Double, lng: Double) {
        val url = "kakaomap://look?p=$lat,$lng"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Open Play Store or Web
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=net.daum.android.map"))
            context.startActivity(marketIntent)
        }
    }
}
