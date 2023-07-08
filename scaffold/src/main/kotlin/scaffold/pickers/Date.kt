@file:Suppress("unused")

package scaffold.pickers

import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.suspendCancellableCoroutine
import scaffold.PickerScope
import kotlin.coroutines.resume

/**
 * MaterialDatePicker callback 转协程
 *
 * @param S 时间类型
 * @return 选择结果
 * @receiver Fragment
 */
context (Fragment)
        private suspend fun <S> MaterialDatePicker<S>.await(): S? {
    val picker = this
    return suspendCancellableCoroutine { co ->
        picker.addOnCancelListener {
            if (co.isActive) {
                co.resume(null)
            }
        }
        picker.addOnPositiveButtonClickListener {
            if (co.isActive) {
                co.resume(it)
            }
        }
        picker.addOnNegativeButtonClickListener {
            if (co.isActive) {
                co.resume(null)
            }
        }
        picker.show(this@Fragment.parentFragmentManager, "MaterialDatePicker")
    }
}

/**
 * 选择日期
 *
 * ```kotlin
 * Fragment{
 *  ...
 *  lifecycleScope.launch{
 *      val date = ToolBox.Pickers.date()
 *      ...
 *  }
 *  ...
 * }
 *
 * ```
 *
 * @param block 配置选择器 [MaterialDatePicker.Builder]
 * @return 日期
 * @receiver [Fragment],[PickerScope]
 */
context (Fragment)
        suspend fun PickerScope.date(block: MaterialDatePicker.Builder<Long>.() -> MaterialDatePicker.Builder<Long> = { this }): Long? {
    val builder = MaterialDatePicker.Builder.datePicker()
    val picker = block(builder).build()
    return picker.await()
}

/**
 * 选择日期范围
 *
 * ```kotlin
 * Fragment{
 *  ...
 *  lifecycleScope.launch{
 *      val dateRange = ToolBox.Pickers.dateRange()
 *      ...
 *  }
 *  ...
 * }
 * ```
 *
 * @param block 配置选择器 [MaterialDatePicker.Builder]
 * @return 日期范围
 * @receiver [Fragment],[PickerScope]
 */
context (Fragment)
        suspend fun PickerScope.dateRange(block: MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>>.() -> MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> = { this }): LongRange? {
    val builder = MaterialDatePicker.Builder.dateRangePicker()
    val picker = block(builder).build()
    return picker.await()?.let {
        it.first..it.second
    }
}