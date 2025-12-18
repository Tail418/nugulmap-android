package com.example.neogulmap

import android.app.Application
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.HiltAndroidApp
import java.security.MessageDigest

@HiltAndroidApp
class NugulmapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Log the configured key (masked)
        val key = BuildConfig.KAKAO_NATIVE_APP_KEY
        Log.d("KakaoSetup", "Configured Kakao Key: ${key.take(4)}****")
        
        // Init SDK
        try {
            KakaoMapSdk.init(this, key)
            Log.d("KakaoSetup", "KakaoMapSdk initialized")
        } catch (e: Exception) {
            Log.e("KakaoSetup", "Failed to init KakaoMapSdk", e)
        }

        // Print Key Hash
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hash = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                Log.e("KakaoSetup", "YOUR KEY HASH: $hash")
            }
        } catch (e: Exception) {
            Log.e("KakaoSetup", "Failed to get Key Hash", e)
        }
    }
}