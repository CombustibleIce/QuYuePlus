package com.ci.quye.http.api

import com.hjq.http.config.IRequestApi

class GetTokenApi : IRequestApi{

    override fun getApi(): String {
        return "/apix/app/init_userinfo"
    }

    public class Bean {
        var user_id: String? = null
        var token: String? = null
    }
}