package scaffold.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import scaffold.views.BindingCreator
import kotlin.reflect.KClass
@TypedBindingAdapterMaker
class TypedBindingAdapterBuilder<Data : Any>(private val items: MutableMap<KClass<*>, ViewHolderFactory<BindingViewHolder<*, out Data>>>) {
    fun <D : Data, B : ViewBinding> items(
        clazz: KClass<D>,
        bindingCreator: BindingCreator<B>,
        bindData: B.(D) -> Unit,
    ) {
        @Suppress("UNCHECKED_CAST")
        items[clazz] =
            ViewHolderFactory.simple(
                bindingCreator,
                bindData
            ) as ViewHolderFactory<BindingViewHolder<*, out Data>>
    }

    fun <VH : BindingViewHolder<*, out Data>> items(
        clazz: KClass<*>,
        creator: (inflater: LayoutInflater, group: ViewGroup?) -> VH,
    ) {
        items[clazz] = object : ViewHolderFactory<BindingViewHolder<*, out Data>> {
            override fun create(inflater: LayoutInflater, group: ViewGroup?): VH {
                return creator(inflater, group)
            }
        }
    }

    fun items(clazz: KClass<*>, factory: ViewHolderFactory<*>) {
        @Suppress("UNCHECKED_CAST")
        items[clazz] = factory as ViewHolderFactory<BindingViewHolder<*, out Data>>
    }
}