package usecase

import androidx.fragment.app.Fragment
import view.SettingsFragment
import javax.inject.Inject

internal class GetSettingsFragmentImpl @Inject constructor() : GetSettingsFragment {
    override fun invoke(): Fragment {
        return SettingsFragment()
    }
}
