@file:Suppress("unused")

package scaffold.paging

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.Collections


/**
 * Data event
 *
 * @param K primary key type
 * @param T data type
 */
sealed interface DataEvent<K : Any, T : Any> {
    /**
     * Update event
     *
     * @param K primary key type
     * @param T data type
     * @property filter
     * @property transform
     */
    class Update<K : Any, T : Any>(val filter: (T) -> Boolean, val transform: (T) -> T) :
        DataEvent<K, T>

    /**
     * Insert Events
     *
     * @param K primary key type
     * @param T data type
     * @constructor Create empty Insert
     * @property value
     * @property transform
     */
    class Insert<K : Any, T : Any>(
        val value: T,
        val transform: (before: T?, after: T?) -> Boolean,
    ) :
        DataEvent<K, T>

    /**
     * Delete
     *
     * @param K primary key type
     * @param T data type
     * @constructor Create empty Delete
     * @property ids delete keys
     */
    class Delete<K : Any, T : Any>(val ids: Set<K>) : DataEvent<K, T>
}

/**
 * Patch to 忽略insert 需单独维护
 *
 * @param data
 * @param keySelector
 * @param K primary key type
 * @param T data type
 * @return
 * @receiver
 */
fun <K : Any, T : Any> DataEvent<K, T>.patchTo(
    data: PagingData<T>,
    keySelector: T.() -> K,
): PagingData<T> {
    return when (this) {
        is DataEvent.Delete -> data.filter {
            it.keySelector() !in ids
        }

        is DataEvent.Update -> data.map { item ->
            if (filter(item)) {
                transform(item)
            } else {
                item
            }
        }

        else -> data
    }
}

/**
 * Data events flow
 *
 * @param K primary key type
 * @param T data type
 */
interface DataEventsFlow<K : Any, T : Any> : Flow<DataEvent<K, T>> {
    val keySelector: T.() -> K
}

/**
 * Mutable data events flow
 *
 * @param K primary key type
 * @param T data type
 */
interface MutableDataEventsFlow<K : Any, T : Any> : DataEventsFlow<K, T> {
    /**
     * Update
     *
     * @param filter data filter
     * @param transform
     */
    fun update(filter: (T) -> Boolean, transform: (T) -> T)

    /**
     * Insert
     *
     * @param value data
     * @param transform
     */
    fun insert(value: T, transform: (before: T?, after: T?) -> Boolean)

    /**
     * Delete
     *
     * @param ids
     */
    fun delete(ids: List<K>)

    companion object {
        operator fun <K : Any, T : Any> invoke(
            keySelector: T.() -> K,
            scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        ): MutableDataEventsFlow<K, T> {
            return DefaultMutableDataEventsFlow(keySelector, scope)
        }
    }
}

/**
 * Update
 *
 * @param value
 * @param K primary key type
 * @param T data type
 * @receiver [MutableDataEventsFlow]
 */
fun <K : Any, T : Any> MutableDataEventsFlow<K, T>.update(value: T) {
    update(keySelector(value)) {
        value
    }
}

/**
 * Update
 *
 * @param key primary key type
 * @param transform
 * @param K primary key type
 * @param T data type
 * @receiver [MutableDataEventsFlow]
 */
fun <K : Any, T : Any> MutableDataEventsFlow<K, T>.update(key: K, transform: (T) -> T) {
    update({ keySelector(it) == key }, transform)
}


/**
 * Delete item
 *
 * @param key primary key type
 * @param K primary key type
 * @param T data type
 * @receiver [MutableDataEventsFlow]
 */
fun <K : Any, T : Any> MutableDataEventsFlow<K, T>.delete(key: K) {
    delete(listOf(key))
}

fun <K : Any, T : Any> DataEventsFlow<K, T>.filterInsert(filter: (T) -> Boolean): DataEventsFlow<K, T> {
    return DataEventsFlowWrapper(
        keySelector,
        filter { if (it is DataEvent.Insert) filter(it.value) else true })
}

private class DataEventsFlowWrapper<K : Any, T : Any>(
    override val keySelector: T.() -> K,
    val flow: Flow<DataEvent<K, T>>,
) :
    DataEventsFlow<K, T>, Flow<DataEvent<K, T>> by flow

/**
 * As data events flow
 *
 * @param K primary key type
 * @param T data type
 * @return [DataEventsFlow]
 * @receiver [MutableDataEventsFlow]
 */
fun <K : Any, T : Any> MutableDataEventsFlow<K, T>.asDataEventsFlow(): DataEventsFlow<K, T> {
    return ReadOnlyDataEventsFlow(this)
}

