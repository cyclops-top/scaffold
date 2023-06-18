package scaffold.simple.pickers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import scaffold.Pickers
import scaffold.ToolBox
import scaffold.app.BindingFragment
import scaffold.pickers.date
import scaffold.pickers.dateRange
import scaffold.pickers.time
import scaffold.simple.databinding.FragmentPickersBinding
import scaffold.simple.databinding.ItemPickerBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class PickersFragment : BindingFragment<FragmentPickersBinding>(FragmentPickersBinding::inflate) {
    private val adapter = PickerAdapter()

    init {
        val dateFormat = SimpleDateFormat("yyyy-HH-dd", Locale.getDefault())
        adapter.items = buildPickers {
            item("time") {
                ToolBox.Pickers.time()?.let { "%02d:%02d".format(it.hour, it.minute) }
            }
            item("date") {
                ToolBox.Pickers.date()?.let { dateFormat.format(Date(it)) }
            }
            item("date range") {
                ToolBox.Pickers.dateRange()
                    ?.let { "${dateFormat.format(Date(it.first))} ~ ${dateFormat.format(Date(it.last))}" }
            }
        }.map { UiItem(it, null) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.items.adapter = adapter
    }

}


data class Item(val name: String, val action: suspend Fragment.() -> String?)

data class UiItem(val item: Item, var value: String?)

context (PickersFragment)
class PickerAdapter : RecyclerView.Adapter<PickerViewHolder>() {

    var items = emptyList<UiItem>()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(DiffCallback(field, value))
            field = value
            diffResult.dispatchUpdatesTo(this)
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickerViewHolder {
        return PickerViewHolder(
            ItemPickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PickerViewHolder, position: Int) {
        val item = items[position]
        holder.setData(item)
        holder.binding.root.setOnClickListener {
            lifecycleScope.launch {
                item.value = item.item.action(this@PickersFragment) ?: return@launch
                notifyItemChanged(items.indexOf(item), "value" to item.value)
            }
        }
    }

    override fun onBindViewHolder(
        holder: PickerViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        super.onBindViewHolder(holder, position, payloads)
        val value = payloads.firstOrNull() ?: return
        if (value is Pair<*, *>) {
            holder.binding.subTitle.text = (value.second as String?)
        }
    }

    private class DiffCallback(
        private val oldList: List<UiItem>,
        private val newList: List<UiItem>,
    ) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].item.name == newList[newItemPosition].item.name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val old = oldList[oldItemPosition]
            val new = newList[newItemPosition]
            return old == new
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val old = oldList[oldItemPosition]
            val new = newList[newItemPosition]
            return if (old.value != new.value) {
                "value" to new.value
            } else {
                null
            }
        }
    }
}


class PickerViewHolder(val binding: ItemPickerBinding) : RecyclerView.ViewHolder(binding.root) {
    fun setData(item: UiItem) {
        binding.title.text = item.item.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
        binding.subTitle.text = item.value
    }
}