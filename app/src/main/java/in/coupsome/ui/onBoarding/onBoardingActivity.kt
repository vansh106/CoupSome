package `in`.coupsome.ui.onBoarding

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import `in`.coupsome.MainActivity
import `in`.coupsome.R
import `in`.coupsome.base.activity.BaseActivity
import `in`.coupsome.databinding.ActivityOnBoardingBinding
import `in`.coupsome.ui.auth.AuthenticationActivity


class onBoardingActivity : BaseActivity<ActivityOnBoardingBinding>(ActivityOnBoardingBinding::inflate){

    private var onboardingAdapter: OnboardingAdapter? = null
    lateinit var  slideInAnimation:Animation

    override fun ActivityOnBoardingBinding.setupViews(savedInstanceState: Bundle?) {
        setOnboardingItem()
        try {
            slideInAnimation = AnimationUtils.loadAnimation(this@onBoardingActivity, R.anim.button_animation)
            slideInAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    buttonOnBoardingAction.visibility = View.VISIBLE
                }
                override fun onAnimationRepeat(animation: Animation) {}
            })

        }catch (e:Exception)
        {
            Log.d("rk",e.message.toString())
        }

        val onboardingViewPager = findViewById<ViewPager2>(R.id.onboardingViewPager)
        onboardingViewPager.adapter = onboardingAdapter

        setOnboadingIndicator()
        setCurrentOnboardingIndicators(0)

        onboardingViewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentOnboardingIndicators(position)
            }
        })

        buttonOnBoardingAction.setOnClickListener {
            if (onboardingViewPager.currentItem + 1 < onboardingAdapter!!.itemCount) {
                onboardingViewPager.currentItem = onboardingViewPager.currentItem + 1
            } else {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }
        skipBtn.setOnClickListener {
            startActivity(Intent(this@onBoardingActivity,AuthenticationActivity::class.java))
            finish()
        }
    }

    private fun ActivityOnBoardingBinding.setOnboadingIndicator() {
        val indicators = arrayOfNulls<ImageView>(
            onboardingAdapter!!.itemCount
        )
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext, R.drawable.onboarding_indicator_inactive
                )
            )
            indicators[i]!!.layoutParams = layoutParams
            layoutOnboardingIndicators.addView(indicators[i])
        }
    }
    @SuppressLint("SetTextI18n")
    private fun ActivityOnBoardingBinding.setCurrentOnboardingIndicators(index: Int) {
        val childCount =layoutOnboardingIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = layoutOnboardingIndicators.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.onboarding_indicator_active))
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.onboarding_indicator_inactive))
            }
        }
        if (index == onboardingAdapter!!.itemCount - 1) {
            buttonOnBoardingAction.text = "Start"
            buttonOnBoardingAction.startAnimation(slideInAnimation)
        } else {
            buttonOnBoardingAction  .text = "Next"
        }
    }


    private fun ActivityOnBoardingBinding.setOnboardingItem() {
        val onBoardingItems = ArrayList<OnBoardingItem>()
        val item1 = OnBoardingItem("", "", R.drawable.image1)
        val item2 = OnBoardingItem("", "", R.drawable.image6)
        val item3 = OnBoardingItem("", "", R.drawable.image8)
        val item4 = OnBoardingItem("", "", R.drawable.image10)
        val item5 = OnBoardingItem("", "", R.drawable.image11)
        onBoardingItems.add(item1)
        onBoardingItems.add(item2)
        onBoardingItems.add(item3)
        onBoardingItems.add(item4)
        onBoardingItems.add(item5)
        onboardingAdapter = OnboardingAdapter(onBoardingItems)
    }

}