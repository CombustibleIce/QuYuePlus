package com.ci.quye.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ci.quye.databinding.ItemLayoutMovieBinding
import com.ci.quye.ui.home.model.MovieListItem

class MovieListAdapter: RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {

    public class ViewHolder(val binding: ItemLayoutMovieBinding): RecyclerView.ViewHolder(binding.root) {}

    private var mData: MutableList<MovieListItem>? = null
    private var listener: OnItemClickListener? = null

    fun setListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    fun addData(data: MutableList<MovieListItem>?) {
        if (mData == null) {
            mData = mutableListOf()
        }
        if (data != null) {
            var oldIndex: Int = mData!!.size - 1
            if (oldIndex < 0) {
                oldIndex = 0
            }
            mData?.addAll(data)
            notifyItemRangeChanged(oldIndex,data.size)
        }
    }

    fun clearData() {
        mData?.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemLayoutMovieBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        if (mData != null) {
            return mData!!.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData!![position]
        Glide.with(holder.binding.root.context)
            .load(item.cover)
            .into(holder.binding.cover)
        holder.binding.title.text = item.title
        holder.binding.root.setOnClickListener {
            listener?.onItemClick(item)
        }
    }

    public interface OnItemClickListener {
        fun onItemClick(item: MovieListItem)
    }
}