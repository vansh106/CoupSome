package `in`.coupsome.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import `in`.coupsome.base.adapter.BaseAdapter
import `in`.coupsome.base.fragment.BaseFragment
import `in`.coupsome.databinding.FragmentTransactionsBinding
import `in`.coupsome.databinding.ItemTransactionBinding
import `in`.coupsome.di.UsersReference
import `in`.coupsome.model.Transaction
import javax.inject.Inject

@AndroidEntryPoint
class AllTransactionsFragment : BaseFragment<FragmentTransactionsBinding>(
    FragmentTransactionsBinding::inflate
), ValueEventListener {

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    @UsersReference
    lateinit var userReference: DatabaseReference

    private val userId by lazy { auth.currentUser?.uid ?: "" }


    private val adapter by lazy {
        BaseAdapter(
            ItemTransactionBinding::inflate,
            Transaction
        ).apply {
            setOnViewHolderInflateListener { binding, data, _ ->
                with(binding) {
                    tvId.text = data.txn_id
                    tvDate.text = data.date
                    tvType.text = data.txn_type
                    tvAmount.text = data.txn_amount
                }
            }
        }
    }

    override fun FragmentTransactionsBinding.setupViews(savedInstanceState: Bundle?) {
        setTitle("All Transactions")
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        userReference.child(userId).child("txns").addValueEventListener(this@AllTransactionsFragment)
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val list: ArrayList<Transaction> = arrayListOf()
        for (dataSnapshot in snapshot.children) {
            Log.d("MySalesFragment.kt", "YASH => onDataChange:79 $dataSnapshot")
            dataSnapshot.getValue(Transaction::class.java)?.let {
                list.add(it)
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