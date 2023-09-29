package `in`.coupsome.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import `in`.coupsome.base.model.AppData
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    var txn_id: String? = null,
    var txn_type: String? = null,
    var txn_amount: String? = null,
    var date: String? = null
) : Parcelable, AppData(){
    companion object diffUtil : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean =
            oldItem.txn_id == newItem.txn_id

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean =
            oldItem == newItem

    }
}
