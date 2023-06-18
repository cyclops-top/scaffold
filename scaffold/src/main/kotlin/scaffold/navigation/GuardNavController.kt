package scaffold.navigation

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import kotlinx.coroutines.launch
import scaffold.permission.findPermissionDispatchers

@Suppress("unused", "MemberVisibilityCanBePrivate")
@JvmInline
value class GuardNavController(private val navController: NavController) {


    /**
     * 通过 [NavDirections] 导航到目的地，通过[NavDirections.arguments]和目的地的
     * 默认参数[GuardNavController.PermissionKey]判断是否需要先判定权限
     *
     * @param directions directions that describe this navigation operation
     */
    context (Fragment)
    fun navigate(directions: NavDirections) {
        navigate(directions.actionId, directions.arguments, null)
    }

    /**
     * 通过 [NavDirections] 导航到目的地，通过[NavDirections.arguments]和目的地的
     * 默认参数[GuardNavController.PermissionKey]判断是否需要先判定权限
     *
     * @param directions directions that describe this navigation operation
     * @param navOptions special options for this navigation operation
     */
    context (Fragment)
    fun navigate(directions: NavDirections, navOptions: NavOptions?) {
        navigate(directions.actionId, directions.arguments, navOptions)
    }

    /**
     * 通过 [NavDirections] 导航到目的地，通过[NavDirections.arguments]和目的地的
     * 默认参数[GuardNavController.PermissionKey]判断是否需要先判定权限
     *
     * @param directions directions that describe this navigation operation
     * @param navigatorExtras extras to pass to the [Navigator]
     */
    context (Fragment)
    fun navigate(directions: NavDirections, navigatorExtras: Navigator.Extras) {
        navigate(directions.actionId, directions.arguments, null, navigatorExtras)

    }

    /**
     * 通过 resId 导航到目的地，通过[NavDestination.getAction]获取目的地的
     * 默认参数和参数args中的[GuardNavController.PermissionKey]判断是否需要先判定权限
     * @param resId an [action][NavDestination.getAction] id or a destination
     *     id to navigate to
     * @param args arguments to pass to the destination
     * @param navOptions special options for this navigation operation
     * @param navigatorExtras extras to pass to the Navigator
     * @throws IllegalStateException if there is no current navigation node
     * @throws IllegalArgumentException if the desired destination cannot be
     *     found from the current destination
     */
    context (Fragment)
    fun navigate(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null,
    ) {
        val permissions = getRequirePermissions(resId, args)
        if (permissions.isNotEmpty()) {
            lifecycleScope.launch {
                val permissionResult = navController.context.findPermissionDispatchers()
                    .requestPermission(permissions)

                if (permissionResult.all { it.value.granted }) {
                    withStarted {
                        navController.navigate(resId, args, navOptions, navigatorExtras)
                    }
                } else {
                    Toast.makeText(navController.context, "权限不足", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                withStarted {
                    navController.navigate(resId, args, navOptions, navigatorExtras)
                }
            }
        }
    }

    /**
     * 通过 NavDeepLinkRequest 导航到目的地，通过[NavGraph.matchDeepLink]获取目的地的
     * 默认参数和参数args中的[GuardNavController.PermissionKey]判断是否需要先判定权限
     * @param request deepLinkRequest to the destination reachable from the
     *     current NavGraph
     * @throws IllegalArgumentException if the given deep link request is
     *     invalid
     */
    context (Fragment) fun navigate(request: NavDeepLinkRequest) {
        navigate(request, null)
    }

    /**
     * 通过 NavDeepLinkRequest 导航到目的地，通过[NavGraph.matchDeepLink]获取目的地的
     * 默认参数和参数args中的[GuardNavController.PermissionKey]判断是否需要先判定权限
     *
     * @param request deepLinkRequest to the destination reachable from the
     *     current NavGraph
     * @param navOptions special options for this navigation operation
     * @throws IllegalArgumentException if the given deep link request is
     *     invalid
     */
    context (Fragment) fun navigate(request: NavDeepLinkRequest, navOptions: NavOptions?) {
        navigate(request, navOptions, null)
    }

    /**
     * 通过 NavDeepLinkRequest 导航到目的地，通过[NavGraph.matchDeepLink]获取目的地的
     * 默认参数和参数args中的[GuardNavController.PermissionKey]判断是否需要先判定权限
     *
     * @param request deepLinkRequest to the destination reachable from the
     *     current NavGraph
     * @param navOptions special options for this navigation operation
     * @param navigatorExtras extras to pass to the Navigator
     * @throws IllegalArgumentException if the given deep link request is
     *     invalid
     */
    context (Fragment)
    fun navigate(
        request: NavDeepLinkRequest,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?,
    ) {
        val permissions = request.getRequirePermissions()
        if (permissions.isNotEmpty()) {
            lifecycleScope.launch {
                val permissionResult = navController.context.findPermissionDispatchers()
                    .requestPermission(permissions)
                if (permissionResult.all { it.value.granted }) {
                    withStarted {
                        navController.navigate(request, navOptions, navigatorExtras)
                    }
                } else {
                    Toast.makeText(navController.context, "权限不足", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                withStarted {
                    navController.navigate(request, navOptions, navigatorExtras)
                }
            }
        }
    }

    /**
     * 在[NavDeepLinkRequest]中获取权限列表
     *
     * @return 权限列表
     */
    private fun NavDeepLinkRequest.getRequirePermissions(): List<String> {
        val deepLinkMatch =
            navController.graph.matchDeepLink(this) ?: throw IllegalArgumentException(
                "Navigation destination that matches request $this cannot be found in the " +
                        "navigation graph ${navController.graph}"
            )
        return buildList {
            addAll(deepLinkMatch.destination.getRequirePermissions())
            addAll(uri!!.getRequirePermissions())
        }.filter { it.isNotEmpty() }
            .distinct()
    }

    /**
     * 在[Uri]的Query参数中查询权限列表
     *
     * @return 权限列表
     */
    private fun Uri.getRequirePermissions(): List<String> {
        return getQueryParameter(PermissionKey)?.split(",") ?: emptyList()
    }
    /**
     * 在[NavDestination.arguments]中查询权限列表
     *
     * @return 权限列表
     */
    private fun NavDestination.getRequirePermissions(): List<String> {
        return (arguments[PermissionKey]?.defaultValue as String?)?.split(",") ?: emptyList()
    }
    /**
     * 通过目的地resId查询目的地和传入参数中查询权限列表
     *
     * @return 权限列表
     */
    private fun getRequirePermissions(@IdRes resId: Int, args: Bundle?): List<String> {
        return buildList {
            addAll(getRequirePermissions(resId))
            addAll(args?.getPermissions() ?: emptyList())
        }.filter { it.isNotEmpty() }
            .distinct()
    }
    /**
     * 通过目的地resId查询目的地查询权限列表
     *
     * @return 权限列表
     */
    private fun getRequirePermissions(@IdRes resId: Int): List<String> {
        return (navController.findDestination(resId)?.arguments?.get(PermissionKey)?.defaultValue as String?)
            ?.split(",") ?: emptyList()
    }
    /**
     * 通过参数[Bundle]中查询权限列表
     *
     * @return 权限列表
     */
    private fun Bundle.getPermissions(): List<String> {
        return getString(PermissionKey)?.split(",") ?: emptyList()
    }

    /**
     * 导航到目的地并返回Fragment结果
     *
     * @param resId 目的地navigation id
     * @param args 参数
     * @param navOptions 导航操作的特殊选项
     * @param navigatorExtras 额外传递的内容，如动画
     * @param T 返回结果类型
     * @return Fragment 返回结果
     */
    context (Fragment) suspend fun <T : Any> NavController.navigateForResult(
        @IdRes resId: Int,
        args: Bundle = bundleOf(),
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null,
    ): T? {
        val request = FragmentResultRequest<T>()
        navigate(resId, request.mergeResultKey(args), navOptions, navigatorExtras)
        return request.awaitResult()
    }

    /**
     * 导航到目的地并返回Fragment结果
     *
     * @param deepLink 目的地
     * @param navOptions 导航操作的特殊选项
     * @param navigatorExtras 额外传递的内容，如动画
     * @param T 返回结果类型
     * @return Fragment 返回结果
     */
    context (Fragment) suspend fun <T : Any> NavController.navigateForResult(
        deepLink: Uri,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null,
    ): T? {
        val request = FragmentResultRequest<T>()
        navigate(deepLink = request.mergeResultKey(uri = deepLink), navOptions, navigatorExtras)
        return request.awaitResult()
    }

    /**
     * 导航到目的地并返回Fragment结果
     *
     * @param deepLink 目的地
     * @param navOptions 导航操作的特殊选项
     * @param navigatorExtras 额外传递的内容，如动画
     * @param T 返回结果类型
     * @return Fragment 返回结果
     */
    context (Fragment) suspend fun <T : Any> NavController.navigateForResult(
        deepLink: String,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null,
    ): T? {
        return navigateForResult(Uri.parse(deepLink), navOptions, navigatorExtras)
    }

    companion object {
        /**
         * 权限关键字
         */
        const val PermissionKey = "permissions"
    }
}
