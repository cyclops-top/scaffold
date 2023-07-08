package scaffold.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass


class TypedBindingAdapter<Data : Any>(diffCallback: DiffUtil.ItemCallback<Data>) :
    RecyclerView.Adapter<BindingViewHolder<*, out Data>>() {
    private val diffUtil = AsyncListDiffer(this, diffCallback)
    var items: List<Data>
        set(value) {
            diffUtil.submitList(value)
        }
        get() = diffUtil.currentList
    private val typeGen = AtomicInteger()
    private val types = HashMap<KClass<*>, Int>()
    private val viewHolderFactories =
        HashMap<Int, ViewHolderFactory<BindingViewHolder<*, out Data>>>()
    private var layoutInflater: LayoutInflater? = null

    fun registerItem(
        clazz: KClass<*>,
        factory: ViewHolderFactory<*>,
    ) {
        val type = typeGen.getAndIncrement()
        types[clazz] = type
        @Suppress("UNCHECKED_CAST")
        viewHolderFactories[type] = factory as ViewHolderFactory<BindingViewHolder<*, out Data>>
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BindingViewHolder<*, out Data> {
        val inflater = layoutInflater ?: LayoutInflater.from(parent.context).also {
            layoutInflater = it
        }
        return viewHolderFactories[viewType]!!.create(inflater, parent)
    }

    private fun getItem(position: Int): Data {
        return items[position]
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return types[items[position]::class]!!
    }

    override fun onBindViewHolder(holder: BindingViewHolder<*, out Data>, position: Int) {
        val data = getItem(position)
        holder.internalSetData(data as Any)
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<*, out Data>,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val data = getItem(position)
            holder.internalUpdatePayload(data as Any)
        }
    }
}

fun <T : Any> buildTypeAdapter(
    diffCallback: DiffUtil.ItemCallback<T>,
    block: TypedBindingAdapterBuilder<T>.() -> Unit,
): TypedBindingAdapter<T> {
    val items: MutableMap<KClass<*>, ViewHolderFactory<BindingViewHolder<*, out T>>> = hashMapOf()
    val builder = TypedBindingAdapterBuilder(items)
    block(builder)
    val adapter = TypedBindingAdapter(diffCallback)
    items.forEach { (type, factory) ->
        adapter.registerItem(type, factory)
    }
    return adapter
}


