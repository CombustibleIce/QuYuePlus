package com.ci.quye.http.model

import com.ci.quye.http.model.HttpListData.ListBean

class HttpListData<T> : HttpData<ListBean<T>?>() {

    class ListBean<T> {

        /** 数据 */
        private var list: MutableList<T>? = null

        fun getList(): MutableList<T>? {
            return list
        }
    }
}