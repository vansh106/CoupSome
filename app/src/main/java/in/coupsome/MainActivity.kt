package `in`.coupsome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.base.activity.BaseActivity
import `in`.coupsome.databinding.ActivityMainBinding
import `in`.coupsome.di.UsersReference
import `in`.coupsome.ui.auth.AuthenticationActivity
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private lateinit var navController: NavController

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    @UsersReference
    lateinit var usersReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refreshToken()
    }

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
        auth.addAuthStateListener {
            if (auth.currentUser == null) {
                AuthenticationActivity.start(this@MainActivity)
                finish()
            }
        }
    }

    private fun refreshToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result
                auth.currentUser?.uid?.let { uid ->
                    usersReference.child(uid).child("token").setValue(token)
                }
            }
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