package `in`.coupsome.model

import android.os.Parcelable
import `in`.coupsome.base.model.AppData
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    var id: String? = null,
    var type: String? = null,
    var amount: String? = null,
    var date: String? = null
) : Parcelable, AppData()
