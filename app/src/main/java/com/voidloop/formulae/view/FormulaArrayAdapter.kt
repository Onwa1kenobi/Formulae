package com.voidloop.formulae.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.voidloop.formulae.R
import com.voidloop.formulae.data.local.FormulaEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FormulaArrayAdapter(
    context: Context, private val viewModel: FormulaeViewModel,
    private val onItemSelected: ((FormulaEntity) -> Unit) = {}
) :
    ArrayAdapter<FormulaEntity>(context, 0), Filterable {

    private var listItems = listOf<FormulaEntity>()

    override fun getCount(): Int {
        return listItems.size
    }

    override fun getItem(position: Int): FormulaEntity {
        return listItems[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView
        if (listItem == null) {
            listItem = LayoutInflater.from(context)
                .inflate(R.layout.layout_text_view, parent, false)
        }

        val item = getItem(position)
        listItem?.findViewById<TextView>(R.id.labelText)?.text = item.input

        listItem?.setOnClickListener { _ ->
            onItemSelected(item)
        }
        return listItem!!
    }

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val charString = constraint.toString().trim()

            GlobalScope.launch {
                listItems = viewModel.searchFormulaLocally(charString)
            }

            val filterResult = FilterResults()
            filterResult.values = listItems
            filterResult.count = listItems.size
            return filterResult
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as FormulaEntity).input
        }
    }
}