package com.ronik.geotraveljournal.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class SearchAddressFilterAdapter(
    context: Context,
    resource: Int,
    private val objects: List<String>
) : ArrayAdapter<String>(context, resource, objects) {

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrEmpty()) {
                    objects
                } else {
                    objects.filter { it.contains(constraint, ignoreCase = true) }
                }

                val results = FilterResults()
                results.values = filteredList
                results.count = filteredList.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                val filteredValues = results?.values as? List<String> ?: emptyList()
                addAll(filteredValues)
                notifyDataSetChanged()
            }
        }
    }
}
