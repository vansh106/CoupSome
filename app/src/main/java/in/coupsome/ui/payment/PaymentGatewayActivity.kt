package `in`.coupsome.ui.payment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.MainActivity
import `in`.coupsome.base.activity.BaseActivity
import `in`.coupsome.databinding.ActivityPaymentGatewayBinding
import `in`.coupsome.di.UsersReference
import `in`.coupsome.model.BuyCoupon
import `in`.coupsome.ui.PhonePa.ApiUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class PaymentGatewayActivity : BaseActivity<ActivityPaymentGatewayBinding>(ActivityPaymentGatewayBinding::inflate),
    PaymentResultListener {
    var apiEndPoint = "/pg/v1/pay"
    val salt = "70c411d2-50f1-4003-9883-d562f377d222" // salt key
    val MERCHANT_ID = "M1T7XRCXLY46"  // Merhcant id
    val MERCHANT_TID = "txnIdqq"+System.currentTimeMillis().toString()
    val BASE_URL = "https://api-preprod.phonepe.com/"


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
    // PhonePe
    private fun startPayment(coupon: BuyCoupon) {
        PhonePe.init(this, PhonePeEnvironment.RELEASE, MERCHANT_ID, "0d2d9a28894e44038cd5c96defdbc03a")
        val data = JSONObject()
        data.put("merchantTransactionId", MERCHANT_TID)//String. Mandatory
        data.put("merchantId" , MERCHANT_ID) //String. Mandatory
        data.put("amount",coupon.price!!.toInt()*100)//Long. Mandatory
        data.put("mobileNumber", coupon.phoneNo) //String. Optional
        data.put("callbackUrl", "coupsome@gmail.com") //String. Mandatory

        val paymentInstrument = JSONObject()
        paymentInstrument.put("type", "UPI_INTENT")
        paymentInstrument.put("targetApp", "net.one97.paytm")

        data.put("paymentInstrument", paymentInstrument )//OBJECT. Mandatory


        val deviceContext = JSONObject()
        deviceContext.put("deviceOS", "ANDROID")
        data.put("deviceContext", deviceContext)


//        val base64Body = android.util.Base64(Gson().toJson(data))

        val payloadBase64 = Base64.encodeToString(data.toString().toByteArray(Charset.defaultCharset()), Base64.NO_WRAP)
        val checksum = sha256(payloadBase64 + apiEndPoint + salt) + "###1";

        Log.d("rk", "onCreate: payload $payloadBase64")
        Log.d("rk", "onCreate: payload 1")
        Log.d("rk", "onCreate: checksum $checksum")

        val b2BPGRequest = B2BPGRequestBuilder()
            .setData(payloadBase64)
            .setChecksum(checksum)
            .setUrl(apiEndPoint)
            .build()


//        val button = findViewById<Button>(R.id.button)
//        button.setOnClickListener {
//            //For SDK call below function

        try {
            PhonePe.getImplicitIntent(this, b2BPGRequest, "net.one97.paytm")
                ?.let { startActivityForResult(it, 1112) };
        } catch (e: PhonePeInitException) {
            Log.e("PaymentGatewayActivity", "startPayment: ", e)
        }

//        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1112 && resultCode== RESULT_OK) {
            Log.d("rk", "onActivityResult: $data")
            Log.d("rk", "onActivityResult: ${data!!.data}")
            checkStatus()

            /*This callback indicates only about completion of UI flow.
            Inform your server to make the transaction
            status call to get the status. Update your app with the
            success/failure status.*/
        }
    }
    private fun checkStatus() {

        val xVerify =
            sha256("/pg/v1/status/$MERCHANT_ID/${MERCHANT_TID}${salt}") + "###1"

        Log.d("rk", "onCreate  xverify : $xVerify")


        val headers = mapOf(
            "Content-Type" to "application/json",
            "X-VERIFY" to xVerify,
            "X-MERCHANT-ID" to MERCHANT_ID,
        )

        lifecycleScope.launch(Dispatchers.IO) {

            val res = ApiUtilities.getApiInterface().checkStatus(MERCHANT_ID, MERCHANT_TID, headers)

            withContext(Dispatchers.Main) {

                Log.d("rk", "onCreate: S ${res.body()}")

                if (res.body() != null && res.body()!!.success) {
                    Log.d("rk", "onCreate: ${res.body()!!.message}")
                    Toast.makeText(
                        this@PaymentGatewayActivity,
                        res.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    if (res.body()!!.data.state == "COMPLETED") {
                        onPaymentSuccess(MERCHANT_TID)
                        startActivity(Intent(this@PaymentGatewayActivity, MainActivity::class.java))
                    }
                    else
                    {
                        startActivity(Intent(this@PaymentGatewayActivity, MainActivity::class.java))
                    }
                }
            }
        }
    }
    private fun sha256(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
    // RosePay
    private fun startPayment2(coupon: BuyCoupon) {
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