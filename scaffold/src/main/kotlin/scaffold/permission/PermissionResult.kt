package scaffold.permission

import android.os.Build
import androidx.fragment.app.Fragment


/**
 * 权限结果
 *
 * @constructor Create empty Permission result
 */
sealed interface PermissionResult {
    /** 是否授权 */
    val granted: Boolean get() = this == Granted

    /** 已授权 */
    object Granted : PermissionResult

    /**
     * 已拒绝
     *
     * @property shouldShowRationale 是否应该显示提示
     */
    data class Denied(val shouldShowRationale: Boolean) : PermissionResult

    companion object {
        context (Fragment) internal operator fun invoke(
            permission: String,
            granted: Boolean,
        ): PermissionResult {
            return if (granted) {
                Granted
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && (permission == android.Manifest.permission.READ_EXTERNAL_STORAGE || permission == android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Granted
            } else {
                Denied(shouldShowRequestPermissionRationale(permission))
            }
        }
    }
}