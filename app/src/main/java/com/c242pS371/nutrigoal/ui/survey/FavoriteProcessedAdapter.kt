package com.c242pS371.nutrigoal.ui.survey

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.databinding.FavoriteProcessedFoodItemBinding
import com.c242pS371.nutrigoal.utils.ToastUtil

class FavoriteProcessedAdapter(
    private var items: List<String>,
    private val context: Context
) : RecyclerView.Adapter<FavoriteProcessedAdapter.ItemViewHolder>() {

    private val checkedItems = mutableSetOf<String>()
    private var filteredItems = items.toList()
    private val maxCheckedItems = 4

    inner class ItemViewHolder(
        private val binding: FavoriteProcessedFoodItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            with(binding) {
                checkBox.text = item
                checkBox.setOnCheckedChangeListener(null)
                checkBox.isChecked = checkedItems.contains(item)
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (checkedItems.size < maxCheckedItems) {
                            checkedItems.add(item)
                        } else {
                            ToastUtil.showToast(
                                context,
                                context.getString(R.string.error_maximum_selected_favorite_food)
                            )
                            checkBox.isChecked =
                                false
                        }
                    } else {
                        checkedItems.remove(item)
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: Collection<String>) {
        this.items = newItems.toList()
        filteredItems = this.items
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

    @SuppressLint("NotifyDataSetChanged")
    fun clearCheckedItems() {
        checkedItems.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        filteredItems = if (query.isEmpty()) {
            items
        } else {
            items.filter { it.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}
