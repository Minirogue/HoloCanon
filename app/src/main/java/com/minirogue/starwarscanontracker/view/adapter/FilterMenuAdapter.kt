package com.minirogue.starwarscanontracker.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.minirogue.starwarscanontracker.model.room.entity.FilterType
import com.minirogue.starwarscanontracker.R
import kotlinx.android.synthetic.main.filter_type_menuitem.view.*

class FilterMenuAdapter: RecyclerView.Adapter<FilterMenuAdapter.FilterTypeViewHolder>() {

    private lateinit var listener: OnClickListeners
    private val typeList = mutableListOf<FilterType>()


    fun updateList(newList: List<FilterType>){
        typeList.clear()
        typeList.addAll(newList)
        notifyDataSetChanged()
    }

    fun setOnClickListeners(newListener: OnClickListeners) {
        listener = newListener
    }

    interface OnClickListeners {
        fun onFilterTypeClicked(filterType: FilterType)
        fun onFilterTypeSwitchClicked(filterType: FilterType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterTypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_type_menuitem, parent, false)
        return FilterTypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterTypeViewHolder, position: Int) {
        val currentItem = typeList[position]
        holder.textView.text = currentItem.getText()
        holder.switch.isChecked = currentItem.isFilterPositive
        holder.switch.setOnClickListener{ listener.onFilterTypeSwitchClicked(currentItem)}
        holder.itemView.setOnClickListener { listener.onFilterTypeClicked(currentItem) }
    }

    override fun getItemCount(): Int {
        return typeList.size
    }

    class FilterTypeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val textView: TextView = itemView.filter_type_textview
        val switch: SwitchCompat = itemView.type_switch
    }
}