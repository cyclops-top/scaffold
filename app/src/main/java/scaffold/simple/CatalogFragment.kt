package scaffold.simple

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import scaffold.app.BindingFragment
import scaffold.simple.databinding.FragmentCatalogBinding
import scaffold.simple.databinding.ItemCatalogBinding
import javax.inject.Inject

@AndroidEntryPoint
class CatalogFragment : BindingFragment<FragmentCatalogBinding>(FragmentCatalogBinding::inflate) {
    @Inject
    lateinit var catalog: Catalog
    private val adapter = CatalogAdapter()
    private val args by navArgs<CatalogFragmentArgs>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.items.adapter = adapter
        adapter.items = args.items?.toList() ?: catalog.root
    }
}

context (CatalogFragment)
class CatalogAdapter : RecyclerView.Adapter<CatalogViewHolder>() {

    var items = emptyList<Catalog.Item>()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(DiffCallback(field, value))
            field = value
            diffResult.dispatchUpdatesTo(this)
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        return CatalogViewHolder(
            ItemCatalogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        val item = items[position]
        holder.setData(item)
        holder.binding.root.setOnClickListener {
            it.postDelayed({
                when (item) {
                    is Catalog.Item.Destination -> findNavController().navigate(item.destination!!)
                    is Catalog.Item.Node -> findNavController().navigate(
                        CatalogFragmentDirections.actionChildren(
                            item.children.toTypedArray(),
                            item.name
                        )
                    )
                }
            }, 300)
        }
    }

    private class DiffCallback(
        private val oldList: List<Catalog.Item>,
        private val newList: List<Catalog.Item>,
    ) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].name == newList[newItemPosition].name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return true
        }

    }
}


class CatalogViewHolder(val binding: ItemCatalogBinding) : RecyclerView.ViewHolder(binding.root) {
    fun setData(item: Catalog.Item) {
        binding.text.text = item.name
    }
}