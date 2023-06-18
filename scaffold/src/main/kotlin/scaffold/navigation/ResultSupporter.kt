package scaffold.navigation

import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.lifecycle.withResumed
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch

/**
 * example:
 * ```kotlin
 * class TestFragment:Fragment(),ResultSupporter<Long> by ResultSupporter(){
 *     init{
 *        initResultCaller()
 *     }
 *     ....
 *     fun setResultCurrentTime(){
 *         setResult(System.currentTimeMillis())
 *     }
 *     ...
 * }
 * ```
 * ```kotlin
 * class TestFragment:Fragment(){
 *     private val resultSupporter = registerResultSupporter<Long>()
 *
 *     fun setResultCurrentTime(){
 *         resultSupporter.setResult(System.currentTimeMillis())
 *     }
 * }
 * ```
 *
 * [Fragment]支持返回结果
 *
 * @param T 返回类型
 */
interface ResultSupporter<T : Any> {
    /**
     * 设置结果
     *
     * @param result
     */
    context (Fragment)
    fun setResult(result: T?)

    /** 注册拦截返回事件 */
    fun Fragment.initResultCaller() {
        lifecycleScope.launch {
            withCreated {
                if (requestKey() != null) {
                    requireActivity().onBackPressedDispatcher.addCallback {
                        setResult(null)
                        remove()
                    }
                }
            }
        }
    }

    /**
     * 是否需要返回结果
     *
     * @return
     */
    fun Fragment.isNeedResult(): Boolean {
        return !requestKey().isNullOrBlank()
    }

    companion object {
        /**
         * 构建默认ResultSupporter
         *
         * @param T 返回结果类型
         * @return [ResultSupporter]
         */
        operator fun <T : Any> invoke(): ResultSupporter<T> = DefaultResultSupporter()

        /** 请求结果key */
        const val RequestKey = "request_key"
    }
}

private class DefaultResultSupporter<T : Any> : ResultSupporter<T> {
    context(Fragment) override fun setResult(result: T?) {
        lifecycleScope.launch {
            val key = requestKey() ?: return@launch
            requireContext().findResultDispatchers()
                .setResult(key, result)
            withResumed {
                findNavController().popBackStack()
            }
        }
    }
}

