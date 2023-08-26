package `in`.coupsome.ui.profile

import android.os.Bundle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.R
import `in`.coupsome.base.fragment.BaseFragment
import `in`.coupsome.databinding.FragmentProfileBinding
import `in`.coupsome.ui.auth.AuthenticationActivity
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(
    FragmentProfileBinding::inflate
) {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun FragmentProfileBinding.setupViews(savedInstanceState: Bundle?) {
        setSubTitle(
            R.drawable.ic_user_m,
            firebaseAuth.currentUser?.displayName ?: "Guest"
        )
        tvAboutUs.setOnClickListener {
            navigateTo(ProfileFragmentDirections.actionProfileFragmentToAboutUsFragment())
        }
        tvFaq.setOnClickListener {
            navigateTo(ProfileFragmentDirections.actionProfileFragmentToFaqFragment())
        }
        tvContactUs.setOnClickListener {
            navigateTo(ProfileFragmentDirections.actionProfileFragmentToContactUsFragment())
        }
        tvLogout.setOnClickListener {
            firebaseAuth.signOut()
            AuthenticationActivity.start(requireContext())
        }
        tvMyCoupons.setOnClickListener {
            navigateTo(ProfileFragmentDirections.actionProfileFragmentToMyCouponsFragment())
        }
        tvMySales.setOnClickListener {
            navigateTo(ProfileFragmentDirections.actionProfileFragmentToMySalesFragment())
        }
        tvManageSales.setOnClickListener {
            navigateTo(ProfileFragmentDirections.actionProfileFragmentToManageSalesFragment())
        }
    }

    private fun navigateTo(navDirections: NavDirections) {
        findNavController().navigate(navDirections)
    }
}