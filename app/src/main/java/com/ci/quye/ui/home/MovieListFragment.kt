package com.ci.quye.ui.home

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.ci.quye.R
import com.ci.quye.databinding.FragmentMovieListBinding
import com.ci.quye.http.api.MovieListApi
import com.ci.quye.http.model.HttpListData
import com.ci.quye.ui.home.adapter.MovieListAdapter
import com.ci.quye.ui.home.model.MovieListItem
import com.ci.quye.ui.play.VideoPlayActivity
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import java.lang.Exception

class MovieListFragment : Fragment(), OnRefreshLoadMoreListener {
    private lateinit var binding: FragmentMovieListBinding
    private var lastVisibleItem = 0
    private lateinit var adapter: MovieListAdapter
    private var page: Int = 1
    private var type: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt("type")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = GridLayoutManager(context,2)
        adapter = MovieListAdapter()
        adapter.setListener(object : MovieListAdapter.OnItemClickListener {
            override fun onItemClick(item: MovieListItem) {
                if (!TextUtils.isEmpty(item.id) && activity != null) {
                    VideoPlayActivity.start(activity!!,item.id!!)
                }
            }

        })
        binding.recyclerView.adapter = adapter
        binding.refreshLayout.setOnRefreshLoadMoreListener(this)
        loadPageList()
    }

    private fun loadPageList() {
        val api = MovieListApi().apply {
            page = this@MovieListFragment.page
            type = this@MovieListFragment.type
        }
        EasyHttp.post(viewLifecycleOwner).api(api).request(object: OnHttpListener<HttpListData<MovieListItem>>{
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

    companion object {
        @JvmStatic
        fun newInstance(type: Int) =
            MovieListFragment().apply {
                arguments = Bundle().apply {
                    putInt("type",type)
                }
            }
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
}