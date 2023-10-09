package `in`.coupsome.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.MainActivity
import `in`.coupsome.base.fragment.BaseFragment
import `in`.coupsome.databinding.FragmentLoginBinding
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate
) {

    @Inject
    lateinit var auth: FirebaseAuth

    override fun FragmentLoginBinding.setupViews(savedInstanceState: Bundle?) {
        btnSignUp.setOnClickListener {
            findNavController().navigateUp()
        }
        btnLogin.setOnClickListener { userLogin() }
        btnGoogle.setOnClickListener { (requireActivity() as AuthenticationActivity).googleSignIn() }
    }

    private fun FragmentLoginBinding.userLogin() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        when {
            email.isEmpty() -> {
                tilEmail.error = "Email is required!"
                etEmail.requestFocus()
                return
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                tilEmail.error = "Please enter a valid email!"
                etEmail.requestFocus()
                return
            }

            password.isEmpty() -> {
                tilEmail.error = "Password is required!"
                etEmail.requestFocus()
                return
            }

            password.length < 6 -> {
                tilEmail.error = "Password should be of min 6 characters!"
                etEmail.requestFocus()
                return
            }

            else -> {
                progressBar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (auth.currentUser?.isEmailVerified == true) {
                                showToast("User Verified!")
                                progressBar.visibility = View.INVISIBLE
                                MainActivity.start(requireContext())
                                requireActivity().finish()
                            } else {
                                showToast("User NOT Verified!")
                                progressBar.visibility = View.INVISIBLE
                            }
                        } else {
                            showToast("Invalid Credentials!")
                            progressBar.visibility = View.INVISIBLE
                        }
                    }
            }
        }
    }
}