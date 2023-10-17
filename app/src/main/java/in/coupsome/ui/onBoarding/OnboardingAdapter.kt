package `in`.coupsome.ui.onBoarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.coupsome.R

class OnboardingAdapter(private val onBoardingItems: List<OnBoardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_container, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.setOnBoardingData(onBoardingItems[position])
    }

    override fun getItemCount(): Int {
        return onBoardingItems.size
    }

    inner class OnboardingViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textTitle: TextView
        private val textDescription: TextView
        private val imageOnboarding: ImageView

        init {
            textTitle = itemView.findViewById<TextView>(R.id.textTitle)
            textDescription = itemView.findViewById<TextView>(R.id.textDescription)
            imageOnboarding = itemView.findViewById<ImageView>(R.id.imageOnboarding)
        }

        fun setOnBoardingData(onBoardingItem: OnBoardingItem) {
            textTitle.text = onBoardingItem.title
            textDescription.text = onBoardingItem.description
            imageOnboarding.setImageResource(onBoardingItem.image)
        }
    }
}