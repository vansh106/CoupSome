package `in`.coupsome.ui.onBoarding

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import `in`.coupsome.MainActivity
import `in`.coupsome.R
import `in`.coupsome.ui.auth.AuthenticationActivity

class onBoardingActivity : AppCompatActivity() {
    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var layoutOnboardingIndicator: LinearLayout
    private lateinit var buttonOnboardingAction: MaterialButton
    private lateinit var skipBtn: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        if(isOnboardingCompleted())
        {
            startActivity(Intent(applicationContext, AuthenticationActivity::class.java))
            finish()
        }
        else
        {
            layoutOnboardingIndicator = findViewById<LinearLayout>(R.id.layoutOnboardingIndicators)
            buttonOnboardingAction = findViewById<MaterialButton>(R.id.buttonOnBoardingAction)
            skipBtn=findViewById(R.id.skipBtn)
            setOnboardingItem()


            val onboardingViewPager = findViewById<ViewPager2>(R.id.onboardingViewPager)
            onboardingViewPager.adapter = onboardingAdapter

            setOnboadingIndicator()
            setCurrentOnboardingIndicators(0)
            try {
                skipBtn.setOnClickListener {
                    startActivity(Intent(applicationContext, AuthenticationActivity::class.java))
                    finish()
                }
            }catch (e:Exception)
            {
                Log.d("rk",e.message.toString())
            }

            onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentOnboardingIndicators(position)
                }
            })

            buttonOnboardingAction.setOnClickListener{
                if (onboardingViewPager.currentItem + 1 < onboardingAdapter!!.itemCount) {
                    onboardingViewPager.currentItem = onboardingViewPager.currentItem + 1
                } else {
                    markOnboardingCompleted()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            }

        }


    }

    private fun setOnboadingIndicator() {
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
            layoutOnboardingIndicator!!.addView(indicators[i])
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setCurrentOnboardingIndicators(index: Int) {
        val childCount = layoutOnboardingIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = layoutOnboardingIndicator.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.onboarding_indicator_active))
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.onboarding_indicator_inactive))
            }
        }
        if (index == onboardingAdapter.itemCount - 1) {
            buttonOnboardingAction.text = "Start"
        } else {
            buttonOnboardingAction.text = "Next"
        }
    }

    private fun setOnboardingItem() {
        val onBoardingItems = ArrayList<OnBoardingItem>()

        val itemFastFood = OnBoardingItem("Fast Delivery to your home","Our delivery partner is on the way to your home!",R.drawable.img1)
        val itemPayOnline = OnBoardingItem("E-Pay your bill online","Electric bill payment is a feature of online, mobile and net banking!",R.drawable.img2)
        val itemEatTogether = OnBoardingItem( "Eat together","Enjoy your meal and have a great day. Don't forget to rate us!",R.drawable.img3)
        onBoardingItems.add(itemFastFood)
        onBoardingItems.add(itemPayOnline)
        onBoardingItems.add(itemEatTogether)
        onboardingAdapter = OnboardingAdapter(onBoardingItems)
    }
    private fun isOnboardingCompleted(): Boolean {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPreferences.getBoolean("onboarding_completed", false)
    }

    // Method to mark onboarding as completed
    private fun markOnboardingCompleted() {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("onboarding_completed", true)
        editor.apply()
    }
}