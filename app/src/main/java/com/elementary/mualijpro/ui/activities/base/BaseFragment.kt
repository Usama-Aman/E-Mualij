package com.elementary.mualijpro.ui.activities.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.elementary.mualijpro.utils.AppUtils
import com.elementary.mualijpro.utils.Loader
import java.lang.Exception

open class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppUtils.hideKeyboard(activity!!)
        try {
            Loader.hideLoader()
        } catch (e: Exception) {

        }
    }
}
