package com.ci.quye

import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.ci.quye.http.model.RequestHandler
import com.ci.quye.http.model.RequestServer
import com.ci.quye.manager.ActivityManager
import com.ci.quye.other.MaterialHeader
import com.ci.quye.other.SmartBallPulseFooter
import com.ci.quye.other.ToastStyle
import com.ci.quye.other.TokenUserManager
import com.hjq.http.EasyConfig
import com.hjq.http.config.IRequestApi
import com.hjq.http.model.HttpHeaders
import com.hjq.http.model.HttpParams
import com.hjq.toast.ToastUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.tencent.mmkv.MMKV
import okhttp3.OkHttpClient

class AppApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        initSdks()
    }

    private fun initSdks() {
        ActivityManager.getInstance().init(this)
        // MMKV 初始化
        MMKV.initialize(this)
        // 初始化吐司
        ToastUtils.init(this, ToastStyle())
        // 设置全局的 Header 构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator{ context: Context, layout: RefreshLayout ->
            MaterialHeader(context).setColorSchemeColors(ContextCompat.getColor(context, R.color.common_accent_color))
        }
        // 设置全局的 Footer 构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator{ context: Context, layout: RefreshLayout ->
            SmartBallPulseFooter(context)
        }
        // 设置全局初始化器
        SmartRefreshLayout.setDefaultRefreshInitializer { context: Context, layout: RefreshLayout ->
            // 刷新头部是否跟随内容偏移
            layout.setEnableHeaderTranslationContent(true)
                // 刷新尾部是否跟随内容偏移
                .setEnableFooterTranslationContent(true)
                // 加载更多是否跟随内容偏移
                .setEnableFooterFollowWhenNoMoreData(true)
                // 内容不满一页时是否可以上拉加载更多
                .setEnableLoadMoreWhenContentNotFull(false)
                // 仿苹果越界效果开关
                .setEnableOverScrollDrag(false)
        }
    }
}