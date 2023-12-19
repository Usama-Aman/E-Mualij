package com.elementary.mualijpro.ui.fragments.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.elementary.mualijpro.R
import com.elementary.mualijpro.interfaces.AlertDialogCallBack
import com.elementary.mualijpro.ui.activities.auth.LoginActivity
import com.elementary.mualijpro.ui.activities.base.BaseFragment
import com.elementary.mualijpro.ui.fragments.changepass.ChangePassFragment
import com.elementary.mualijpro.ui.fragments.profile.ProfileFragment
import com.elementary.mualijpro.ui.fragments.settings.SettingsFragment
import com.elementary.mualijpro.ui.fragments.statistics.StatisticsFragment
import com.elementary.mualijpro.utils.AppSP
import com.elementary.mualijpro.utils.AppUtils
import com.elementary.mualijpro.utils.Constants
import com.elementary.mualijpro.utils.Constants.changePassFragmentStack
import com.elementary.mualijpro.utils.Constants.profileFragmentStack
import com.elementary.mualijpro.utils.Constants.settingsFragmentStack
import com.elementary.mualijpro.utils.Constants.statisticsFragmentStack
import com.elementary.mualijpro.utils.UserDataPref
import kotlinx.android.synthetic.main.fragment_menu.*

@Suppress("DEPRECATION")
class MenuFragment : BaseFragment(), View.OnClickListener, AlertDialogCallBack {

    private var fragmentStack: String? = null

    companion object {
        fun newInstance(fragmentStack: String): MenuFragment {
            val args = Bundle()
            args.putString("fragmentStack", fragmentStack)
            val fragment = MenuFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun readBundle(bundle: Bundle?) {
        if (bundle != null)
            this.fragmentStack = bundle.getString("fragmentStack")
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        readBundle(arguments)
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
        setListeners()
    }

    private fun setListeners() {
        rlProfile.setOnClickListener(this)
        rlSettings.setOnClickListener(this)
        rlStatistics.setOnClickListener(this)
        rlPassword.setOnClickListener(this)
        rlLogout.setOnClickListener(this)
    }

    private fun initialization() {
        if (fragmentStack == profileFragmentStack) {
            setMenuItemBg(rlProfile, ivProfile, tvProfile, R.drawable.ic_profile_sel)
            var fragment: Fragment? = childFragmentManager.findFragmentById(R.id.frameLayout)
            if (fragment !is ProfileFragment)
                setFragment(ProfileFragment.newInstance(), profileFragmentStack)
        } else {
            setMenuItemBg(rlSettings, ivSettings, tvSettings, R.drawable.ic_setting_sel)
            var fragment: Fragment? = childFragmentManager.findFragmentById(R.id.frameLayout)
            if (fragment !is SettingsFragment)
                setFragment(SettingsFragment.newInstance(), settingsFragmentStack)
        }
    }

    private fun setFragment(fragment: Fragment, stack: String) {
        childFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment, stack).commit()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.rlProfile -> {
                AppUtils.hideKeyboard(activity!!)
                updateHeaderValue(
                    R.drawable.ic_profile_sel,
                    resources.getString(R.string.txt_header_profile_title)
                )
                setMenuItemBg(rlProfile, ivProfile, tvProfile, R.drawable.ic_profile_sel)
                var fragment: Fragment? = childFragmentManager.findFragmentById(R.id.frameLayout)
                if (fragment !is ProfileFragment)
                    setFragment(ProfileFragment.newInstance(), profileFragmentStack)
            }

            R.id.rlSettings -> {
                AppUtils.hideKeyboard(activity!!)
                updateHeaderValue(
                    R.drawable.ic_setting_sel,
                    resources.getString(R.string.txt_header_settings_title)
                )
                setMenuItemBg(rlSettings, ivSettings, tvSettings, R.drawable.ic_setting_sel)
                var fragment: Fragment? = childFragmentManager.findFragmentById(R.id.frameLayout)
                if (fragment !is SettingsFragment)
                    setFragment(SettingsFragment.newInstance(), settingsFragmentStack)
            }

            R.id.rlStatistics -> {
                AppUtils.hideKeyboard(activity!!)
                updateHeaderValue(
                    R.drawable.ic_statistics_sel,
                    resources.getString(R.string.txt_header_statistics_title)
                )
                setMenuItemBg(
                    rlStatistics,
                    ivStatistics,
                    tvStatistics,
                    R.drawable.ic_statistics_sel
                )
                var fragment: Fragment? = childFragmentManager.findFragmentById(R.id.frameLayout)
                if (fragment !is StatisticsFragment)
                    setFragment(StatisticsFragment.newInstance(), statisticsFragmentStack)
            }

            R.id.rlPassword -> {
                AppUtils.hideKeyboard(activity!!)
                updateHeaderValue(
                    R.drawable.ic_password_sel,
                    resources.getString(R.string.txt_header_change_pass_title)
                )
                setMenuItemBg(rlPassword, ivPassword, tvPassword, R.drawable.ic_password_sel)
                var fragment: Fragment? = childFragmentManager.findFragmentById(R.id.frameLayout)
                if (fragment !is ChangePassFragment)
                    setFragment(ChangePassFragment.newInstance(), changePassFragmentStack)
            }

            R.id.rlLogout -> {
                AppUtils.logoutConfirmationAlert(
                    activity!!,
                    this
                )
            }
        }
    }

    private fun updateHeaderValue(image: Int, title: String) {
        try {
            val intent = Intent("mainActivity")
            intent.putExtra("type", "updateHeaderTitleIcon")
            intent.putExtra("image", image)
            intent.putExtra("title", title)
            LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
        } catch (e: Exception) {

        }
    }

    private fun setMenuItemBg(parent: RelativeLayout, iv: ImageView, tv: TextView, image: Int) {

        // Clear app backgrounds
        rlProfile.setBackgroundColor(resources.getColor(R.color.white))
        rlSettings.setBackgroundColor(resources.getColor(R.color.white))
        rlStatistics.setBackgroundColor(resources.getColor(R.color.white))
        rlPassword.setBackgroundColor(resources.getColor(R.color.white))
        ivProfile.setImageResource(R.drawable.ic_profile_un_sel)
        ivSettings.setImageResource(R.drawable.ic_setting_un_sel)
        ivStatistics.setImageResource(R.drawable.ic_statistics_un_sel)
        ivPassword.setImageResource(R.drawable.ic_password_un_sel)
        tvProfile.setTextColor(resources.getColor(R.color.gray_dark))
        tvSettings.setTextColor(resources.getColor(R.color.gray_dark))
        tvStatistics.setTextColor(resources.getColor(R.color.gray_dark))
        tvPassword.setTextColor(resources.getColor(R.color.gray_dark))

        // Set selected item background
        parent.setBackgroundColor(resources.getColor(R.color.app_default_dark_color))
        iv.setImageResource(image)
        tv.setTextColor(resources.getColor(R.color.white))
    }

    override fun onDialogPositiveClick() {
        val userSP: UserDataPref = UserDataPref.getInstance(activity!!)
        userSP.emptyUserData()
        val sp: AppSP = AppSP.getInstance(activity!!)
        sp.savePreferences(Constants.accessToken, "")
        sp.savePreferences(Constants.isLoggedIn, false)
        val intent = Intent(activity!!, LoginActivity::class.java)
        intent.putExtra(Constants.intentConstKey, Constants.intentConstValue)
        startActivity(intent)
        activity!!.finish()
    }

}