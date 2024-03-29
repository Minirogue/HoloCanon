package com.minirogue.holocanon.feature.select.filters.internal.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.minirogue.holocanon.feature.select.filters.internal.databinding.SelectFiltersFilterSelectionItemBinding
import filters.model.FilterGroup
import filters.model.FilterType
import filters.model.MediaFilter

class FilterSelectionAdapter : RecyclerView.Adapter<FilterSelectionAdapter.FilterTypeViewHolder>() {

    private lateinit var listener: OnClickListeners
    private val typeList = mutableListOf<FilterGroup>()
    private val isExpanded = mutableMapOf<FilterType, Boolean>()
    private val excludedTypes = ArrayList<FilterType>()

    fun updateList(newList: List<FilterGroup>) {
        typeList.clear()
        for (item in newList) {
            if (item.type !in excludedTypes) {
                typeList.add(item)
            }
        }
        notifyDataSetChanged()
    }

    fun updateExcludedTypes(newExcludedTypes: List<FilterType>) {
        excludedTypes.clear()
        excludedTypes.addAll(newExcludedTypes)
        cleanTypeList()
        notifyDataSetChanged()
    }

    private fun cleanTypeList() {
        typeList.removeAll { it.type in excludedTypes }
    }

    fun setOnClickListeners(newListener: OnClickListeners) {
        listener = newListener
    }

    interface OnClickListeners {
        fun onFilterClicked(mediaFilter: MediaFilter)
        fun onFilterTypeSwitchClicked(filterGroup: FilterGroup)
        fun setFilterGroupObservation(chipGroup: ChipGroup, filterGroup: FilterGroup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterTypeViewHolder {
        val binding = SelectFiltersFilterSelectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterTypeViewHolder, position: Int) {
        val currentItem = typeList[position]
        holder.itemView.setOnClickListener {
            isExpanded[currentItem.type] = !getIsExpanded(currentItem); notifyItemChanged(position)
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

    private fun getIsExpanded(group: FilterGroup) = isExpanded[group.type] ?: false

    override fun getItemCount(): Int {
        return typeList.size
    }

    class FilterTypeViewHolder(val binding: SelectFiltersFilterSelectionItemBinding) : RecyclerView.ViewHolder(binding.root)
}
