package `in`.coupsome.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
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
        val text = "By signing up, you agree to our Terms of Use and Privacy Policy"
        val spannableString = SpannableString(text)
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                try {
                    startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://pdfhost.io/v/amZtrmuYO_privacy_policy"))
                    )
                } catch (e: Exception) {
                    showToast("Unable to view privacy policy!")
                }
            }
        }, text.indexOf("Privacy"), text.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                try {
                    // TODO@vansh: 29/09/23 change this link
                    startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://azure-honey-31.tiiny.site/"))
                    )
                } catch (e: Exception) {
                    showToast("Unable to view privacy policy!")
                }
            }
        }, text.indexOf("Terms"), text.indexOf(" and"), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvTerms.text = spannableString
        tvTerms.movementMethod = LinkMovementMethod.getInstance()
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