package `in`.coupsome.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import `in`.coupsome.base.model.AppData

open class BaseAdapter<M : AppData, VB : ViewBinding>(
    private val inflate: InflateViewHolderLayout<VB>,
    diff: DiffUtil.ItemCallback<M>
) : ListAdapter<M, BaseAdapter<M, VB>.ViewHolder>(diff) {

    private var onViewInflateListener: OnViewInflateListener<ViewBinding, AppData>? = null

    @SuppressWarnings("unchecked")
    fun setOnViewHolderInflateListener(
        listener: OnViewInflateListener<VB, M>? = null
    ) {
        this.onViewInflateListener = listener as? OnViewInflateListener<ViewBinding, AppData>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflate.invoke(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    open fun submit(data: List<M>) {
        submitList(data.toMutableList())
    }

    open fun submit(vararg data: M) {
        submitList(data.toMutableList())
    }

    inner class ViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: M) {
            onViewInflateListener?.onViewInflated(binding, data, adapterPosition)
        }
    }
}

fun interface OnViewInflateListener<T : ViewBinding, M : AppData> {
    fun onViewInflated(binding: T, data: M, position: Int)
}