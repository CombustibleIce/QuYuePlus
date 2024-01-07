package com.ci.quye.ui.play

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ci.quye.databinding.ActivityVideoPlayBinding
import com.ci.quye.http.api.GetTokenApi
import com.ci.quye.http.api.MovieDetailApi
import com.ci.quye.http.model.HttpData
import com.ci.quye.other.TokenUserManager
import com.ci.quye.ui.home.model.MovieDetailBean
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import com.hjq.toast.ToastUtils
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import java.lang.Exception


class VideoPlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayBinding

    companion object {
        @JvmStatic
        private val PARAM_MOVIE_ID: String = "PARAM_MOVIE_ID"
        @JvmStatic
        fun start(context: Context,id: String) {
            val intent = Intent(context, VideoPlayActivity::class.java)
            intent.putExtra(PARAM_MOVIE_ID,id)
            context.startActivity(intent)
        }
    }

    private var id: String? = null
    private lateinit var orientationUtils: OrientationUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        id = intent.getStringExtra(PARAM_MOVIE_ID)
        if (TextUtils.isEmpty(id)) return
        loadMovieDetail(id!!)
    }

    private fun playWithUrl(url: String) {
        binding.videoPlayer.setUp(url,false, null)
        binding.videoPlayer.titleTextView.visibility = View.VISIBLE
        binding.videoPlayer.backButton.visibility = View.VISIBLE
        binding.videoPlayer.backButton.setOnClickListener {
            onBackPressed()
        }
        orientationUtils = OrientationUtils(this,binding.videoPlayer)
        binding.videoPlayer.fullscreenButton.setOnClickListener {
            orientationUtils.resolveByClick()
        }
        binding.videoPlayer.setIsTouchWiget(true)
        binding.videoPlayer.isNeedOrientationUtils = false
        binding.videoPlayer.startPlayLogic()
    }

    private fun loadMovieDetail(id: String) {
        val api = MovieDetailApi().also {
            it.id = id
        }
        EasyHttp.post(this).api(api).request(object : OnHttpListener<HttpData<MovieDetailBean>> {
            override fun onSucceed(result: HttpData<MovieDetailBean>?) {
                if (result?.getData() != null) {
                    if (!TextUtils.isEmpty(result.getData()?.url)) {
                        playWithUrl(result.getData()?.url!!)
                    } else {
                        prepare()
                    }
                }
            }

            override fun onFail(e: Exception?) {
                ToastUtils.show("获取失败")
            }
        })
    }

    private fun prepare() {
        TokenUserManager.getInstance().initDeviceId()
        EasyHttp.post(this).api(GetTokenApi()).request(object :
            OnHttpListener<HttpData<GetTokenApi.Bean>> {
            override fun onSucceed(result: HttpData<GetTokenApi.Bean>?) {
                if (result?.getData() != null) {
                    TokenUserManager.getInstance().tokenInfo = result.getData()
                    loadMovieDetail(id!!)
                }
            }

            override fun onFail(e: Exception?) {

            }
        })
    }

    override fun onPause() {
        super.onPause()
        binding.videoPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        binding.videoPlayer.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        if (orientationUtils != null) {
            orientationUtils.releaseListener()
        }
    }

    override fun onBackPressed() {
        binding.videoPlayer.setVideoAllCallBack(null)
        super.onBackPressed()
    }
}