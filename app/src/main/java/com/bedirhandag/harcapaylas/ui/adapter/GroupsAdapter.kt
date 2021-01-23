package com.bedirhandag.harcapaylas.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bedirhandag.harcapaylas.R

class GroupsAdapter(
    private val itemList: ArrayList<String>,
    private val onItemClickListener: (key: String) -> Unit
): RecyclerView.Adapter<GroupsAdapter.PostHolder>() {

    fun updateList(newList: ArrayList<String>){
        itemList.apply {
            clear()
            addAll(newList)
            notifyDataSetChanged()
        }
    }

    fun addItem(item: String) {
        itemList.apply {
            add(size, item)
            notifyItemInserted(size-1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.row_adapter_groups_item,parent,false).also {
            return PostHolder(it)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.title?.run {
            text = context.resources.getString(R.string.placeholder_group_item_title, itemList[position])
            holder.itemView.setOnClickListener { onItemClickListener(itemList[position]) }
        }
    }

    class PostHolder(view : View) : RecyclerView.ViewHolder(view) {
        var title : TextView? = null
        var photo : ImageView? = null
        init {
            title = view.findViewById(R.id.title)
            photo = view.findViewById(R.id.photo)
        }
    }
}