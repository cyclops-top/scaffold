package scaffold.simple

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

class Catalog(items: Set<Item.Destination>) {
    val root: List<Item>

    init {
        val map: Map<String, List<Item.Destination>> = items.groupBy { it.parent }
        root = map[""]!!.map {
            it.toItem(map)
        }
    }

    private fun Item.Destination.toItem(total: Map<String, List<Item.Destination>>): Item {
        return if (destination != null) {
            this
        } else {
            val children = (total[name] ?: emptyList()).map {
                it.toItem(total)
            }
            Item.Node(name, children)
        }
    }

    @Parcelize
    sealed interface Item : Parcelable {
        val name: String

        @Parcelize
        data class Destination(
            val parent: String,
            override val name: String,
            val destination: Int?,
        ) : Item

        @Parcelize
        data class Node(override val name: String, val children: List<Item>) : Item
    }


}