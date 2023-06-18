package scaffold.navigation

import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.withStarted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface ResultDispatchers {
    /**
     * 设置结果
     *
     * @param key key
     * @param result result
     */
    fun setResult(key: String, result: Any?)

    /**
     * 等待 [ResultSupporter] 返回结果，注意在[LifecycleOwner]上下文下， 保证多次设置结果后，只返回
     * [State.STARTED] 后第一个数据
     *
     * @param key Key
     * @param T 返回类型
     * @return [ResultSupporter] result
     * @see [ResultSupporter.setResult]
     */
    context (LifecycleOwner)
            suspend fun <T> awaitResult(key: String): T?
}

@Singleton
internal class DefaultResultDispatchers @Inject constructor() : ResultDispatchers {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val resultStream = MutableSharedFlow<Pair<String, Any?>>()

    override fun setResult(key: String, result: Any?) {
        scope.launch {
            resultStream.emit(key to result)
        }
    }

    context (LifecycleOwner)
            override suspend fun <T> awaitResult(key: String): T? {
        return channelFlow {
            resultStream.filter { it.first == key }
                .collectLatest {
                    withStarted { }
                    @Suppress("UNCHECKED_CAST")
                    send(it.second as T?)
                }
        }.first()
    }
}