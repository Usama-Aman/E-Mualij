package com.elementary.mualijpro.ui.activities.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.SizeF
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.elementary.mualijpro.MualijProApplication
import com.elementary.mualijpro.MualijProApplication.Companion.isPhone
import com.elementary.mualijpro.R
import com.elementary.mualijpro.interfaces.AlertDialogCallBack
import com.elementary.mualijpro.models.posts.user.User
import com.elementary.mualijpro.ui.activities.auth.LoginActivity
import com.elementary.mualijpro.ui.activities.base.BaseActivity
import com.elementary.mualijpro.ui.fragments.changepass.ChangePassFragment
import com.elementary.mualijpro.ui.fragments.dashboard.DashboardFragment
import com.elementary.mualijpro.ui.fragments.menu.MenuFragment
import com.elementary.mualijpro.ui.fragments.profile.ProfileFragment
import com.elementary.mualijpro.ui.fragments.settings.SettingsFragment
import com.elementary.mualijpro.ui.fragments.statistics.StatisticsFragment
import com.elementary.mualijpro.utils.AppSP
import com.elementary.mualijpro.utils.AppUtils
import com.elementary.mualijpro.utils.Constants
import com.elementary.mualijpro.utils.Constants.changePassFragmentStack
import com.elementary.mualijpro.utils.Constants.dashboardFragmentStack
import com.elementary.mualijpro.utils.Constants.menuFragmentStack
import com.elementary.mualijpro.utils.Constants.profileFragmentStack
import com.elementary.mualijpro.utils.Constants.settingsFragmentStack
import com.elementary.mualijpro.utils.Constants.statisticsFragmentStack
import com.elementary.mualijpro.utils.UserDataPref
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header.*
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : BaseActivity(), View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener, AlertDialogCallBack {

    private var doubleBackToExitPressedOnce = false

    private var isComingFromNotification = false
    private var notificationType = ""
    private var appointmentId = ""
    private var userdata: User? = null

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navProfileImageView: CircleImageView
    private lateinit var navUserName: TextView
    private lateinit var navCross: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Is coming from any hacking technique
        if (intent != null && intent.getIntExtra(
                Constants.intentConstKey,
                0
            ) != Constants.intentConstValue
        )
            finish()

        isPhone = this.resources.getBoolean(R.bool.isPhone)

        setContentView(R.layout.activity_main)
        registerBroadCastReceiver()
        initialization()
        setListeners()
        getUserObject()
        setData()
    }

    private fun setListeners() {
        if (!isPhone) {
            rlHeaderProfile.setOnClickListener(this)
            rlHeaderSettings.setOnClickListener(this)
            rlHeaderLogo.setOnClickListener(this)
            rlHeaderDate.setOnClickListener(this)
        } else {
            initializeDrawer()
        }
    }

    fun hideShowToolbar(visibility: Int) {
        rlHeader.visibility = visibility
    }

    private fun initializeDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        navigationView.setNavigationItemSelectedListener(this)

        btnDrawer.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START, true)
        }


        val navHeaderView = navigationView.getHeaderView(0)
        navProfileImageView = navHeaderView.findViewById(R.id.navProfileImage)
        navUserName = navHeaderView.findViewById(R.id.navUserName)
        navCross = navHeaderView.findViewById(R.id.navCross)
        navCross.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START, true)
        }

        navHeaderView.setOnClickListener {
            onNavigationItemSelected(navigationView.menu.getItem(3))
        }
    }


    private fun initialization() {
        if (intent.hasExtra("isComingFromNotification"))
            isComingFromNotification = intent.getBooleanExtra("isComingFromNotification", false)
        if (intent.hasExtra("notificationType"))
            notificationType = intent.getStringExtra("notificationType")
        if (intent.hasExtra("appointmentId"))
            appointmentId = intent.getStringExtra("appointmentId")
        changeHeaderTitleIcon(
            R.drawable.ic_appointment,
            resources.getString(R.string.txt_header_appoinment_title)
        )
        var bundle = Bundle()
        if (isComingFromNotification) {
            if (notificationType.equals(Constants.chatType, true)) {
                bundle.putBoolean("isComingFromNotification", isComingFromNotification)
                bundle.putString("notificationType", notificationType)
                bundle.putString("appointmentId", appointmentId)
            }
        }
        var dashboardFragment = DashboardFragment()
        dashboardFragment.arguments = bundle
        setFragment(dashboardFragment, dashboardFragmentStack)
    }

    private fun setData() {
        if (!isPhone) {
            val c = Calendar.getInstance().time
            tvHeaderDateTitle.text =
                resources.getString(R.string.txt_setting_date) + "\n" + AppUtils.dateFormat(c)

            if (userdata != null) {
                AppUtils.loadImageWithGlide(
                    this,
                    userdata?.getFullImage()!!,
                    ivHeaderProfile,
                    R.drawable.profile_placeholder
                )
            }
        } else {
            AppUtils.loadImageWithGlide(
                this,
                userdata?.getFullImage()!!,
                navProfileImageView,
                R.drawable.profile_placeholder
            )

            navUserName.text = userdata?.getFullName()
        }
    }

    fun getUserObject() {
        userdata = null
        val userSP: UserDataPref = UserDataPref.getInstance(this)
        userdata = userSP.getUserData()
    }

    private fun setFragment(fragment: Fragment, stack: String) {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayoutMain, fragment, stack)
            .commit()
    }

    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.rlHeaderProfile -> {
                AppUtils.hideKeyboard(this)
                changeHeaderTitleIcon(
                    R.drawable.ic_profile_sel,
                    resources.getString(R.string.txt_header_profile_title)
                )
                setFragment(MenuFragment.newInstance(profileFragmentStack), menuFragmentStack)
            }

            R.id.rlHeaderDate -> {
                try {
                    val intent = Intent("dashBoard")
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                } catch (e: Exception) {

                }
            }

            R.id.rlHeaderSettings -> {
                AppUtils.hideKeyboard(this)
                changeHeaderTitleIcon(
                    R.drawable.ic_setting_sel,
                    resources.getString(R.string.txt_header_settings_title)
                )
                setFragment(MenuFragment.newInstance(settingsFragmentStack), menuFragmentStack)
            }

            R.id.rlHeaderLogo -> {
                AppUtils.hideKeyboard(this)
                changeHeaderTitleIcon(
                    R.drawable.ic_appointment,
                    resources.getString(R.string.txt_header_appoinment_title)
                )
                var fragment: Fragment? =
                    supportFragmentManager.findFragmentById(R.id.frameLayoutMain)
                if (fragment !is DashboardFragment)
                    setFragment(DashboardFragment(), dashboardFragmentStack)

            }
        }
    }

    private fun changeHeaderTitleIcon(imageView: Int?, title: String) {
        if (!isPhone) {
            ivHeaderTitleIcon.setImageResource(imageView!!)
            tvHeaderTitle.text = title
        } else {
            tvHeaderTitle.text = title
        }
    }

    private fun registerBroadCastReceiver() {
        val mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent != null && intent.hasExtra("type")) {
                    if (intent.getStringExtra("type") == "updateProfileImage") {
                        getUserObject()
                        if (!isPhone) {
                            AppUtils.loadImageWithGlide(
                                this@MainActivity,
                                userdata?.getFullImage()!!,
                                ivHeaderProfile,
                                R.drawable.profile_placeholder
                            )
                        } else {
                            navUserName.text = userdata?.getFullName()
                            AppUtils.loadImageWithGlide(
                                this@MainActivity,
                                userdata?.getFullImage()!!,
                                navProfileImageView,
                                R.drawable.profile_placeholder
                            )
                        }
                    } else if (intent.getStringExtra("type") == "updateHeaderTitleIcon"
                        && intent.hasExtra("image") && intent.hasExtra("title")
                    )
                        changeHeaderTitleIcon(
                            intent.getIntExtra("image", 0),
                            intent.getStringExtra("title")
                        )
                }
            }
        }
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mReceiver, IntentFilter("mainActivity"))
    }

    override fun onBackPressed() {
        if (!isPhone) {
            super.onBackPressed()
            finishAffinity()
        } else {
            when {
                drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                    drawerLayout.closeDrawer(
                        GravityCompat.START,
                        true
                    )
                }
                doubleBackToExitPressedOnce -> {
                    super.onBackPressed()
                }
                else -> {
                    this.doubleBackToExitPressedOnce = true
                    Toast.makeText(
                        this@MainActivity,
                        resources.getString(R.string.back_pressed_again),
                        Toast.LENGTH_SHORT
                    ).show()
                    Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
                }
            }

        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
            }
        }
        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START, true)
        val fragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.frameLayoutMain)

        Handler().postDelayed({
            when (item.itemId) {
                R.id.nav_my_appointments -> {

                    rlHeaderDate.visibility = View.VISIBLE

                    AppUtils.hideKeyboard(this)
                    changeHeaderTitleIcon(
                        R.drawable.ic_appointment,
                        resources.getString(R.string.txt_header_appoinment_title)
                    )

                    if (fragment !is DashboardFragment)
                        setFragment(DashboardFragment(), dashboardFragmentStack)
                }
                R.id.nav_profile -> {
                    rlHeaderDate.visibility = View.INVISIBLE

                    AppUtils.hideKeyboard(this)
                    changeHeaderTitleIcon(
                        R.drawable.ic_profile_sel,
                        resources.getString(R.string.txt_header_profile_title)
                    )
                    if (fragment !is ProfileFragment)
                        setFragment(ProfileFragment.newInstance(), profileFragmentStack)
                }
                R.id.nav_statistics -> {
                    rlHeaderDate.visibility = View.INVISIBLE

                    AppUtils.hideKeyboard(this)
                    changeHeaderTitleIcon(
                        R.drawable.ic_statistics_sel,
                        resources.getString(R.string.txt_header_statistics_title)
                    )
                    if (fragment !is StatisticsFragment)
                        setFragment(StatisticsFragment.newInstance(), statisticsFragmentStack)

                }
                R.id.nav_settings -> {
                    rlHeaderDate.visibility = View.INVISIBLE

                    AppUtils.hideKeyboard(this)
                    changeHeaderTitleIcon(
                        R.drawable.ic_setting_sel,
                        resources.getString(R.string.txt_header_settings_title)
                    )
                    if (fragment !is SettingsFragment)
                        setFragment(SettingsFragment.newInstance(), settingsFragmentStack)
                }
                R.id.nav_change_password -> {
                    rlHeaderDate.visibility = View.INVISIBLE

                    AppUtils.hideKeyboard(this)
                    changeHeaderTitleIcon(
                        R.drawable.ic_setting_sel,
                        resources.getString(R.string.txt_header_change_pass_title)
                    )
                    if (fragment !is ChangePassFragment)
                        setFragment(ChangePassFragment.newInstance(), changePassFragmentStack)
                }
                R.id.nav_log_out -> {
                    AppUtils.logoutConfirmationAlert(
                        this,
                        this
                    )
                }

            }
        }, 300)

        return true
    }

    override fun onDialogPositiveClick() {
        val userSP: UserDataPref = UserDataPref.getInstance(this)
        userSP.emptyUserData()
        val sp: AppSP = AppSP.getInstance(this)
        sp.savePreferences(Constants.accessToken, "")
        sp.savePreferences(Constants.isLoggedIn, false)
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(Constants.intentConstKey, Constants.intentConstValue)
        startActivity(intent)
        this.finish()
    }


}