package com.minirogue.starwarscanontracker.view.adapter

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.minirogue.starwarscanontracker.databinding.FilterSelectionItemBinding
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.FilterType

class FilterSelectionAdapter : RecyclerView.Adapter<FilterSelectionAdapter.FilterTypeViewHolder>() {

    private lateinit var listener: OnClickListeners
    private val typeList = mutableListOf<FilterType>()
    private val isExpanded = SparseBooleanArray()
    private val excludedTypes = ArrayList<Int>()

    fun updateList(newList: List<FilterType>) {
        typeList.clear()
        for (item in newList) {
            if (item.typeId !in excludedTypes) {
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
        val binding = FilterSelectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterTypeViewHolder, position: Int) {
        val currentItem = typeList[position]
        holder.itemView.setOnClickListener {
            isExpanded.put(currentItem.typeId,
                    !getIsExpanded(currentItem)); notifyItemChanged(position)
        }
        holder.binding.filterTypeTextview.text = currentItem.text
        holder.binding.typeSwitch.isChecked = currentItem.isFilterPositive
        holder.binding.typeSwitch.text = if (currentItem.isFilterPositive) "filter in" else "filter out"
        holder.binding.typeSwitch.setOnClickListener { listener.onFilterTypeSwitchClicked(currentItem) }

        val chipGroup = holder.binding.filterSelectionChipgroup
        listener.setFilterGroupObservation(chipGroup, currentItem)

        holder.binding.collapsibleSubview.visibility = if (getIsExpanded(currentItem)) View.VISIBLE else View.GONE
        holder.binding.filtergroupExpandIcon.rotation = if (getIsExpanded(currentItem)) 0.toFloat() else (-90).toFloat()
        // holder.itemView.setOnClickListener { listener.onFilterTypeClicked(currentItem) }
    }

    private fun getIsExpanded(type: FilterType) = isExpanded.get(type.typeId, false)

    override fun getItemCount(): Int {
        return typeList.size
    }

    class FilterTypeViewHolder(val binding: FilterSelectionItemBinding) : RecyclerView.ViewHolder(binding.root)
}
