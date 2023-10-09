package `in`.coupsome.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import `in`.coupsome.base.model.AppData
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class BuyCoupon(
    var name: String? = null,
    var benefits: String? = null,
    var price: String? = null,
    var brand: String? = null,
    var phoneNo: String? = null,
    var code: String? = null,
    var valid: String? = null,
    var category: String? = null,
    var key: String? = null,
    var userId: String? = null
) : Parcelable, AppData() {

    companion object diffUtil : DiffUtil.ItemCallback<BuyCoupon>() {
        override fun areItemsTheSame(oldItem: BuyCoupon, newItem: BuyCoupon): Boolean =
            oldItem.key == newItem.key

        override fun areContentsTheSame(oldItem: BuyCoupon, newItem: BuyCoupon): Boolean =
            oldItem == newItem

    }
}
