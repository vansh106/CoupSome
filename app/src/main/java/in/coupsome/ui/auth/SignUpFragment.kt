package `in`.coupsome.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.base.fragment.BaseFragment
import `in`.coupsome.databinding.FragmentSignupBinding
import `in`.coupsome.model.AppUser
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignupBinding>(
    FragmentSignupBinding::inflate
) {

    @Inject
    lateinit var auth: FirebaseAuth


    override fun FragmentSignupBinding.setupViews(savedInstanceState: Bundle?) {
        btnSignIn.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
        }
        btnSignUp.setOnClickListener { registerUser() }
        btnGoogle.setOnClickListener { (requireActivity() as AuthenticationActivity).googleSignIn() }
    }

    private fun FragmentSignupBinding.registerUser() {
        val email: String = etEmail.text.toString().trim()
        val fullName: String = etName.text.toString().trim()
        val phoneNumber: String = etPhone.text.toString().trim()
        val password: String = etPassword.text.toString().trim()
        when {
            fullName.isEmpty() -> {
                tilName.error = "Name is Required!"
                etName.requestFocus()
                return
            }
            phoneNumber.isEmpty() -> {
                tilPhone.error = "Phone number is required!"
                etPhone.requestFocus()
                return
            }
            email.isEmpty() -> {
                tilEmail.error = "Email ID is required!"
                etEmail.requestFocus()
                return
            }
            password.isEmpty() -> {
                tilPassword.error = "Password is required!"
                etPassword.requestFocus()
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                tilEmail.error = "Please provide valid email!"
                etEmail.requestFocus()
                return
            }
            password.length < 6 -> {
                tilPassword.error = "Password should be of min 6 characters!"
                etPassword.requestFocus()
                return
            }
            else -> {
                progressBar.visibility = View.VISIBLE
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            task.result.user?.let {
                                userProfileChangeRequest {
                                    displayName = fullName
                                }.let { request ->
                                    it.updateProfile(request)
                                }
                            }

                            val user = AppUser(fullName, phoneNumber, email)
                            FirebaseDatabase.getInstance().getReference("Users").child(
                                FirebaseAuth.getInstance().currentUser!!.uid
                            )
                                .setValue(user).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        auth.currentUser?.sendEmailVerification()
                                            ?.addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    showToast("User registered successfully, PLEASE VERIFY YOUR MAIL ID!")
                                                    etName.setText("")
                                                    etPhone.setText("")
                                                    etEmail.setText("")
                                                    etPassword.setText("")
                                                    progressBar.visibility = View.INVISIBLE
                                                }
                                            }
                                    } else {
                                        showToast("Failed to register! Please Try Again")
                                        progressBar.visibility = View.INVISIBLE
                                    }
                                }
                        } else {
                            showToast("Error: Email already taken!")
                            progressBar.visibility = View.INVISIBLE
                        }
                    }
            }
        }
    }
}