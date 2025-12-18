package com.example.neogulmap.presentation.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri // Add this line
import androidx.core.content.ContextCompat

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

    fun vectorToBitmap(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun createColoredMarkerBitmap(context: Context, drawableId: Int, color: Int): Bitmap? {
        val drawable: Drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN) // Apply color tint
        
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun createRedMarkerBitmap(context: Context): Bitmap {
        val size = 50 
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = android.graphics.Paint()
        paint.color = android.graphics.Color.RED
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        return bitmap
    }
}
