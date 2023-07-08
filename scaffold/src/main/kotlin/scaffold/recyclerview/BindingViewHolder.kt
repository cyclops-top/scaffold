package scaffold.recyclerview

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class BindingViewHolder<B : ViewBinding, Data>(val binding: B) : RecyclerView.ViewHolder(binding.root) {
    @Suppress("UNCHECKED_CAST")
    internal fun internalSetData(data: Any) {
        setData(data as Data)
    }

    @Suppress("UNCHECKED_CAST")
    internal fun internalUpdatePayload(data: Any) {
        updatePayload(data as Data)
    }

    private fun setData(data: Data) {
        binding.setData(data)
    }

    private fun updatePayload(data: Data) {
        binding.updatePayload(data)
    }

    protected open fun B.setData(data: Data) {}
    protected open fun B.updatePayload(data: Data) {}
}