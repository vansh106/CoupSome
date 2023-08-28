package `in`.coupsome.ui.other

import android.content.Intent
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
        val recipientList = "coupsome@gmail.com"

        btnSend.setOnClickListener {
            val recipient = recipientList.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val subject: String = etSubject.text.toString().trim { it <= ' ' }
            val message: String = etMessage.text.toString().trim { it <= ' ' }
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_EMAIL, recipient)
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.type = "message/rfc822"
            startActivity(Intent.createChooser(intent, "Choose an email client"))
        }
    }
}