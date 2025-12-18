package com.example.neogulmap

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NugulmapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the Kakao v2 SDK
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}