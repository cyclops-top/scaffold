@file:Suppress("unused")

package scaffold.navigation

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.EntryPoint
import dagger.hilt.android.EntryPointAccessors
import scaffold.di.ResultDispatchersEntryPoint


/**
 * 返回请求参数
 *
 * @return
 * @receiver [Fragment]
 * @see [ResultSupporter.RequestKey]
 */
internal fun Fragment.requestKey(): String? {
    return arguments?.getString(ResultSupporter.RequestKey)
}

/**
 * 注册[ResultSupporter]
 *
 * @param T 结果返回类型
 * @return [ResultSupporter]
 * @receiver [Fragment]
 */
fun <T : Any> Fragment.registerResultSupporter(): ResultSupporter<T> {
    return ResultSupporter<T>().apply {
        initResultCaller()
    }
}


/**
 * 查找 [GuardNavController]
 *
 * @return
 * @receiver [Fragment]
 */
fun Fragment.findGuardNavController(): GuardNavController {
    return GuardNavController(findNavController())
}

/**
 * 查找 [ResultDispatchers]，通过 hilt [EntryPoint]
 *
 * @return [ResultDispatchers]
 * @see [ResultDispatchersEntryPoint]
 */
fun Context.findResultDispatchers(): ResultDispatchers {
    return EntryPointAccessors.fromApplication(
        applicationContext,
        ResultDispatchersEntryPoint::class.java
    ).resultDispatchers()
}
