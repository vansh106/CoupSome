package `in`.coupsome.ui.PhonePa

import `in`.coupsome.ui.PhonePa.Model.Data


data class ApiResponse(
    val code: String,
    val `data`: Data,
    val message: String,
    val success: Boolean
)