package com.bedirhandag.harcapaylas.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bedirhandag.harcapaylas.R
import com.bedirhandag.harcapaylas.model.GroupMemberDetail

class TransactionDetailsAdapter(
    private val itemList: ArrayList<GroupMemberDetail>
): RecyclerView.Adapter<TransactionDetailsAdapter.PostHolder>() {

    fun updateList(newList: ArrayList<GroupMemberDetail>){
        itemList.apply {
            clear()
            addAll(newList)
            notifyDataSetChanged()
        }
    }

    fun addItem(item: GroupMemberDetail) {
        itemList.apply {
            add(size, item)
            notifyItemInserted(size-1)
        }
    }

    fun removeItem(item: GroupMemberDetail) {
        itemList.remove(item)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.row_adapter_transaction_details,parent,false).also {
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
            }
        }
    }

    class PostHolder(view : View) : RecyclerView.ViewHolder(view) {
        var username : TextView? = null
        var price : TextView? = null
        init {
            username = view.findViewById(R.id.holderUname)
            price = view.findViewById(R.id.holderPrice)
        }
    }
}