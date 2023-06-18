package scaffold.simple.pickers

import androidx.fragment.app.Fragment

interface PickersMaker {
    fun item(name: String, action: suspend Fragment.() -> String?)
}

private class DefaultPickersMaker(val list: MutableList<Item> = mutableListOf()) : PickersMaker {
    override fun item(name: String, action: suspend Fragment.() -> String?) {
        list.add(Item(name, action))
    }

}

fun buildPickers(block: PickersMaker.() -> Unit): List<Item> {
    val maker = DefaultPickersMaker()
    block(maker)
    return maker.list
}
