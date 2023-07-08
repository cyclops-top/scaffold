package scaffold.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

class TypedPagingAdapter<T : Any>(
    diffCallback: DiffUtil.ItemCallback<T>,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    workerDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : PagingDataAdapter<T, BindingViewHolder<*, out T>>(
    diffCallback,
    mainDispatcher,
    workerDispatcher
) {
    private val typeGen = AtomicInteger()
    private val types = HashMap<KClass<*>, Int>()
    private val viewHolderFactories =
        HashMap<Int, ViewHolderFactory<BindingViewHolder<*, out T>>>()
    private var layoutInflater: LayoutInflater? = null

    fun registerItem(
        clazz: KClass<*>,
        factory: ViewHolderFactory<*>,
    ) {
        val type = typeGen.getAndIncrement()
        types[clazz] = type
        @Suppress("UNCHECKED_CAST")
        viewHolderFactories[type] = factory as ViewHolderFactory<BindingViewHolder<*, out T>>
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BindingViewHolder<*, out T> {
        val inflater = layoutInflater ?: LayoutInflater.from(parent.context).also {
            layoutInflater = it
        }
        return viewHolderFactories[viewType]!!.create(inflater, parent)
    }


    override fun getItemViewType(position: Int): Int {
        return types[peek(position)!!::class]!!
    }

    override fun onBindViewHolder(holder: BindingViewHolder<*, out T>, position: Int) {
        val data = getItem(position)
        holder.internalSetData(data as Any)
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<*, out T>,
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


fun <T : Any> buildPagingAdapter(
    diffCallback: DiffUtil.ItemCallback<T>,
    block: TypedBindingAdapterBuilder<T>.() -> Unit,
): TypedPagingAdapter<T> {
    val items: MutableMap<KClass<*>, ViewHolderFactory<BindingViewHolder<*, out T>>> = hashMapOf()
    val builder = TypedBindingAdapterBuilder(items)
    block(builder)
    val adapter = TypedPagingAdapter(diffCallback)
    items.forEach { (type, factory) ->
        adapter.registerItem(type, factory)
    }
    return adapter
}

