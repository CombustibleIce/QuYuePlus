package com.ci.quye.other

import androidx.lifecycle.LifecycleOwner
import com.ci.quye.http.api.GetTokenApi
import com.ci.quye.http.model.HttpData
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import java.lang.Exception

class TokenUserManager private constructor(){

    companion object {

        @Suppress("StaticFieldLeak")
        private val manager: TokenUserManager by lazy { TokenUserManager() }

        fun getInstance(): TokenUserManager {
            return manager
        }

        /**
         * 获取一个对象的独立无二的标记
         */
        private fun getObjectTag(`object`: Any): String {
            // 对象所在的包名 + 对象的内存地址
            return `object`.javaClass.name + Integer.toHexString(`object`.hashCode())
        }
    }

    private var deviceId: String? = null
    var tokenInfo: GetTokenApi.Bean? = null

    fun initDeviceId() {
        deviceId = System.currentTimeMillis().toString()
    }

    fun getDeviceId(): String? {
        return deviceId
    }
}