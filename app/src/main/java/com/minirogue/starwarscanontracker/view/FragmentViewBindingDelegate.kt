package com.minirogue.starwarscanontracker.view

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<T : ViewBinding>(
        val fragment: Fragment,
        val viewBindingFactory: (View) -> T
) : ReadOnlyProperty<Fragment, T> {
    private var binding: T? = null

    /**
     * Observe the Fragment's View's LifecycleOwner to ensure that the binding gets removed at the correct time.
     * If this is not done, then there is a risk of the binding not referencing the active View, as
     * Fragments outlive their Views.
     */
    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observe(fragment, Observer { viewLifecycleOwner ->
                    viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            binding = null
                        }
                    })
                })
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        // If a binding exists, return it.
        binding?.let {
            return@getValue it
        }
        // Otherwise, check to make sure the current lifecycle state is appropriate
        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException(
                    "Binding is only available when the View is available (between onCreateView and onDestroyView)."
            )
        }
        // Create the binding and return it
        return viewBindingFactory(thisRef.requireView()).also { this@FragmentViewBindingDelegate.binding = it }
    }
}

/**
 * Pass this the bind method of the ViewBinding class (e.g. TaxCenterFragmentBinding::bind) to get the binding.
 * Note that the binding can only be valid between the [Fragment.onCreateView] and [Fragment.onDestroyView] methods.
 */
fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
        FragmentViewBindingDelegate(this, viewBindingFactory)
