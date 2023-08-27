package `in`.coupsome.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.MainActivity
import `in`.coupsome.R
import `in`.coupsome.base.activity.BaseActivity
import `in`.coupsome.databinding.ActivityAuthBinding
import `in`.coupsome.di.UsersReference
import `in`.coupsome.model.AppUser
import javax.inject.Inject

@AndroidEntryPoint
class AuthenticationActivity : BaseActivity<ActivityAuthBinding>(ActivityAuthBinding::inflate) {
    private var googleClient: GoogleSignInClient? = null

    private val RC_SIGN_IN = 100

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    @UsersReference
    lateinit var userReference: DatabaseReference

    override fun ActivityAuthBinding.setupViews(savedInstanceState: Bundle?) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleClient = GoogleSignIn.getClient(this@AuthenticationActivity, gso)
    }

    fun googleSignIn() {
        googleClient?.signInIntent?.let {
            startActivityForResult(it, RC_SIGN_IN)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { firebaseAuth(it) }
            } catch (e: ApiException) {
                showToast("Error signing in!$e")
            }
        }
    }

    private fun firebaseAuth(idToken: String) {
        val credentials = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credentials).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                auth.currentUser?.let {
                    userReference.child(it.uid).setValue(
                        AppUser(
                            fullName = it.displayName,
                            phone = it.phoneNumber,
                            email = it.email,
                        )
                    )
                }
                MainActivity.start(this@AuthenticationActivity)
            } else {
                Log.d("AuthenticationActivity.kt", "YASH => firebaseAuth:80 ${task.exception}")
                showToast("Error signing in!")
            }
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, AuthenticationActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

}