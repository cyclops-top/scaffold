@file:Suppress("unused")

package scaffold.core

import androidx.annotation.IntRange

/**
 * Flag 标志位工具，使用value class 不会增加过多额外开销
 *
 * ```kotlin
 *  private val flagCreator = FlagCreator()
 *  private val FlagEmpty = flagCreator.createNext()
 *  private val Flag1 = flagCreator.createNext()
 *  private val Flag2 = flagCreator.createNext()
 *
 *  var current = FlagEmpty
 *
 *  //add flag 1
 *  current += Flag1
 *  //has flag1
 *  if(current contains Flag1){
 *      //do something
 *  }
 *  //use scope process
 *
 *  current.process{
 *      if(Flag1.has){
 *       //do something
 *      }
 *      if(Flag2.has){
 *       //do something
 *      }
 *  }
 * ```
 *
 * @sample scaffold.core.FlagUnitTest
 */
sealed interface Flag {
    val value: Long

    val isEmpty: Boolean get() = value == 0L

    /**
     * Add flag
     *
     * ```kotlin
     * var current = FlagEmpty
     * current += Flag1
     * ```
     *
     * @param flag
     * @return new Flag
     */
    operator fun plus(flag: Flag): Flag

    /**
     * remove flag
     *
     * ```kotlin
     * var current = Flag1
     * current -= Flag1
     * //current is empty
     * ```
     *
     * @param flag
     * @return new Flag
     */
    operator fun minus(flag: Flag): Flag

    /**
     * 是否包含该Flag 或者被包含
     *
     * ```kotlin
     * if(current contains Flag1){
     *   // do something
     * }
     * ```
     *
     * @param flag
     * @return
     */
    infix fun contains(flag: Flag): Boolean

    /**
     * 创建下一个标志，左移一位
     *
     * @return
     */
    fun createNext(): Flag

    @JvmInline
    private value class Default(override val value: Long) : Flag {
        override fun plus(flag: Flag): Flag {
            return Default(value or flag.value)
        }

        override fun minus(flag: Flag): Flag {
            return Default(value and flag.value.inv())
        }

        override fun contains(flag: Flag): Boolean {
            return (value and flag.value) > 0
        }

        override fun createNext(): Flag {
            if (value == 0L) {
                return Default(1L)
            }
            return Default(value shl 1)
        }

        override fun toString(): String {
            return "Flag(${value.toString(2)})"
        }
    }

    companion object {
        operator fun invoke(value: Long): Flag {
            return Default(value)
        }

        fun create(@IntRange(from = 0, to = 64) bitIndex: Int = 0): Flag {
            return if (bitIndex == 0) {
                Default(0L)
            } else {
                Default(1L shl (bitIndex - 1))
            }
        }
    }
}

/**
 * Flag scope 用于批量处理flag
 *
 * @property current
 * @see Flag.process
 */
@JvmInline
value class FlagScope(private val current: Flag) {
    /** 是否包含该标志 */
    val Flag.has: Boolean get() = current.contains(this)
}

/**
 * 对Flag批量处理
 *
 * ```kotlin
 * current.process{
 *  if(Flag1.has){
 *      //do something
 *  }
 *  if(Flag2.has){
 *      //do something
 *  }
 * }
 * ```
 *
 * @param block
 * @receiver Flag
 */
inline fun Flag.process(crossinline block: FlagScope.() -> Unit) {
    block(FlagScope(this))
}

/** Flag构建 */
class FlagCreator {
    private var bitIndex = 0
    fun createNext(): Flag {
        if (bitIndex > 64) {
            throw IllegalArgumentException("Flag supports maximum length of 64")
        }
        return Flag.create(bitIndex++)
    }
}