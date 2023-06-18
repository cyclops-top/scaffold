package scaffold.navigation

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Fragment返回结果请求
 *
 * @param T 结果类型
 * @property key 请求参数
 */
internal class FragmentResultRequest<T : Any>(
    private val key: String = "result:${System.nanoTime()}",
) {
    /**
     * 合并请求结果参数
     *
     * @param bundle 参数
     * @return 参数
     */
    fun mergeResultKey(bundle: Bundle): Bundle {
        return bundle.apply {
            putString(ResultSupporter.RequestKey, key)
        }
    }

    /**
     * 合并请求结果参数
     *
     * @param uri deep link
     * @return deep link
     */
    fun mergeResultKey(uri: Uri): Uri {
        return Uri.Builder()
            .scheme(uri.scheme)
            .authority(uri.authority)
            .path(uri.path)
            .query(uri.query)
            .fragment(uri.fragment)
            .appendQueryParameter(ResultSupporter.RequestKey, key)
            .build()
    }

    /**
     * 等待返回结果
     *
     * @return 返回结果
     */
    context (Fragment)
            suspend fun awaitResult(): T? {
        return requireContext().findResultDispatchers().awaitResult(key)
    }
}