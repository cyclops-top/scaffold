package scaffold.permission

import androidx.fragment.app.Fragment

/**
 * 权限分发
 *
 * @see [DefaultPermissionDispatchersHost]
 */
interface PermissionDispatchers {


    /**
     * 通过协程获取permission结果
     *
     *  ```kotlin
     *  Fragment{
     *      ...
     *
     *      lifecycleScope.launch{
     *          val result = requireContext().findGuardDispatchers().requirePermission(listOf(Manifest.permission.CAMERA))
     *          when(result){
     *           PermissionResult.Granted -> {
     *              // open camera
     *           }
     *           is PermissionResult.Denied -> {
     *              if(result.shouldShowRationale){
     *                  // show please open camera permission dialog
     *              }else{
     *                  // do nothing or toast tips
     *              }
     *          }
     *      }
     *      ...
     *  }
     *  ```
     *
     * @param permissions
     * @return 权限结果集 key:权限，value:[PermissionResult]
     * @receiver [Fragment]
     */
    context (Fragment)
    suspend fun requestPermission(permissions: List<String>): Map<String, PermissionResult>


}
