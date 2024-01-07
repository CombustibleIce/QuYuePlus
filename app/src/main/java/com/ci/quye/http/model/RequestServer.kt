package com.ci.quye.http.model

import com.hjq.http.config.IRequestServer
import com.hjq.http.model.BodyType

class RequestServer : IRequestServer {
    var baseUrl: String? = null

    override fun getHost(): String {
        return "$baseUrl"
    }

    override fun getPath(): String {
        return ""
    }

    override fun getType(): BodyType {
        // 以表单的形式提交参数
        return BodyType.FORM
    }
}