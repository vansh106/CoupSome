package `in`.coupsome

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.ui.auth.AuthenticationActivity
import `in`.coupsome.base.activity.BaseActivity
import `in`.coupsome.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { true }
        }
    }

    override fun ActivitySplashBinding.setupViews(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            delay(500)
            if (true ?: firebaseAuth.currentUser != null) {
                MainActivity.start(this@SplashActivity)
                finish()
            } else {
                AuthenticationActivity.start(this@SplashActivity)
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}