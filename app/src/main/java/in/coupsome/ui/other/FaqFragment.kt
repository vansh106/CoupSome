package `in`.coupsome.ui.other

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.base.adapter.BaseAdapter
import `in`.coupsome.base.fragment.BaseFragment
import `in`.coupsome.base.model.AppData
import `in`.coupsome.databinding.FragmentRecyclerViewBinding
import `in`.coupsome.databinding.ItemFaqBinding

@AndroidEntryPoint
class FaqFragment : BaseFragment<FragmentRecyclerViewBinding>(FragmentRecyclerViewBinding::inflate) {
    private data class Faq(
        val question: String,
        val answer: String
    ) : AppData() {
        companion object diffUtil : DiffUtil.ItemCallback<Faq>() {
            override fun areItemsTheSame(oldItem: Faq, newItem: Faq): Boolean {
                return oldItem.question == newItem.question
            }

            override fun areContentsTheSame(oldItem: Faq, newItem: Faq): Boolean {
                return oldItem == newItem
            }
        }
    }

    private val faqs = listOf<Faq>(
        Faq(
            "What to do if coupons doesn't work after buying?",
            " We will refund your money, you need to send us transaction details through \"contact us\" page."
        ),
        Faq(
            "How to get coupons to sell?", "You can get coupons from rewards section in your payment apps. \n" +
                    "NOTE: SELL ONLY EXCLUSIVE COUPONS THAT ONLY YOU HAVE (eg: AJEWU8B8AEW), generic coupons such as \"SALE50\", \"SUMMER40\", etc will be rejected"
        ),
        Faq(
            "Are there any other ways of receiving sales money apart from UPI?",
            "You will have the feature of setting payment methods in upcoming version, as of now, you can contact us and we will help you."
        )
    )

    override fun FragmentRecyclerViewBinding.setupViews(savedInstanceState: Bundle?) {
        setTitle("Frequently Asked Questions")
        recyclerView.adapter = BaseAdapter(ItemFaqBinding::inflate, Faq).apply {
            submit(faqs)
            setOnViewHolderInflateListener { binding, data, _ ->
                binding.tvTitle.text = data.question
                binding.tvText.text = data.answer
            }
        }
    }
}