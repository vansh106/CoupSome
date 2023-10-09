package `in`.coupsome.ui.other

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.base.adapter.BaseAdapter
import `in`.coupsome.base.fragment.BaseFragment
import `in`.coupsome.base.model.AppData
import `in`.coupsome.databinding.FragmentRecyclerViewBinding
import `in`.coupsome.databinding.ItemAboutBinding

@AndroidEntryPoint
class AboutUsFragment : BaseFragment<FragmentRecyclerViewBinding>(FragmentRecyclerViewBinding::inflate) {

    private data class ListItem(
        val title: String,
        val description: String
    ) : AppData() {
        companion object diffUtil : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    private val list = listOf(
        ListItem(
            "About Us",
            "In a bustling city, Vansh, CEO of Coupsome, stumbled upon a food delivery app coupon he didn't need and gave it to a friend. This small act sparked an idea, leading him to create Coupsome, a marketplace for buying and selling coupons. With a dedicated team, they crafted a user-friendly platform connecting people with unused exclusive coupons to those seeking discounts. Coupsome flourished, fostering a sense of community and making saving money an enjoyable experience. Vansh's passion for helping people save and share grew, making Coupsome a household name and an inspiring tale of transforming simple ideas into extraordinary ventures."
        ),
        ListItem(
            "Our Mission",
            "At Coupsome, our mission is to empower users to both earn and save money through our dynamic and user-friendly marketplace for buying and selling coupons. We are committed to revolutionizing the couponing landscape, creating a vibrant community of savvy consumers who can effortlessly access and share enticing discounts on a wide range of products and services. By fostering a culture of sustainable coupon usage, we aim to inspire smart spending habits and drive positive financial impact, allowing our users to maximize their savings potential while also generating extra income by selling their unused exclusive coupons."
        ),
        ListItem(
            "Who we serve",
            "Coupsome's target audience comprises teenagers, college students, and individuals seeking discounts and extra income. Teenagers and college students, with free time on their hands, are drawn to the platform due to their limited budgets, seeking cost-saving opportunities to enjoy experiences without overspending. For these tech-savvy users, Coupsome provides a convenient way to buy and sell coupons online. Additionally, college students, juggling studies and part-time jobs, find the platform an attractive avenue to earn extra income by selling unused coupons. With an array of exclusive deals and offers, the platform creates a win-win situation, connecting those in need of savings with entrepreneurial-minded sellers, fostering a sense of community and rewarding couponing experience for all."
        ),
        ListItem(
            "Our Team",
            "Coupsome was founded in 2023 by Vansh Bulani, who is a curious and passionate individual with a strong interest in computer science, android development, and entrepreneurship. Exploring the ever-evolving world of technology excites him, and he finds joy in programming, problem-solving, and creating user-friendly mobile applications. His entrepreneurial spirit drives him to seek out opportunities, devise effective business strategies, and collaborate with like-minded individuals. Constantly eager to learn and grow, he embraces challenges and approaches every endeavor with enthusiasm and determination. As of now, he is the only registered individual in the company working with freelancers."
        ),
    )

    override fun FragmentRecyclerViewBinding.setupViews(savedInstanceState: Bundle?) {
        setTitle("About Us")
        recyclerView.adapter = BaseAdapter(ItemAboutBinding::inflate, ListItem).apply {
            submit(list)
            setOnViewHolderInflateListener { binding, data, _ ->
                binding.tvTitle.text = data.title
                binding.tvText.text = data.description
            }
        }
    }
}