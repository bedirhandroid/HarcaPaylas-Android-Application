package com.bedirhandag.harcapaylas.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bedirhandag.harcapaylas.R
import com.bedirhandag.harcapaylas.model.ReportModel

class ReportsAdapter(
    private val itemList: ArrayList<ReportModel>,
    private val onItemClickListener: (model: ReportModel) -> Unit,
    private val onItemLongClickListener: (model: ReportModel) -> Unit
): RecyclerView.Adapter<ReportsAdapter.PostHolder>() {

    fun updateList(newList: ArrayList<ReportModel>){
        itemList.apply {
            clear()
            addAll(newList)
            notifyDataSetChanged()
        }
    }

    fun addItem(item: ReportModel) {
        itemList.apply {
            add(size, item)
            notifyItemInserted(size-1)
        }
    }

    fun removeItem(item: ReportModel) {
        itemList.remove(item)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.row_adapter_reports_item,parent,false).also {
            return PostHolder(it)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.apply {
            itemList[position].also { _item ->
                username?.text = _item.username
                price?.text = price?.context?.getString(R.string.placeholder_price, _item.price)
                description?.text = _item.description
            }
        }

        holder.itemView.run {
            setOnClickListener { onItemClickListener(itemList[position]) }
            setOnLongClickListener {
                onItemLongClickListener(itemList[position])
                true
            }
        }
    }

    class PostHolder(view : View) : RecyclerView.ViewHolder(view) {
        var username : TextView? = null
        var price : TextView? = null
        var description : TextView? = null
        init {
            username = view.findViewById(R.id.edtUsername)
            price = view.findViewById(R.id.edtPrice)
            description = view.findViewById(R.id.edtDescription)
        }
    }
}