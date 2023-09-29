package `in`.coupsome.ui.payment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.base.activity.BaseActivity
import `in`.coupsome.databinding.ActivityPaymentGatewayBinding
import `in`.coupsome.di.UsersReference
import `in`.coupsome.model.BuyCoupon
import org.json.JSONObject
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class PaymentGatewayActivity : BaseActivity<ActivityPaymentGatewayBinding>(ActivityPaymentGatewayBinding::inflate),
    PaymentResultListener {
    private val coupon by lazy { intent.getParcelableExtra<BuyCoupon>(ARG_COUPON) }

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    @UsersReference
    lateinit var userReference: DatabaseReference

    private val userId by lazy { auth.currentUser?.uid ?: "" }

    private val currentUserRef by lazy { userReference.child(userId) }
    override fun ActivityPaymentGatewayBinding.setupViews(savedInstanceState: Bundle?) {
        if (coupon != null)
            startPayment(coupon!!)
        else
            finish()
    }

    private fun startPayment(coupon: BuyCoupon) {
        val activity: Activity = this
        val co = Checkout()
        try {
            Checkout.preload(activity.applicationContext)
            val options = JSONObject()
            options.put("name", "CoupSome")
            options.put("send_sms_hash", true)
            options.put("description", "App Payment")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://i.ibb.co/dk7KSCQ/logo.png")
            options.put("currency", "INR")
            val payment = coupon.price
            // amount is in paise so please multiple it by 100
            //Payment failed Invalid amount (should be passed in integer paise. Minimum value is 100 paise, i.e. ₹ 1)
            var total = payment?.toDouble()
            total = total?.times(100)
            options.put("amount", total)
            val preFill = JSONObject()
            preFill.put("email", "coupsome@gmail.com")
            preFill.put("contact", coupon.phoneNo)
            options.put("prefill", preFill)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH] // Note: Month starts from 0 (January is 0)

        val day = calendar[Calendar.DAY_OF_MONTH]
        val currentDate = day.toString() + "/" + (month + 1) + "/" + year

        val couponMap: MutableMap<String, String?> = HashMap()
        couponMap["code"] = coupon?.code
        couponMap["brand"] = coupon?.brand
        couponMap["benefits"] = coupon?.benefits
        couponMap["category"] = coupon?.category
        currentUserRef
            .child("CouponsBought")
            .push().let {
                it.setValue(couponMap)
            }

        val txnMap: MutableMap<String, String?> = HashMap()
        txnMap["date"] = currentDate
        txnMap["txn_id"] = p0
        txnMap["txn_type"] = "Buy"
        txnMap["txn_amount"] = coupon?.price
        currentUserRef
            .child("txns")
            .push()
            .setValue(txnMap)

        coupon?.let { coupon ->
            userReference
                .child(coupon.userId!!)
                .child("my_sales")
                .child(coupon.key!!)
                .child("valid")
                .setValue("2")
            txnMap["txn_type"] = "Sale"
            userReference
                .child(coupon.userId!!)
                .child("txns")
                .push()
                .setValue(txnMap)
        }

        setResult(RESULT_OK)
        finish()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        finish()
    }

    companion object {
        private const val ARG_COUPON = "coupon"
    }

    class PaymentGatewayActivityResultContract : ActivityResultContract<BuyCoupon, Boolean>() {
        override fun createIntent(context: Context, input: BuyCoupon) =
            Intent(context, PaymentGatewayActivity::class.java)
                .putExtra(ARG_COUPON, input)

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean = resultCode == RESULT_OK
    }

}