package com.elementary.mualijpro.ui.fragments.profile

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elementary.mualijpro.R
import com.elementary.mualijpro.models.posts.user.User
import com.elementary.mualijpro.ui.activities.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.etEmail
import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.elementary.mualijpro.dialogs.DialogCities
import com.elementary.mualijpro.dialogs.DialogMap
import com.elementary.mualijpro.interfaces.ApiArrayCallBack
import com.elementary.mualijpro.interfaces.ApiResponseEvent
import com.elementary.mualijpro.interfaces.CityCallBack
import com.elementary.mualijpro.interfaces.ProfileMapUpdate
import com.elementary.mualijpro.models.local.WebResponse
import com.elementary.mualijpro.models.posts.cities.Cities
import com.elementary.mualijpro.models.posts.cities.CitiesResponse
import com.elementary.mualijpro.networks.APIsTag
import com.elementary.mualijpro.networks.ApiController
import com.elementary.mualijpro.utils.*
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@Suppress("DEPRECATION")
class ProfileFragment : BaseFragment(), View.OnClickListener, ApiResponseEvent, ApiArrayCallBack,
    ProfileMapUpdate,
    CityCallBack {


    var genderValue = "male"
    var onlineAvailabilityValue = "0"
    private var userdata: User? = null
    private val takePhotoCode = 100
    private val galleryPhotoCode = 200
    private var profileImagePath: String? = ""
    private var imageUri: Uri? = null
    private val autocompleteRequestCode = 1
    private var mapLat: Double = 0.0
    private var mapLong: Double = 0.0
    private var mapAddress: String = ""
    private var cityId = 0
    var citiesArray = ArrayList<Cities>()
    var citiesDialog: DialogCities? = null

    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializations()
        setListeners()
        getProfileDataFromApi()
//        setData()
        setChangeListeners()
    }

    private fun initializations() {
        etEmail.isEnabled = false
        etEmail.keyListener = null
    }

    private fun setListeners() {
        rlProfileParent.setOnClickListener(this)
        btnGenderMale.setOnClickListener(this)
        btnGenderFemale.setOnClickListener(this)
        btnAvailabilityYes.setOnClickListener(this)
        btnAvailabilityNo.setOnClickListener(this)
        btnUpdate.setOnClickListener(this)
        rlClinicAddress.setOnClickListener(this)
        rlCity.setOnClickListener(this)


        llMain.setOnClickListener { AppUtils.hideKeyboard(activity!!) }
        svMain.setOnClickListener { AppUtils.hideKeyboard(activity!!) }
        llSub.setOnClickListener { AppUtils.hideKeyboard(activity!!) }
        llSub1.setOnClickListener { AppUtils.hideKeyboard(activity!!) }
    }


    private fun setData() {
        val userSP: UserDataPref = UserDataPref.getInstance(this.liveContext()!!)
        userdata = userSP.getUserData()

        if (userdata != null) {
            AppUtils.loadImageWithGlide(
                this.liveContext()!!,
                userdata?.getFullImage()!!,
                ivProfile,
                R.drawable.profile_placeholder
            )

            cityId = userdata?.getCityId()!!
            etFirstName.setText(userdata?.getFirstName()!!)
            etLastName.setText(userdata?.getLastName()!!)
            etPhoneNumber.setText(userdata?.getMobileNumber()!!)
            etEmail.setText(userdata?.getEmail()!!)
            tvClinicAddressValue.text = userdata?.getLocation()!!
            etLangSpoken.setText(userdata?.getPreferredLanguage()!!)
            etPersonalState.setText(userdata?.getStatement()!!)
            etEducation.setText(userdata?.getEducation()!!)
            etBoardCertificate.setText(userdata?.getCertificates()!!)



            if (userdata?.getGender()!!.getKey().equals("male")) {
                genderValue = "male"
                changeBtnBg(btnGenderMale, btnGenderFemale)
            } else {
                genderValue = "female"
                changeBtnBg(btnGenderFemale, btnGenderMale)
            }
            if (userdata?.getIsOnlineAccept() == 1) {
                onlineAvailabilityValue = "1"
                changeBtnBg(btnAvailabilityYes, btnAvailabilityNo)
            } else {
                onlineAvailabilityValue = "0"
                changeBtnBg(btnAvailabilityNo, btnAvailabilityYes)
            }
            etOnlineFee.setText(userdata?.getDoctorFee()!!)

        }
    }

    private fun changeBtnBg(selectedBtn: Button, unSelectedBtn: Button) {
        selectedBtn.setBackgroundColor(resources.getColor(R.color.app_default_color))
        selectedBtn.setTextColor(resources.getColor(R.color.white))
        unSelectedBtn.setBackgroundColor(resources.getColor(R.color.white))
        unSelectedBtn.setTextColor(resources.getColor(R.color.gray_dark))
    }

    private fun setChangeListeners() {
        etFirstName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                AppUtils.validateEtWithLength(etFirstName)
            }
        })

        etLastName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                AppUtils.validateEtWithLength(etLastName)
            }
        })

        etPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                AppUtils.validatePhone(etPhoneNumber)
            }
        })

        etOnlineFee.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (onlineAvailabilityValue == "1") {
                    if (!AppUtils.validateInput(etOnlineFee))
                        return
                } else
                    AppUtils.setBackground(etOnlineFee, R.drawable.bg_white_with_gray_border)
            }
        })
    }

    private fun validateGetCityReq() {

        if (!AppUtils.isOnline(activity!!)) {
            AppUtils.touchScreenEnable(activity!!)
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_alert_internet_connection)
            )
            return
        }
        ApiController.getInstanceArray(activity!!, this).getCitiesList()

    }

    private fun getProfileDataFromApi() {
        Loader.showLoader(activity!!)
        if (!AppUtils.isOnline(activity!!)) {
            AppUtils.touchScreenEnable(activity!!)
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_alert_internet_connection)
            )
            return
        }
        ApiController.getInstance(activity!!, this).getProfileData()

    }

    private fun validateRequest() {

        if (!AppUtils.validateEtWithLength(etFirstName))
            return

        if (!AppUtils.validateEtWithLength(etLastName))
            return

        if (!AppUtils.validatePhone(etPhoneNumber))
            return

        if (!AppUtils.validateAddress(tvClinicAddressValue, rlClinicAddress))
            return

        if (!AppUtils.validateCity(cityId, rlCity))
            return

        if (onlineAvailabilityValue == "1") {
            if (!AppUtils.validateInput(etOnlineFee))
                return
        } else
            AppUtils.setBackground(etOnlineFee, R.drawable.bg_white_with_gray_border)

        if (!AppUtils.isOnline(activity!!)) {
            AppUtils.touchScreenEnable(activity!!)
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_alert_internet_connection)
            )
            return
        }

        try {

            var parseValue = "text/plain"
            val firstName: RequestBody? =
                RequestBody.create(MediaType.parse(parseValue), etFirstName.text.toString())
            val cityId: RequestBody? =
                RequestBody.create(MediaType.parse(parseValue), cityId.toString())
            val lastName: RequestBody? =
                RequestBody.create(MediaType.parse(parseValue), etLastName.text.toString())
            val email = RequestBody.create(MediaType.parse(parseValue), etEmail.text.toString())
            val mobileNumber: RequestBody? =
                RequestBody.create(MediaType.parse(parseValue), etPhoneNumber.text.toString())
            val statement: RequestBody? =
                RequestBody.create(MediaType.parse(parseValue), etPersonalState.text.toString())
            val education: RequestBody? =
                RequestBody.create(MediaType.parse(parseValue), etEducation.text.toString())
            val certificates: RequestBody? =
                RequestBody.create(MediaType.parse(parseValue), etBoardCertificate.text.toString())
            val gender: RequestBody? = RequestBody.create(MediaType.parse(parseValue), genderValue)
            val preferredLanguage: RequestBody? =
                RequestBody.create(MediaType.parse(parseValue), etLangSpoken.text.toString())
            val doctorFee: RequestBody? =
                RequestBody.create(MediaType.parse(parseValue), etOnlineFee.text.toString())
            val isOnlineAccept: RequestBody? =
                RequestBody.create(MediaType.parse(parseValue), onlineAvailabilityValue)
            val location: RequestBody? =
                RequestBody.create(
                    MediaType.parse(parseValue),
                    tvClinicAddressValue.text.toString()
                )
            val latitude: RequestBody? =
                RequestBody.create(MediaType.parse(parseValue), userdata?.getLatitude().toString())
            val longitude: RequestBody? =
                RequestBody.create(MediaType.parse(parseValue), userdata?.getLongitude().toString())
            var photo: MultipartBody.Part? = null
            val imageFile: RequestBody?

            if (!(profileImagePath.equals(""))) {
                imageFile =
                    RequestBody.create(MediaType.parse("image/jpeg"), File(profileImagePath!!))
                photo = MultipartBody.Part.createFormData(
                    "photo",
                    File(profileImagePath!!).name,
                    imageFile!!
                )
            }


            Loader.showLoader(activity!!)
            ApiController.getInstance(activity!!, this).updateProfile(
                firstName,
                cityId,
                lastName,
                email,
                mobileNumber,
                statement,
                education,
                certificates,
                gender,
                preferredLanguage,
                doctorFee,
                isOnlineAccept,
                location,
                latitude,
                longitude,
                photo
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun checkPermissionForUploadImage() {
        takeRunTimePermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            autocompleteRequestCode -> if (resultCode == AutocompleteActivity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                mapLat = place.latLng!!.latitude
                mapLong = place.latLng!!.longitude
                mapAddress = place.address!!
                try {
                    val intent = Intent("updateMap")
                    intent.putExtra("address", mapAddress)
                    intent.putExtra("latitude", mapLat)
                    intent.putExtra("longitude", mapLong)
                    LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            galleryPhotoCode -> try {

                val selectedImage = data!!.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = activity?.contentResolver?.query(
                    selectedImage!!,
                    filePathColumn,
                    null,
                    null,
                    null
                )
                cursor?.moveToFirst()
                val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
                profileImagePath = cursor?.getString(columnIndex!!)

                var bitmap =
                    MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImage)
                ivProfile.setBackgroundResource(R.drawable.profile_placeholder)
                val matrix = Matrix()
                matrix.postRotate(0f)
                val bmp =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                AppUtils.showImageWithGlideBitmap(
                    activity!!,
                    bmp,
                    ivProfile
                )

                cursor?.close()

            } catch (e: Exception) {
                profileImagePath = ""
            }

            takePhotoCode -> try {
                val cr = activity?.contentResolver
                val metaCursor = cr?.query(
                    imageUri!!,
                    arrayOf(MediaStore.MediaColumns.DATA), null, null, null
                )

                if (metaCursor?.moveToFirst()!!)
                    profileImagePath = metaCursor.getString(0)

                val viewUri = imageUri
                var bitmap =
                    MediaStore.Images.Media.getBitmap(activity?.getContentResolver(), viewUri)

                ivProfile.setBackgroundResource(R.drawable.profile_placeholder)
                val matrix = Matrix()
                matrix.postRotate(0f)
                val bmp =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                AppUtils.showImageWithGlideBitmap(
                    activity!!,
                    bmp,
                    ivProfile
                )

            } catch (e: Exception) {
                profileImagePath = ""
            }
        }

    }

    private fun openGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, galleryPhotoCode)
    }

    private fun openCamera() {

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        imageUri = null
        imageUri = activity?.contentResolver
            ?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, takePhotoCode)

    }

    private fun takeRunTimePermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.size > 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)
                    showPictureDialog()
                else
                    AppUtils.showPermissionAlertDialog(activity!!)
                return

            }
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(activity!!)
        pictureDialog.setTitle(resources.getString(R.string.txt_title_image_picker))
        val pictureDialogItems = arrayOf(
            resources.getString(R.string.txt_choose_gallery),
            resources.getString(R.string.txt_open_camera),
            resources.getString(R.string.txt_cancel)
        )
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> {
                    dialog.dismiss()
                    openGallery()
                }
                1 -> {
                    dialog.dismiss()
                    openCamera()
                }
                2 -> dialog.dismiss()
            }
        }
        pictureDialog.show()
    }

    private fun liveContext(): Context? {
        return context ?: return null
    }

    // Api Array Response
    override fun onSuccess(requestType: Int, webResponse: CitiesResponse) {
        Loader.hideLoader()
        citiesArray = webResponse.getCities()!!
        for (i in 0 until citiesArray?.size!!) {
            if (cityId == citiesArray[i].getId()) {
                citiesArray[i].isSelected = true
                try {
                    if (AppUtils.getDefaultLang().equals("en", true))
                        tvCityValue.text = citiesArray[i].getNameEn()
                    else
                        tvCityValue.text = citiesArray[i].getNameAr()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                break
            }
        }
    }

    // API Error Response Handling
    override fun onError(requestType: Int, errorMessage: String) {
        try {
            Loader.hideLoader()
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                errorMessage
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // API Success Response Handling
    override fun onSuccess(requestType: Int, webResponse: Any) {

        when (requestType) {
            APIsTag.updateProfile -> {
                try {
                    Loader.hideLoader()
                    var objectData = webResponse as WebResponse<*>

                    var userdata: User? =
                        CommonFunctions.convertDataType(objectData, User::class.java)
                    var userToken =
                        AppSP.getInstance(activity!!).readString(Constants.accessToken, "")
                    val userSP: UserDataPref = UserDataPref.getInstance(activity!!)
                    userdata?.setToken(userToken!!)
                    userSP.saveUserData(userdata)
                    try {
                        val intent = Intent("mainActivity")
                        intent.putExtra("type", "updateProfileImage")
                        LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    CustomAlert.showDropDownNotificationSuccess(
                        activity!!,
                        resources.getString(R.string.txt_alert_success_information),
                        objectData.message!!
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            APIsTag.getProfileData -> {
                var objectData = webResponse as WebResponse<*>

                var userdata: User? = CommonFunctions.convertDataType(objectData, User::class.java)
                var userToken = AppSP.getInstance(activity!!).readString(Constants.accessToken, "")
                val userSP: UserDataPref = UserDataPref.getInstance(activity!!)
                userdata?.setToken(userToken!!)
                userSP.saveUserData(userdata)

                setData()
                validateGetCityReq()

            }
        }
    }

    override fun onMapDoneClick() {

        if (mapLat != 0.0)
            userdata?.setLatitude(mapLat)

        if (mapLong != 0.0)
            userdata?.setLongitude(mapLong)

        if (mapAddress != "") {
            userdata?.setLocation(mapAddress)
            tvClinicAddressValue.text = mapAddress
        }

    }

    override fun onMapDrag(mapLat: Double, mapLong: Double, mapAddress: String) {

        this.mapLat = mapLat
        this.mapLong = mapLong
        this.mapAddress = mapAddress

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.rlProfileParent -> {
                checkPermissionForUploadImage()
            }

            R.id.rlClinicAddress -> {
                var startEndTimeDialog = DialogMap(this, userdata!!, this)
                fragmentManager?.let { startEndTimeDialog.show(it, "") }
            }

            R.id.rlCity -> {
                citiesDialog = DialogCities(citiesArray, this)
                fragmentManager?.let { citiesDialog?.show(it, "") }
            }

            R.id.btnUpdate -> {
                validateRequest()
            }

            R.id.btnAvailabilityYes -> {
                onlineAvailabilityValue = "1"
                AppUtils.validateInput(etOnlineFee)
                changeBtnBg(btnAvailabilityYes, btnAvailabilityNo)
            }

            R.id.btnAvailabilityNo -> {
                onlineAvailabilityValue = "0"
                AppUtils.setBackground(etOnlineFee, R.drawable.bg_white_with_gray_border)
                changeBtnBg(btnAvailabilityNo, btnAvailabilityYes)
            }

            R.id.btnGenderMale -> {
                genderValue = "male"
                changeBtnBg(btnGenderMale, btnGenderFemale)
            }

            R.id.btnGenderFemale -> {
                genderValue = "female"
                changeBtnBg(btnGenderFemale, btnGenderMale)
            }
        }
    }

    override fun onCallBack(cityObject: Cities, position: Int) {
        AppUtils.setBackground(rlCity, R.drawable.bg_white_with_gray_border)
        if (AppUtils.getDefaultLang().equals("en", true))
            tvCityValue.text = cityObject.getNameEn()
        else
            tvCityValue.text = cityObject.getNameAr()
        cityId = cityObject.getId()!!
        for (i in 0 until citiesArray.size)
            citiesArray[i].isSelected = false
        citiesArray[position].isSelected = true
        if (AppUtils.isKeyboardOpen(activity!!))
            AppUtils.hideKeyboardForOverlay(activity!!)
        citiesDialog?.dismiss()
    }

}