package `in`.coupsome.ui.coupon

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.base.fragment.BaseFragment
import `in`.coupsome.databinding.FragmentAddCouponBinding
import `in`.coupsome.di.AllCouponsReference
import `in`.coupsome.di.UsersReference
import `in`.coupsome.model.BuyCoupon
import `in`.coupsome.model.PaymentMode
import javax.inject.Inject

@AndroidEntryPoint
class AddCouponFragment : BaseFragment<FragmentAddCouponBinding>(
    FragmentAddCouponBinding::inflate
) {
    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    @UsersReference
    lateinit var userReference: DatabaseReference

    @Inject
    @AllCouponsReference
    lateinit var allCouponsReference: DatabaseReference

    private val userId by lazy { auth.currentUser?.uid ?: "" }

    override fun FragmentAddCouponBinding.setupViews(savedInstanceState: Bundle?) {
        setTitle("Add Coupons for sale")
        btnSell.setOnClickListener { sellCoupon() }
    }

    private fun FragmentAddCouponBinding.sellCoupon() {
        if (etBrand.text.isNullOrEmpty()) {
            tilBrand.error = "Please enter brand name"
            return
        }
        if (etBenefits.text.isNullOrEmpty()) {
            tilBenefits.error = "Please enter benefits"
            return
        }
        if (etSellingPrice.text.isNullOrEmpty()) {
            tilSellingPrice.error = "Please enter price"
            return
        }
        if (spinnerCategories.selectedItemPosition == 0) {
            showToast("Please select category")
            return
        }
        if (etCouponCode.text.isNullOrEmpty()) {
            tilCouponCode.error = "Please enter coupon code"
            return
        }
        val coupon = BuyCoupon(
            benefits = etBenefits.text.toString(),
            price = etSellingPrice.text.toString(),
            brand = etBrand.text.toString(),
            category = spinnerCategories.selectedItem.toString(),
            code = etCouponCode.text.toString(),
            valid = PaymentMode.ADDED.value
        )
        userReference.child(userId).child("my_sales").push().setValue(
            coupon
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                showToast("Coupon added successfully")
                etBrand.text?.clear()
                etBenefits.text?.clear()
                etSellingPrice.text?.clear()
                etCouponCode.text?.clear()
            } else {
                showToast("Something went wrong")
            }
        }
        allCouponsReference.push().setValue(coupon)
    }
}