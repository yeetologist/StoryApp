package com.github.yeetologist.storyapp.view.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.yeetologist.storyapp.data.remote.response.ListStoryItem
import com.github.yeetologist.storyapp.databinding.ItemStoryBinding

class MainAdapter : ListAdapter<ListStoryItem, MainAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(detailResult: ListStoryItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listResult: ListStoryItem) {
            binding.tvTitle.text = listResult.name
            Glide.with(binding.root)
                .load(listResult.photoUrl)
                .into(binding.ivList)
            binding.tvDescription.text = listResult.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val result = getItem(position)
        holder.bind(result)
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(result)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
