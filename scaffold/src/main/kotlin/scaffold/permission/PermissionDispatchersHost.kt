package scaffold.permission

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
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

interface PermissionDispatchersHost : PermissionDispatchers {
    /**
     * 注册权限的回调
     *
     * @receiver [ActivityResultCaller]
     * @see [ActivityResultContracts.RequestMultiplePermissions]
     */
    context (ActivityResultCaller)
    fun register()
}

@Singleton
internal class DefaultPermissionDispatchersHost @Inject constructor() : PermissionDispatchersHost {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val permissionStream = MutableSharedFlow<Map<String, Boolean>>()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    context (ActivityResultCaller)
            override fun register() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                scope.launch {
                    permissionStream.emit(it)
                }
            }
    }

    context (Fragment)
            override suspend fun requestPermission(permissions: List<String>): Map<String, PermissionResult> {
        return channelFlow {
            launch {
                permissionStream.filter {
                    it.keys.size == permissions.size && it.keys.containsAll(permissions)
                }.collectLatest { result ->
                    send(result.mapValues { PermissionResult(it.key, it.value) })
                }
            }
            permissionLauncher.launch(permissions.toTypedArray())
        }.first()
    }
}
