package `in`.coupsome.base.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.textview.MaterialTextView
import `in`.coupsome.R

typealias InflateFragmentLayout<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(
    private val bindInflater: InflateFragmentLayout<VB>
) : Fragment() {
    var binding: VB? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BaseFragment.kt", this.javaClass.simpleName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = bindInflater(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.setupViews(savedInstanceState)
    }

    abstract fun VB.setupViews(savedInstanceState: Bundle?)

    fun showToast(message: Any?) {
        message?.let {
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun VB.setTitle(title: String) {
        root.findViewById<MaterialTextView>(R.id.tv_title)?.apply {
            visibility = View.VISIBLE
            text = title
        }
    }

    fun VB.setSubTitle(@DrawableRes icon: Int, subtitle: String) {
        root.findViewById<AppCompatImageView>(R.id.iv_subtitle)?.apply {
            visibility = View.VISIBLE
            setImageResource(icon)
        }
        root.findViewById<MaterialTextView>(R.id.tv_subtitle)?.apply {
            visibility = View.VISIBLE
            text = subtitle
        }
    }
}