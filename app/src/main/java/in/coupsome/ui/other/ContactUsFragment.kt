package `in`.coupsome.ui.other

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.base.fragment.BaseFragment
import `in`.coupsome.databinding.FragmentContactUsBinding

@AndroidEntryPoint
class ContactUsFragment: BaseFragment<FragmentContactUsBinding>(
    FragmentContactUsBinding::inflate
) {
    override fun FragmentContactUsBinding.setupViews(savedInstanceState: Bundle?) {
        setTitle("Contact Us")
    }
}