package `in`.coupsome.ui.PhonePa.Model.status

data class CheckStatusModel(
    val code: String,
    val `data`: Data,
    val message: String,
    val success: Boolean
)