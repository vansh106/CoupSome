package `in`.coupsome.base.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding


typealias InflateActivityLayout<T> = (LayoutInflater) -> T

abstract class BaseActivity<VB : ViewBinding>(
    private val bindInflater: InflateActivityLayout<VB>,
) : AppCompatActivity() {

    var binding: VB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupIntent()
        binding = bindInflater(layoutInflater)
        binding?.apply {
            setContentView(root)
            setupViews(savedInstanceState)
        }
    }

    abstract fun VB.setupViews(savedInstanceState: Bundle?)

    open fun setupIntent() {}

    fun showToast(message: Any?) {
        message?.let {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}