@file:Suppress("UnusedReceiverParameter","unused")

package scaffold


/**
 * 工具箱作用域，仅用于拓展
 */
sealed interface ToolBoxScope
private object DefaultToolBoxScope : ToolBoxScope


/**
 * 目的地作用域，仅用于拓展
 */
sealed interface DestinationScope
private object DefaultDestinationScope : DestinationScope

/**
 * 选择器作用域，仅用于拓展
 */
sealed interface PickerScope
private object DefaultPickerScope : PickerScope

/**
 * @see [DestinationScope]
 */
val ToolBoxScope.Destinations: DestinationScope get() = DefaultDestinationScope

/**
 * @see [PickerScope]
 */
val ToolBoxScope.Pickers: PickerScope get() = DefaultPickerScope

/**
 * @see [ToolBoxScope]
 */
val ToolBox: ToolBoxScope get() = DefaultToolBoxScope
