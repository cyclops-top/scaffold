package scaffold.simple

import dagger.hilt.android.AndroidEntryPoint
import scaffold.app.BindingFragment
import scaffold.simple.databinding.FragmentMainBinding

@AndroidEntryPoint
class MainFragment : BindingFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {


}