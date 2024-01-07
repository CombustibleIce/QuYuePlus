package com.ci.quye.http.api

import com.hjq.http.config.IRequestApi

class LineDoorApi: IRequestApi{
    var id: String = "0.0058147500517155"
    override fun getApi(): String {
        return "/a.json"
    }
}