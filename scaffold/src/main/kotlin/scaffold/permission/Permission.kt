package scaffold.permission

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.withCreated
import dagger.hilt.EntryPoint
import dagger.hilt.android.EntryPointAccessors
import scaffold.ToolBoxScope
import scaffold.di.PermissionDispatchersEntryPoint

/**
 * 查找 [PermissionDispatchers]，通过 hilt [EntryPoint]
 *
 * @return [PermissionDispatchers]
 * @see [PermissionDispatchersEntryPoint]
 */
fun Context.findPermissionDispatchers(): PermissionDispatchers {
    return EntryPointAccessors.fromApplication(
        applicationContext,
        PermissionDispatchersEntryPoint::class.java
    ).permissionDispatchers()
}

/**
 * 请求权限
 *
 * @param permission 权限
 * @return 权限结果
 * @receiver [Fragment],[ToolBoxScope]
 * @see [permissions]
 */
context (Fragment)
        suspend fun ToolBoxScope.permission(permission: String): PermissionResult {
    withCreated { }
    return requireContext().findPermissionDispatchers()
        .requestPermission(listOf(permission))[permission]!!
}


/**
 * 请求多个权限
 *
 * @param permissions 权限列表
 * @return 权限结果
 * @receiver [Fragment],[ToolBoxScope]
 */
context (Fragment)
        suspend fun ToolBoxScope.permissions(permissions: List<String>): Map<String, PermissionResult> {
    withCreated { }
    return requireContext().findPermissionDispatchers()
        .requestPermission(permissions)
}