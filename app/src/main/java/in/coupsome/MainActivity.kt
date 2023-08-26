package `in`.coupsome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.base.activity.BaseActivity
import `in`.coupsome.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private lateinit var navController: NavController

    override fun ActivityMainBinding.setupViews(savedInstanceState: Bundle?) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.findNavController()
        bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, navDestination, _ ->
            ivBack.isVisible = when (navDestination.id) {
                R.id.buyCouponsFragment,
                R.id.addCouponFragment,
                R.id.profileFragment -> false

                else -> true
            }
        }
        ivBack.setOnClickListener {
            navController.navigateUp()
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            context.startActivity(starter)
        }
    }
}