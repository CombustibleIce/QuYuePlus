package com.ci.quye.http.api

import com.hjq.http.config.IRequestApi

class MovieListApi: IRequestApi {
    override fun getApi(): String {
        return "/apix/movie/index"
    }

    var type: Int? = null
    var sub_category_id: Int = 1
    var page: Int = 1
}