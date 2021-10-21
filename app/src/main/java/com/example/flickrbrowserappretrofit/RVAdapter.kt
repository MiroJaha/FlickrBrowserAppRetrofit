package com.example.flickrbrowserappretrofit

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickrbrowserappretrofit.databinding.ImagesViewBinding

class RVAdapter (private var list: ArrayList<Data>, private val classNumber: Int): RecyclerView.Adapter<RVAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: ImagesViewBinding, listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    private lateinit var hold: RecyclerView.ViewHolder
    private lateinit var myListener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener:OnItemClickListener ){
        myListener=listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ImagesViewBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            myListener
        )
    }

    override fun onViewRecycled(holder: ItemViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.checkBox.setOnCheckedChangeListener(null)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val photo = list[position]

        holder.binding.apply {
            checkBox.isChecked = photo.checkBox
            titleTV.text = photo.title
            Glide.with(mainLay)
                .load("https://live.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg")
                .into(imageView)
            Log.d("MyData","https://live.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg")
        }
        holder.binding.checkBox.setOnCheckedChangeListener { _, checked ->
            if (classNumber==1) {
                when (checked) {
                    true -> {
                        photo.checkBox = true
                    }
                    else -> {
                        photo.checkBox = false
                    }
                }
            }
            else{
                when (checked) {
                    false -> {
                        list.removeAt(position)
                        update()
                    }
                }
            }
        }
    }

    override fun getItemCount() = list.size

    fun update(){
        notifyDataSetChanged()
    }
}