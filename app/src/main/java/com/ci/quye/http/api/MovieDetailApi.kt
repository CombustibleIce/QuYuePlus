package com.ci.quye.http.api

import com.hjq.http.config.IRequestApi

class MovieDetailApi: IRequestApi {
    override fun getApi(): String {
        return "/apix/movie/play"
    }

    var id: String? = null
}