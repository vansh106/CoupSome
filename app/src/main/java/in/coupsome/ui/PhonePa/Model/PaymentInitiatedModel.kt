package `in`.coupsome.ui.PhonePa.Model

data class PaymentInitiatedModel(
    val code: String,
    val `data`: Data,
    val message: String,
    val success: Boolean
)