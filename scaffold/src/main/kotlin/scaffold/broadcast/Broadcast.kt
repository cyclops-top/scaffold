package scaffold.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import scaffold.ToolBoxScope

/**
 * Broadcast stream
 * ```kotlin
 * ToolBox.broadcast(IntentFilter(SMS_RECEIVED_ACTION))
 *  .collectLatest {intent->
 *      //... parser sms data
 *  }
 * ```
 * @param filter Broadcast's filter
 * @return
 * @receiver [ToolBoxScope]
 */
context (Fragment)
fun ToolBoxScope.broadcast(filter: IntentFilter): Flow<Intent> {
    return callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(intent)
            }
        }
        requireContext().registerReceiver(receiver, filter)
        awaitClose {
            requireContext().unregisterReceiver(receiver)
        }
    }
}