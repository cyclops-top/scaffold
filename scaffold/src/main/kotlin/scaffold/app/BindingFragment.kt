package scaffold.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.viewbinding.ViewBinding

/**
 * Binding fragment
 *
 * ```kotlin
 * // MainFragmentBinding 来自 R.layout.main_fragment 通过 viewBinding 生成
 * class MainFragment:BindingFragment<MainFragmentBinding>(MainFragmentBinding::inflate)
 * ```
 *
 * @param VB ViewBinding
 */
abstract class BindingFragment<VB : ViewBinding>(val bindingCreator: (LayoutInflater, ViewGroup?, Boolean) -> VB) :
    Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!
    protected val isViewInit get() = _binding != null


    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = bindingCreator(inflater, container, false).also {
            it.root.setViewTreeLifecycleOwner(viewLifecycleOwner)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}