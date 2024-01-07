package com.ci.quye.http.model

import com.hjq.http.config.IRequestServer
import com.hjq.http.model.BodyType

class LineRequestServer : IRequestServer {

    override fun getHost(): String {
        return "http://an231218.oss-cn-shanghai.aliyuncs.com"
    }

    override fun getPath(): String {
        return ""
    }

    override fun getType(): BodyType {
        // 以表单的形式提交参数
        return BodyType.FORM
    }
}