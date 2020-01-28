package com.minirogue.starwarscanontracker.view.adapter

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.FilterType
import kotlinx.android.synthetic.main.filter_selection_item.view.*

class FilterSelectionAdapter : RecyclerView.Adapter<FilterSelectionAdapter.FilterTypeViewHolder>() {

    private lateinit var listener: OnClickListeners
    private val typeList = mutableListOf<FilterType>()
    private val isExpanded = SparseBooleanArray()
    private val excludedTypes = ArrayList<Int>()


    fun updateList(newList: List<FilterType>) {
        typeList.clear()
        for (item in newList){
            if (item.typeId !in excludedTypes){
                typeList.add(item)
            }
        }
        notifyDataSetChanged()
    }

    fun updateExcludedTypes(newExcludedTypes: List<Int>) {
        excludedTypes.clear()
        excludedTypes.addAll(newExcludedTypes)
        cleanTypeList()
        notifyDataSetChanged()
    }

    private fun cleanTypeList() {
        for (type in typeList) {
            if (type.typeId in excludedTypes) {
                typeList.remove(type)
            }
        }
    }

    fun setOnClickListeners(newListener: OnClickListeners) {
        listener = newListener
    }

    interface OnClickListeners {
        fun onFilterClicked(filterObject: FilterObject)
        fun onFilterTypeSwitchClicked(filterType: FilterType)
        fun setFilterGroupObservation(chipGroup: ChipGroup, filterType: FilterType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterTypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_selection_item, parent, false)
        return FilterTypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterTypeViewHolder, position: Int) {
        val currentItem = typeList[position]
        holder.itemView.setOnClickListener { isExpanded.put(currentItem.typeId, !getIsExpanded(currentItem)); notifyItemChanged(position) }
        holder.textView.text = currentItem.text
        holder.switch.isChecked = currentItem.isFilterPositive
        holder.switch.text = if (currentItem.isFilterPositive) "filter in" else "filter out"
        holder.switch.setOnClickListener { listener.onFilterTypeSwitchClicked(currentItem) }

        val chipGroup = holder.itemView.filter_selection_chipgroup
        listener.setFilterGroupObservation(chipGroup, currentItem)

        holder.itemView.collapsible_subview.visibility = if (getIsExpanded(currentItem)) View.VISIBLE else View.GONE
        holder.expandIcon.rotation = if (getIsExpanded(currentItem)) 0.toFloat() else (-90).toFloat()
        //holder.itemView.setOnClickListener { listener.onFilterTypeClicked(currentItem) }
    }

    private fun getIsExpanded(type: FilterType) = isExpanded.get(type.typeId, false)

    override fun getItemCount(): Int {
        return typeList.size
    }

    class FilterTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textView: TextView = itemView.filter_type_textview
        val switch: SwitchCompat = itemView.type_switch
        val expandIcon: ImageView = itemView.filtergroup_expand_icon
    }
}