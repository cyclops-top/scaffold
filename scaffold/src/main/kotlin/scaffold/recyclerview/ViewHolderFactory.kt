package scaffold.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import scaffold.views.BindingCreator

interface ViewHolderFactory<VH : BindingViewHolder<*, *>> {
    fun create(inflater: LayoutInflater, group: ViewGroup?): VH

    companion object {

        fun <Data : Any, B : ViewBinding> simple(
            bindingCreator: BindingCreator<B>,
            bindData: B.(Data) -> Unit,
        ):
                ViewHolderFactory<BindingViewHolder<B, Data>> {
            return invoke(bindingCreator) {
                object : BindingViewHolder<B, Data>(it) {
                    override fun B.setData(data: Data) {
                        bindData(data)
                    }
                }
            }
        }

        operator fun <Data, B : ViewBinding, VH : BindingViewHolder<B, Data>> invoke(
            bindingCreator: BindingCreator<B>,
            holderCreator: (B) -> VH,
        ): ViewHolderFactory<VH> {
            return object : ViewHolderFactory<VH> {
                override fun create(
                    inflater: LayoutInflater,
                    group: ViewGroup?,
                ): VH {
                    return holderCreator(bindingCreator(inflater, group, false))
                }
            }
        }
    }
}