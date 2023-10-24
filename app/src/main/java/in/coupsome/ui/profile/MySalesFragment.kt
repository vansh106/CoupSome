package `in`.coupsome.ui.profile

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.base.adapter.BaseAdapter
import `in`.coupsome.base.fragment.BaseFragment
import `in`.coupsome.databinding.FragmentRedeemBinding
import `in`.coupsome.databinding.ItemSaleBinding
import `in`.coupsome.di.UsersReference
import `in`.coupsome.model.BuyCoupon
import `in`.coupsome.model.PaymentMode
import javax.inject.Inject

@AndroidEntryPoint
class MySalesFragment : BaseFragment<FragmentRedeemBinding>(
    FragmentRedeemBinding::inflate
), ValueEventListener {
    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    @UsersReference
    lateinit var userReference: DatabaseReference

    private val userId by lazy { auth.currentUser?.uid ?: "" }

    private val adapter by lazy {
        BaseAdapter(
            ItemSaleBinding::inflate,
            BuyCoupon.diffUtil
        ).apply {
            setOnViewHolderInflateListener { binding, data, _ ->
                with(binding) {
                    tvTitle.text = data.brand
                    tvDescription.text = data.benefits
                    tvCode.text = "Rs. " + data.price
                }
            }
        }
    }

    override fun FragmentRedeemBinding.setupViews(savedInstanceState: Bundle?) {
        setTitle("Redeem your earnings")
        recyclerView.adapter = adapter
        btnUpdate.setOnClickListener { updateUserUPI() }
        userReference.child(userId).child("my_sales").addValueEventListener(this@MySalesFragment)
    }

    private fun FragmentRedeemBinding.updateUserUPI() {
        val upi: String = etUpi.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(upi)) {
            showToast("Please enter UPI")
            return
        }


        // Update the "upi" attribute for the specified user ID
        userReference.child(userId).child("upi").setValue(upi)
            .addOnSuccessListener { _ ->
                showToast("UPI ID updated, IF ENTERED WRONG, UPDATE IMMEDIATELY!")
                etUpi.setText("")
            }
            .addOnFailureListener { e: Exception ->
                showToast(
                    "Failed to update UPI: " + e.message,
                )
            }
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val list: ArrayList<BuyCoupon> = arrayListOf()
        for (dataSnapshot in snapshot.children) {
            Log.d("MySalesFragment.kt", "YASH => onDataChange:79 $dataSnapshot")
            val m: BuyCoupon? = dataSnapshot.getValue(BuyCoupon::class.java)
            if (m?.valid.equals(PaymentMode.REDEEMED.value)) {
                m?.let { list.add(it) }
            }
        }
        binding?.apply {
            layoutEmptyState.root.isVisible = list.isEmpty()
            recyclerView.isVisible = list.isNotEmpty()
        }
        adapter.submit(list)
    }

    override fun onCancelled(error: DatabaseError) {
        Log.d("MySalesFragment.kt", "YASH => onCancelled:84 $error")
    }
}