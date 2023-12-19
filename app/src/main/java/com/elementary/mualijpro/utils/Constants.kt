package com.elementary.mualijpro.utils

import com.elementary.mualijpro.ui.fragments.dashboard.DashboardFragment

object Constants {

    // Http
    const val MAIN_HTTP_BASE_URL = "F5B6892E435F2F039C762ACB2510FBC128AC2FE0A8C28F10F30054452595E74384EBC3C09E8A8E3158D184423892A681" // Public URL
//    const val MAIN_HTTP_BASE_URL = "8BA615B9350FA46CEB8D9DBDB4994DEB064D37F73A34EE8F47319F16C825D7179E162AC97C6C7F6098495A20117AE96A" // Local URL

    // Sockets
    const val MAIN_SOCKET_BASE_URL = "4B7710E479B680149A7E1A627F2ABC4869204EA286B11D173BBA184FB0A146E441C195E1F5E27BE701A06987A20DFBE1" // Public URL
//    const val MAIN_SOCKET_BASE_URL = "8BA615B9350FA46CEB8D9DBDB4994DEBB4B25C91433DF56F09CD5942277CFD46" // Local URL

    const val AckTimeOut: Long = 5000
    const val intentConstValue = 1000
    const val intentConstKey = "intentConstKey"

    const val chatType = "chat"
    const val appointmentType = "appointment"

    var appToken = ""
    var all = "ALL"
    var isDeviceRegisterCalled = false
    var isExpired: Int = 0
    const val FireBaseToken = "FireBaseToken"
    const val isLoggedIn = "IsLoggedIn"
    const val userCurrentLang = "userCurrentLang"
    const val accessToken = ""
    var paramDeviceId = "device_id"
    var paramOsVersion = "os_version"
    var paramDeviceName = "device_name"
    var baseDateTimeFormat = "yyyy-MM-dd HH:mm"
    var paramDeviceType = "type"
    var deviceType = "android"
    var settingsFragmentStack = "settingsFragment"
    var statisticsFragmentStack = "statisticsFragment"
    var profileFragmentStack = "profileFragment"
    var dashboardFragmentStack = "dashboardFragment"
    var menuFragmentStack = "menuFragment"
    var changePassFragmentStack = "changePassFragment"
    var baseDateFormat = "yyyy-MM-dd"
    var dashboardDays = "days"
    var dashboardUpdatePrescription = "updatePrescription"
    var dashboardCancelAppointment = "cancelAppointment"
    var dashboardRescheduleAppointment = "rescheduleAppointment"
    var dashboardReportPatient = "dashboardReportPatient"
    var directoryName = "MualijPro/"
    var dashboardCompleteAppointment = "completeAppointment"


    var fragmentDashboardRef: DashboardFragment? = null

    var chatVideoTime = 30

    var socketParamToId = "to_id"
    var socketParamFromId = "from_id"
    var socketParamToType = "to_type"
    var socketParamFromType = "from_type"
    var socketParamAppnmtId = "appointment_id"
    var socketParamMessageId = "message_id"
    var socketParamMessage = "message"
    var socketParamType = "type"
    var socketParamAudioTime = "media_time"
    var socketParamVideoThumb = "video_thumb"
    var patientJoin = "patient_join"
    var doctorJoin = "doctor_join"
    var toType = "patient"
    var fromType = "doctor"
    var messageType = "message"
    var imageType = "image"
    var audioType = "audio"
    var videoType = "video"
    var doctorOfflineMsg = "Doctor is offline now"
    var doctorOfflineType = "doctor_offline"
    var patientOfflineType = "patient_offline"

}
