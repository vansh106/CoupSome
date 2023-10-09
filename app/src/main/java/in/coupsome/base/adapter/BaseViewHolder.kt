package `in`.coupsome.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import `in`.coupsome.base.model.AppData

typealias InflateViewHolderLayout<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

open class BaseViewHolder<M : AppData, VB : ViewBinding>(
    private val _binding: VB
) : RecyclerView.ViewHolder(_binding.root) {

    fun bind(data: M) {
        _binding.bind(data)
    }

    open fun VB.bind(data: M) {}

    fun getBinding() = _binding
}