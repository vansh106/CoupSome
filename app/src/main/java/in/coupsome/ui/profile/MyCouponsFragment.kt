package `in`.coupsome.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
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
import `in`.coupsome.databinding.ItemSaleBinding
import `in`.coupsome.di.UsersReference
import `in`.coupsome.model.BuyCoupon
import javax.inject.Inject

@AndroidEntryPoint
class MyCouponsFragment: BaseFragment<FragmentRecyclerViewBinding>(
    FragmentRecyclerViewBinding::inflate
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
            BuyCoupon
        ).apply {
            setOnViewHolderInflateListener { binding, data, _ ->
                with(binding) {
                    tvTitle.text = data.brand
                    tvDescription.text = data.benefits
                    tvAmount.text = data.price
                    imgCoupon.setImageResource(
                        when (data.category) {
                            "Fashion" -> R.drawable.fashion_buy_icon
                            "Food" -> R.drawable.food_buy_icon
                            else -> R.drawable.img_coupon
                        }
                    )
                }
            }
        }
    }

    override fun FragmentRecyclerViewBinding.setupViews(savedInstanceState: Bundle?) {
        setTitle("My Coupons")
        recyclerView.adapter = adapter
        userReference.child(userId).child("CouponsBought").addValueEventListener(this@MyCouponsFragment)
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val list = arrayListOf<BuyCoupon>()
        for (data in snapshot.children) {
            val buyCoupon = data.getValue(BuyCoupon::class.java)
            buyCoupon?.let { list.add(it) }
        }
        binding?.apply {
            layoutEmptyState.root.isVisible = list.isEmpty()
            recyclerView.isVisible = list.isNotEmpty()
        }
        adapter.submit(list)
    }

    override fun onCancelled(error: DatabaseError) {
        Log.d("MyCouponsFragment.kt", "YASH => onCancelled:62 $error")
    }
}