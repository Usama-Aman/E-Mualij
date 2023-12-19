package com.elementary.mualijpro.dialogs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.elementary.mualijpro.R
import com.elementary.mualijpro.interfaces.ProfileMapUpdate
import com.elementary.mualijpro.models.posts.user.User
import com.elementary.mualijpro.ui.fragments.profile.ProfileFragment
import com.elementary.mualijpro.utils.AppUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class DialogMap(mapUpdate: ProfileMapUpdate, userData: User, profileFragment: ProfileFragment) : DialogFragment(),
    OnMapReadyCallback, View.OnClickListener {

    var tvSearchLocation: TextView? = null
    private var profileFragment: ProfileFragment? = null
    var rlSearchLocation: RelativeLayout? = null
    var ivCrossCards: ImageView? = null
    var mMap: GoogleMap? = null
    var mMapView: MapView? = null
    private var userData: User? = null
    var btnDone: Button? = null
    private var mapUpdate: ProfileMapUpdate? = null
    private var onCameraIdleListener: GoogleMap.OnCameraIdleListener? = null
    var mContext: FragmentActivity? = null
    var llMain: RelativeLayout? = null

    init {
        this.userData = userData
        this.mapUpdate = mapUpdate
        this.profileFragment = profileFragment
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)

        dialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
        val decorView = dialog!!.window!!.decorView
        val uiOptions =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        decorView.systemUiVisibility = uiOptions
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {

            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            DialogFactory.dialogSettings(mContext!!, dialog, llMain, 1f, 0.9f)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        }
    }


    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        super.onCreateView(inflater, parent, state)
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_map_layout, parent, false)

        // Map initializations
        mMapView = view?.findViewById(R.id.mapView)
        mMapView?.onCreate(state)
        mMapView?.onResume()
        mMapView?.getMapAsync(this)

        viewInitialize(view)
        setListeners()
        registerBroadCastReceiver()
        return view

    }

    private fun viewInitialize(v: View) {

        val apiKey = getString(R.string.google_maps_key)
        if (!Places.isInitialized()) {
            Places.initialize(activity!!.applicationContext, apiKey)
        }
        Places.createClient(activity!!)
        llMain = v.findViewById(R.id.llMain);
        tvSearchLocation = v.findViewById(R.id.tvSearchLocation)
        rlSearchLocation = v.findViewById(R.id.rlSearchLocation)
        ivCrossCards = v.findViewById(R.id.ivCrossCards)
        btnDone = v.findViewById(R.id.btnDone)

    }

    private fun setListeners() {
        ivCrossCards?.setOnClickListener(this)
        rlSearchLocation?.setOnClickListener(this)
        btnDone?.setOnClickListener(this)
    }


    private fun configureCameraIdle() {
        onCameraIdleListener = GoogleMap.OnCameraIdleListener {
            val latLng = mMap?.cameraPosition?.target
            try {
                val address = AppUtils.getAddress(mContext!!, latLng?.latitude!!, latLng?.longitude!!)
                tvSearchLocation?.text = address
                mapUpdate?.onMapDrag(latLng?.latitude!!, latLng?.longitude!!, address)
            } catch (e: Exception) {

            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (userData != null) {
            val latLong = LatLng(userData?.getLatitude()!!, userData?.getLongitude()!!)
            tvSearchLocation?.text = userData?.getLocation()
            mMap?.moveCamera(CameraUpdateFactory.newLatLng(latLong))
            val camPosition = CameraPosition.Builder().target(latLong).zoom(15f).build()
            mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition))
        }
        configureCameraIdle()
        mMap?.setOnCameraIdleListener(onCameraIdleListener)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivCrossCards -> this.dismiss()
            R.id.btnDone -> {
                mapUpdate?.onMapDoneClick()
                this.dismiss()
            }
            R.id.rlSearchLocation -> {
                val intent = Autocomplete.IntentBuilder(getMode(), getPlaceFields())
                    .build(activity!!)
                profileFragment?.startActivityForResult(intent, 1)
            }
        }
    }

    private fun getMode(): AutocompleteActivityMode {
        return AutocompleteActivityMode.OVERLAY
    }

    private fun getPlaceFields(): List<Place.Field> {
        return listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME)
    }

    private fun registerBroadCastReceiver() {
        val mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                if (intent.hasExtra("address"))
                    tvSearchLocation?.text = intent.getStringExtra("address")

                if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
                    val latLong =
                        LatLng(intent.getDoubleExtra("latitude", 0.0), intent.getDoubleExtra("longitude", 0.0))
                    mMap?.moveCamera(CameraUpdateFactory.newLatLng(latLong))
                    val camPosition = CameraPosition.Builder().target(latLong).zoom(15f).build()
                    mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition))
                }
            }
        }
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(mReceiver, IntentFilter("updateMap"))
    }


}