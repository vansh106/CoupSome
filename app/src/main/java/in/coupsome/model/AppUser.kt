package `in`.coupsome.model

import android.os.Parcelable
import `in`.coupsome.base.model.AppData
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppUser(
    var fullName: String? = null,
    var phone: String? = null,
    var email: String? = null
) : Parcelable, AppData()