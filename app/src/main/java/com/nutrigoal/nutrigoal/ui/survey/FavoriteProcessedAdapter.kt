package com.nutrigoal.nutrigoal.ui.survey

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutrigoal.nutrigoal.databinding.FavoriteProcessedFoodItemBinding

class FavoriteProcessedAdapter(
    private var items: List<String>,
) : RecyclerView.Adapter<FavoriteProcessedAdapter.ItemViewHolder>() {

    private val checkedItems = mutableSetOf<String>()
    private var filteredItems = items.toList()

    inner class ItemViewHolder(
        private val binding: FavoriteProcessedFoodItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            with(binding) {
                checkBox.text = item
                checkBox.isChecked = checkedItems.contains(item)
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        checkedItems.add(item)
                    } else {
                        checkedItems.remove(item)
                    }
                }
            }
        }
    }

    fun updateItems(newItems: Collection<String>) {
        this.items = emptyList()
        this.items = newItems.toList()
        notifyDataSetChanged()
    }

    fun getCheckedItems(): List<String> {
        return checkedItems.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = FavoriteProcessedFoodItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(filteredItems[position])
    }

    override fun getItemCount(): Int = filteredItems.size

    fun filter(query: String) {
        filteredItems = if (query.isEmpty()) {
            items
        } else {
            items.filter { it.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}
