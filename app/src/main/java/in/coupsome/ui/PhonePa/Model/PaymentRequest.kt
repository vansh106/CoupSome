package `in`.coupsome.ui.PhonePa.Model

import com.google.gson.annotations.SerializedName

data class PaymentRequest(
    @SerializedName("request")
    val requestJson: String
)