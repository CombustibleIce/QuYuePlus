package com.ci.quye.ui.home

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.GridLayoutManager
import com.ci.quye.R
import com.ci.quye.databinding.ActivitySearchMovieBinding
import com.ci.quye.http.api.MovieListApi
import com.ci.quye.http.api.SearchMovieApi
import com.ci.quye.http.model.HttpListData
import com.ci.quye.ui.home.adapter.MovieListAdapter
import com.ci.quye.ui.home.model.MovieListItem
import com.ci.quye.ui.play.VideoPlayActivity
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import com.hjq.toast.ToastUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import java.lang.Exception

class SearchMovieActivity : AppCompatActivity(), OnRefreshLoadMoreListener {
    private lateinit var binding: ActivitySearchMovieBinding
    private lateinit var adapter: MovieListAdapter
    private var page: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        adapter = MovieListAdapter()
        adapter.setListener(object : MovieListAdapter.OnItemClickListener {
            override fun onItemClick(item: MovieListItem) {
                if (!TextUtils.isEmpty(item.id)) {
                    VideoPlayActivity.start(this@SearchMovieActivity,item.id!!)
                }
            }

        })
        binding.recyclerView.adapter = adapter
        binding.refreshLayout.setOnRefreshLoadMoreListener(this)
        binding.input.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
                val keyword = binding.input.text.toString().trim()
                if (TextUtils.isEmpty(keyword)) {
                    ToastUtils.show("请输入关键词")
                    return false
                }
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    page = 1
                    adapter.clearData()
                    loadPageList()
                }
                return false
            }
        })
        binding.clear.setOnClickListener {
            binding.input.setText("")
            adapter.clearData()
        }
        binding.cancel.setOnClickListener {
            finish()
        }
    }

    private fun loadPageList() {
        val api = SearchMovieApi().apply {
            page = this@SearchMovieActivity.page
            keywords = binding.input.editableText.toString()
        }
        EasyHttp.post(this).api(api).request(object:
            OnHttpListener<HttpListData<MovieListItem>> {
            override fun onSucceed(result: HttpListData<MovieListItem>?) {
                adapter.addData(result?.getData()?.getList())
                if (page == 1) {
                    binding.refreshLayout.finishRefresh()
                    if (adapter.itemCount < 10) {
                        binding.refreshLayout.setNoMoreData(true)
                    } else {
                        page ++
                        loadPageList()
                    }
                } else {
                    binding.refreshLayout.finishLoadMore()
                    if (adapter.itemCount < 10) {
                        binding.refreshLayout.setNoMoreData(true)
                    } else {
                        page ++
                        loadPageList()
                    }
                }
            }

            override fun onFail(e: Exception?) {
                e?.printStackTrace()
            }
        })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = 1
        adapter.clearData()
        loadPageList()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        page ++
        loadPageList()
    }

    companion object {
        @JvmStatic
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity,SearchMovieActivity::class.java))
        }
    }
}