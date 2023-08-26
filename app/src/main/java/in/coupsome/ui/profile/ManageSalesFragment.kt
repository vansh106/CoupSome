package `in`.coupsome.ui.profile

import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
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

    private var list: ArrayList<BuyCoupon> = arrayListOf()

    private val adapter by lazy {
        BaseAdapter(
            ItemCouponBinding::inflate,
            BuyCoupon.diffUtil
        )
    }

    override fun FragmentRecyclerViewBinding.setupViews(savedInstanceState: Bundle?) {
        setTitle("Manage your Coupons")
        userId?.let {
            usersReference.child(it).child("my_sales").addValueEventListener(this@ManageSalesFragment)
        }
        recyclerView.adapter = adapter
    }

    override fun onDataChange(snapshot: DataSnapshot) {

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
        adapter.submit(list)
    }

    override fun onCancelled(error: DatabaseError) {
        Log.e("ManageSalesFragment.kt", "YASH => onCancelled:56 $error")
    }
}