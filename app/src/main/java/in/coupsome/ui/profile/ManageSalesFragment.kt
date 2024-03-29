package `in`.coupsome.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.R
import `in`.coupsome.base.adapter.BaseAdapter
import `in`.coupsome.base.fragment.BaseFragment
import `in`.coupsome.databinding.FragmentRecyclerViewBinding
import `in`.coupsome.databinding.ItemCouponBinding
import `in`.coupsome.di.UsersReference
import `in`.coupsome.model.BuyCoupon
import javax.inject.Inject

@AndroidEntryPoint
class ManageSalesFragment : BaseFragment<FragmentRecyclerViewBinding>(
    FragmentRecyclerViewBinding::inflate
), ValueEventListener {

    @Inject
    @UsersReference
    lateinit var usersReference: DatabaseReference

    @Inject
    lateinit var auth: FirebaseAuth

    private val userId by lazy { auth.currentUser?.uid }

    private val adapter by lazy {
        BaseAdapter(
            ItemCouponBinding::inflate, BuyCoupon.diffUtil
        ).apply {
            setOnViewHolderInflateListener { binding, data, _ ->
                with(binding) {
                    tvTitle.text = data.brand
                    tvDescription.text = data.benefits
                    tvStatus.isVisible = data.valid != "1"
                    tvPrice.text = getString(R.string.rupee_x, data.price)
                    btnBuy.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red_900)
                    btnBuy.text = "Delete"
                    btnBuy.setOnClickListener {
                        MaterialAlertDialogBuilder(requireContext()).setTitle("Confirm Delete")
                            .setMessage("Are you sure you want to delete this coupon? This action cannot be undone.")
                            .setPositiveButton("Delete") { dialog, _ ->
                                userId?.let {
                                    usersReference.child(it)
                                        .child("my_sales")
                                        .child(data.key!!).removeValue()
                                }
                                dialog.dismiss()
                            }.setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                            }.show()
                    }
                    tvStatus.text = when (data.valid) {
                        "0" -> "Verified"
                        "3" -> "Rejected"
                        else -> "Pending"
                    }
                    tvStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            when (data.valid) {
                                "0" -> R.color.green_900
                                "3" -> R.color.red_900
                                else -> R.color.orange_900
                            }
                        )
                    )
                }
            }
        }
    }

    override fun FragmentRecyclerViewBinding.setupViews(savedInstanceState: Bundle?) {
        setTitle("Manage your Coupons")
        userId?.let {
            usersReference.child(it).child("my_sales").addValueEventListener(this@ManageSalesFragment)
        }
        recyclerView.adapter = adapter
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val list = arrayListOf<BuyCoupon>()
        for (dataSnapshot in snapshot.children) {
            val m: BuyCoupon? = dataSnapshot.getValue(BuyCoupon::class.java)
            m?.key = dataSnapshot.key
            when {
                m?.valid.equals("2") -> continue
                m?.valid.equals("3") -> continue
            }
            if (m != null) {
                list.add(m)
            }
        }
        binding?.apply {
            layoutEmptyState.root.isVisible = list.isEmpty()
            recyclerView.isVisible = list.isNotEmpty()
        }
        adapter.submit(list)
    }

    override fun onCancelled(error: DatabaseError) {
        Log.e("ManageSalesFragment.kt", "YASH => onCancelled:56 $error")
    }
}