package scaffold.simple

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import scaffold.permission.PermissionDispatchersHost
import scaffold.simple.databinding.ActivityMainBinding
import javax.inject.Inject

/**
 * Main activity
 *
 * @constructor Create empty Main activity
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var guardDispatchersHost: PermissionDispatchersHost
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guardDispatchersHost.register()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.post {
            binding.toolbar.setupWithNavController(binding.myNavHostFragment.findNavController())
        }

    }
}