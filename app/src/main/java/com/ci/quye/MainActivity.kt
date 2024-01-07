package com.ci.quye

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.ci.quye.databinding.ActivityMainBinding
import com.ci.quye.http.api.GetTokenApi
import com.ci.quye.http.api.LineDoorApi
import com.ci.quye.http.model.HttpData
import com.ci.quye.http.model.LineRequestServer
import com.ci.quye.http.model.RequestHandler
import com.ci.quye.http.model.RequestServer
import com.ci.quye.other.TokenUserManager
import com.ci.quye.ui.home.MovieListFragment
import com.ci.quye.ui.home.SearchMovieActivity
import com.hjq.http.EasyConfig
import com.hjq.http.EasyHttp
import com.hjq.http.config.IRequestApi
import com.hjq.http.listener.OnHttpListener
import com.hjq.http.model.HttpHeaders
import com.hjq.http.model.HttpParams
import com.hjq.toast.ToastUtils
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FragmentPagerAdapter
    private val tabs: MutableList<MyTab> = mutableListOf()
    private val pages: MutableList<Fragment> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initClient()
    }

    private fun initClient() {
        // 网络请求框架初始化
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .build()
        EasyConfig.with(okHttpClient)
            // 是否打印日志
            .setLogEnabled(false)
            // 设置服务器配置
            .setServer(LineRequestServer())
            // 设置请求处理策略
            .setHandler(RequestHandler(application))
            // 设置请求重试次数
            .setRetryCount(1)
            .setInterceptor { api: IRequestApi, params: HttpParams, headers: HttpHeaders ->
                // 添加全局请求头
                val deviceId = TokenUserManager.getInstance().getDeviceId()
                val userId: String? = TokenUserManager.getInstance().tokenInfo?.user_id
                val token: String? = TokenUserManager.getInstance().tokenInfo?.token
                if (!TextUtils.isEmpty(deviceId)) {
                    headers.put("device_id",deviceId)
                }
                if (!TextUtils.isEmpty(userId)) {
                    headers.put("user_id", userId)
                }
                if (!TextUtils.isEmpty(token)) {
                    headers.put("token", token)
                }
            }
            .into()
        EasyHttp.get(this).api(LineDoorApi()).request(object : OnHttpListener<JSONObject> {
            override fun onSucceed(result: JSONObject?) {
                if (result != null) {
                    val apiArray: JSONArray? = result.getJSONArray("api")
                    if (apiArray != null && apiArray.length() > 0) {
                        val apiObj: JSONObject? = apiArray.getJSONObject(apiArray.length() - 1)
                        val url = apiObj?.getString("address")
                        if (!TextUtils.isEmpty(url)) {
                            initClientWithUrl(url!!)
                        }
                    }
                }
            }

            override fun onFail(e: Exception?) {
                e?.printStackTrace()
            }

        })
    }

    private fun initClientWithUrl(url: String) {
        // 网络请求框架初始化
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .build()
        EasyConfig.with(okHttpClient)
            // 是否打印日志
            .setLogEnabled(false)
            // 设置服务器配置
            .setServer(RequestServer().apply {
                baseUrl = url
            })
            // 设置请求处理策略
            .setHandler(RequestHandler(application))
            // 设置请求重试次数
            .setRetryCount(1)
            .setInterceptor { api: IRequestApi, params: HttpParams, headers: HttpHeaders ->
                // 添加全局请求头
                val deviceId = TokenUserManager.getInstance().getDeviceId()
                val userId: String? = TokenUserManager.getInstance().tokenInfo?.user_id
                val token: String? = TokenUserManager.getInstance().tokenInfo?.token
                if (!TextUtils.isEmpty(deviceId)) {
                    headers.put("device_id",deviceId)
                }
                if (!TextUtils.isEmpty(userId)) {
                    headers.put("user_id", userId)
                }
                if (!TextUtils.isEmpty(token)) {
                    headers.put("token", token)
                }
            }
            .into()
        prepare()
    }

    private fun initLayout() {
        tabs.clear()
        tabs.add(MyTab("推荐",1))
        tabs.add(MyTab("最新",2))
        tabs.add(MyTab("国产",3))
        tabs.add(MyTab("日韩",4))
        tabs.add(MyTab("欧美",5))
        tabs.forEach {
            binding.tabLayout.addTab(binding.tabLayout.newTab().apply {
                text = it.title
            })
            pages.add(MovieListFragment.newInstance(it.type))
        }

        adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getCount(): Int {
                return pages.size
            }

            override fun getItem(position: Int): Fragment {
                return pages[position]
            }

        }
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        for (index in 0..<tabs.size) {
            binding.tabLayout.getTabAt(index)?.text = tabs[index].title
        }
        binding.search.setOnClickListener {
            SearchMovieActivity.start(this)
        }
    }

    private fun prepare() {
        TokenUserManager.getInstance().initDeviceId()
        EasyHttp.post(this).api(GetTokenApi()).request(object :
            OnHttpListener<HttpData<GetTokenApi.Bean>> {
            override fun onSucceed(result: HttpData<GetTokenApi.Bean>?) {
                if (result?.getData() != null) {
                    TokenUserManager.getInstance().tokenInfo = result.getData()
                    initLayout()
                } else {
                    ToastUtils.show("初始化异常，请退出重试")
                }
            }

            override fun onFail(e: Exception?) {

            }
        })
    }

    class MyTab(var title: String?, var type: Int) {

    }
}