package com.ci.quye.http.api

import com.hjq.http.config.IRequestApi

class SearchMovieApi: IRequestApi {
    override fun getApi(): String {
        return "/apix/movie/search"
    }

    var keywords: String? = null
    var page: Int = 1
}