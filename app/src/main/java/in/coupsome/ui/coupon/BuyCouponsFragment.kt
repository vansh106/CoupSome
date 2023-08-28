package `in`.coupsome.ui.coupon

import android.os.Bundle
import android.util.Log
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
import `in`.coupsome.databinding.FragmentBuyCouponsBinding
import `in`.coupsome.databinding.ItemCouponBinding
import `in`.coupsome.di.UsersReference
import `in`.coupsome.model.BuyCoupon
import `in`.coupsome.ui.payment.PaymentGatewayActivity
import javax.inject.Inject

@AndroidEntryPoint
class BuyCouponsFragment : BaseFragment<FragmentBuyCouponsBinding>(FragmentBuyCouponsBinding::inflate),
    ValueEventListener {

    @Inject
    @UsersReference
    lateinit var usersReference: DatabaseReference

    @Inject
    lateinit var auth: FirebaseAuth

    private val user by lazy { auth.currentUser }

    private val paymentGatewayActivityLauncher =
        registerForActivityResult(PaymentGatewayActivity.PaymentGatewayActivityResultContract()) {
            Log.d("BuyCouponsFragment.kt", "YASH => onActivityResult: $it")
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Congratulations")
                .setMessage(
                    "Congrats for your savings. Payment Successfully Done!\n" +
                            "Go to profile page to view transaction details and coupons bought."
                )
                .setPositiveButton("Dismiss") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

    private val adapter by lazy {
        BaseAdapter(
            ItemCouponBinding::inflate,
            BuyCoupon.diffUtil
        ).apply {
            setOnViewHolderInflateListener { binding, data, _ ->
                binding.apply {
                    tvTitle.text = data.brand
                    tvDescription.text = data.benefits
                    tvPrice.text = getString(R.string.rupee_x, data.price)
                    btnBuy.setOnClickListener {
                        Log.d("BuyCouponsFragment.kt", "YASH => :62 ${data.userId}")
                        paymentGatewayActivityLauncher.launch(data)
                    }
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

    override fun FragmentBuyCouponsBinding.setupViews(savedInstanceState: Bundle?) {
        setTitle("Buy Coupons")
        usersReference
            .orderByChild("my_sales")
            .addValueEventListener(this@BuyCouponsFragment)
        recyclerView.adapter = adapter
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val list = ArrayList<BuyCoupon>()
        for (userSnapshot in snapshot.children) {
            val userId = userSnapshot.key
            if (userId != user?.uid) {
                val salesSnapshot = userSnapshot.child("my_sales")
                for (saleSnapshot in salesSnapshot.children) {
                    saleSnapshot.getValue(BuyCoupon::class.java)?.apply {
                        this.key = saleSnapshot.key
                        this.userId = userId
                    }?.let {
                        if (it.valid != "0") {
                            return@let
                        }
                        list.add(it)
                    }
                }
            }
        }
        Log.d("BuyCouponsFragment.kt", "YASH => onDataChange:98 $list")
        binding?.apply {
            layoutEmptyState.root.isVisible = list.isEmpty()
            recyclerView.isVisible = list.isNotEmpty()
        }
        adapter.submit(list)
    }

    override fun onCancelled(error: DatabaseError) {
        // Handle any errors that occur during the database query
        Log.d("BuyCouponsFragment.kt", "YASH => onCancelled:89 $error")
    }
}