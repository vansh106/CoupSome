package `in`.coupsome.ui.coupon

import android.os.Bundle
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
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
                    btnBuy.setOnClickListener {
                        paymentGatewayActivityLauncher.launch(data)
                    }
                }
            }
        }
    }

    override fun FragmentBuyCouponsBinding.setupViews(savedInstanceState: Bundle?) {
        setTitle("Buy Coupons")
        usersReference.orderByChild("my_sales")
            .addValueEventListener(this@BuyCouponsFragment)
        recyclerView.adapter = adapter
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val items: MutableList<BuyCoupon> = ArrayList()

        for (userSnapshot in snapshot.children) {
            val userId = userSnapshot.key
            if (userId != user?.uid) {
                val salesSnapshot = userSnapshot.child("my_sales")
                val name = userSnapshot.child("fullName").getValue(String::class.java)
                val phone = userSnapshot.child("phone").getValue(String::class.java)
                for (saleSnapshot in salesSnapshot.children) {
                    val saleId = saleSnapshot.key
                    val benefits = saleSnapshot.child("benefits").getValue(String::class.java)
                    val price = saleSnapshot.child("price").getValue(String::class.java)
                    val brand = saleSnapshot.child("brand").getValue(String::class.java)
                    val code = saleSnapshot.child("code").getValue(String::class.java)
                    val valid = saleSnapshot.child("valid").getValue(String::class.java)
                    val cat = saleSnapshot.child("category").getValue(String::class.java)
                    if (valid != "0") {
                        continue
                    }
                    val buy = BuyCoupon(name, benefits, price, brand, phone, code, valid, cat, saleId).apply {
                        this.userId = userId
                    }
                    Log.d("modelBuy", buy.category + buy.name + buy.phoneNo)
                    items.add(buy)
                }
            }
        }
        adapter.submit(items)
    }

    override fun onCancelled(error: DatabaseError) {
        // Handle any errors that occur during the database query
        Log.d("BuyCouponsFragment.kt", "YASH => onCancelled:89 $error")
    }
}