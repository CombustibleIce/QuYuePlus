package com.ci.quye.other

import java.nio.charset.Charset
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object Base64Util {

    @OptIn(ExperimentalEncodingApi::class)
    fun decode(src: ByteArray): String? {
        try {
            val bytes = Base64.decode(src, 0, src.size)
            return String(bytes, Charset.forName("utf-8"))
        }catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun decodeJsonStr(str: String): String? {
        return decode(str.replace("36aa8bb5b88a03d405bc1fd425d48a9f","").toByteArray())
    }
}