private class ReadOnlyDataEventsFlow<K : Any, T : Any>(source: MutableDataEventsFlow<K, T>) :
    DataEventsFlow<K, T> by source


private class DefaultMutableDataEventsFlow<K : Any, T : Any>(
    override val keySelector: T.() -> K,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
) : MutableDataEventsFlow<K, T> {
    private val _events = MutableSharedFlow<DataEvent<K, T>>()

    override suspend fun collect(collector: FlowCollector<DataEvent<K, T>>) {
        _events.collect(collector)
    }

    override fun update(filter: (T) -> Boolean, transform: (T) -> T) {
        scope.launch {
            _events.emit(DataEvent.Update(filter, transform))
        }
    }

    override fun insert(value: T, transform: (before: T?, after: T?) -> Boolean) {
        scope.launch {
            _events.emit(DataEvent.Insert(value, transform))
        }
    }

    override fun delete(ids: List<K>) {
        scope.launch {
            _events.emit(DataEvent.Delete(ids.toSet()))
        }
    }
}

/**
 * Patch
 *
 * ```kotlin
 *  data class ItemData(val id: Int, val value: String)
 *  val eventsFlow =  MutableDataEventsFlow(keySelector = ItemData::id)
 *  class ItemDataPagingSource:PagingSource<Int,ItemData>(){
 *      ...
 *  }
 *  val pager = Pager(config = PagingConfig(20)) {
 *      ItemDataPagingSource()
 *  }.flow
 *  .patch(eventsFlow)
 *
 * // if delete item.id = 1
 *  eventsFlow.delete(listOf(1))
 * ```
 *
 * @param eventsFlow
 * @param K primary key type
 * @param T data type
 * @return
 */
@JvmName("patchPagingData")
fun <K : Any, T : Any> Flow<PagingData<T>>.patch(eventsFlow: DataEventsFlow<K, T>): Flow<PagingData<T>> {

    return channelFlow {
        val producerScope = this
        var data: PagingData<T>? = null
        val inserts = arrayListOf<DataEvent.Insert<K, T>>()
        launch {
            eventsFlow.collectLatest { event ->
                if (data != null) {
                    data = when (event) {
                        is DataEvent.Insert -> {
                            inserts.add(event)
                            data!!.applyInserts(inserts)
                        }

                        else -> event.patchTo(data!!, eventsFlow.keySelector)
                            .applyInserts(inserts)
                    }
                    producerScope.send(data!!)
                }
            }
        }
        launch {
            this@patch.cachedIn(this).collectLatest {
                data = it
                inserts.clear()
                producerScope.send(data!!)
            }
        }
    }
}

private fun <K : Any, T : Any> PagingData<T>.applyInserts(inserts: List<DataEvent.Insert<K, T>>): PagingData<T> {
    var result = this
    for (insert in Collections.unmodifiableList(inserts.toMutableList())) {
        result = result.insertSeparators { before: T?, after: T? ->
            if (insert.transform(before, after)) insert.value else null
        }
    }
    return result
}

/**
 * Patch
 *
 * @param eventsFlow
 * @param K primary key type
 * @param T data type
 * @return Flow<T>
 * @see patch
 */

@JvmName("patchNullable")
fun <K : Any, T : Any> Flow<T?>.patch(eventsFlow: Flow<DataEvent<K, T>>): Flow<T> {
    return channelFlow {
        val producerScope = this
        var data: T? = null

        launch {
            eventsFlow.collectLatest { event ->
                if (data != null) {
                    if (event is DataEvent.Update) {
                        if (event.filter(data!!)) {
                            data = event.transform(data!!)
                            producerScope.send(data!!)
                        }
                    }
                }
            }
        }
        launch {
            this@patch.collectLatest {
                data = it
                producerScope.send(data!!)
            }
        }
    }
}

/**
 * Patch
 *
 * @param eventsFlow
 * @param K primary key type
 * @param T data type
 * @return
 * @see patch
 */
fun <K : Any, T : Any> Flow<T>.patch(eventsFlow: Flow<DataEvent<K, T>>): Flow<T> {
    return channelFlow {
        val producerScope = this
        var data: T? = null

        launch {
            eventsFlow.collectLatest { event ->
                if (data != null) {
                    if (event is DataEvent.Update) {
                        if (event.filter(data!!)) {
                            data = event.transform(data!!)
                            producerScope.send(data!!)
                        }
                    }
                }
            }
        }
        launch {
            this@patch.collectLatest {
                data = it
                producerScope.send(data!!)
            }
        }
    }
}