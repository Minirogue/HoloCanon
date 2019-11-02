package com.minirogue.starwarscanontracker.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.R
import kotlinx.android.synthetic.main.filter_menuitem.view.*
import kotlinx.android.synthetic.main.filter_type_menuitem.view.*

class FilterSubMenuAdapter: RecyclerView.Adapter<FilterSubMenuAdapter.FilterObjectViewHolder>() {

    private lateinit var listener: OnClickListeners
    private val typeList = mutableListOf<FilterObject>()


    fun updateList(newList: List<FilterObject>){
        typeList.clear()
        typeList.addAll(newList)
        notifyDataSetChanged()
    }

    fun setOnClickListener(newListener: OnClickListeners) {
        listener = newListener
    }

    interface OnClickListeners {
        fun onFilterClicked(filterObject: FilterObject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterObjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_menuitem, parent, false)
        return FilterObjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterObjectViewHolder, position: Int) {
        val currentItem = typeList[position]
        holder.textView.text = currentItem.displayText
        holder.checkbox.isChecked = currentItem.active
        holder.itemView.setOnClickListener { listener.onFilterClicked(currentItem) }
    }

    override fun getItemCount(): Int {
        return typeList.size
    }

    class FilterObjectViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val textView: TextView = itemView.filter_type_textview
        val checkbox: CheckBox = itemView.filter_active_checkbox
    }
}