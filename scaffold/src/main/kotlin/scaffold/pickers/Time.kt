@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package scaffold.pickers

import androidx.fragment.app.Fragment
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.coroutines.suspendCancellableCoroutine
import scaffold.PickerScope
import kotlin.coroutines.resume

/**
 * 时间选择
 * ```kotlin
 *  class TestFragment:Fragment(){
 *  ...
 *     lifecycleScope.launch{
 *        val time = ToolBox.Pickers.time()
 *     }
 *  ...
 *  }
 * ```
 * @param config 配置Picker
 * @receiver [Fragment],[PickerScope]
 * @return
 */
context (Fragment)
        suspend fun PickerScope.time(config: MaterialTimePicker.Builder.() -> MaterialTimePicker.Builder = { this }): Time? {
    val builder = MaterialTimePicker.Builder()
    config(builder)
    val picker = builder.build()
    return suspendCancellableCoroutine { co ->
        picker.addOnCancelListener {
            co.resume(null)
        }
        picker.addOnPositiveButtonClickListener {
            co.resume(Time(picker.hour, picker.minute))
        }
        picker.addOnNegativeButtonClickListener {
            co.resume(null)
        }
        picker.show(this@Fragment.parentFragmentManager, "MaterialTimePicker")
    }
}

/** Time */
@JvmInline
value class Time private constructor(private val value: Pair<Int, Int>) {
    constructor(hour: Int, minute: Int) : this(hour to minute)

    val hour: Int get() = value.first
    val minute: Int get() = value.second

    fun component1(): Int = hour
    fun component2(): Int = minute
    override fun toString(): String {
        return "Time($hour:$minute)"
    }
}
