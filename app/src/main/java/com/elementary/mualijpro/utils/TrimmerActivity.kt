package com.elementary.mualijpro.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import com.daasuu.mp4compose.FillMode
import com.daasuu.mp4compose.composer.Mp4Composer
import com.elementary.mualijpro.R
import com.elementary.mualijpro.dialogs.DialogChat
import com.elementary.mualijpro.ui.activities.base.BaseActivity
import com.kaopiz.kprogresshud.KProgressHUD
import com.video_trim.K4LVideoTrimmer
import com.video_trim.interfaces.OnK4LVideoListener
import com.video_trim.interfaces.OnTrimVideoListener
import kotlinx.android.synthetic.main.activity_videotrim.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TrimmerActivity : BaseActivity(), OnTrimVideoListener, OnK4LVideoListener {
    private var mVideoTrimmer: K4LVideoTrimmer? = null


    private var progressKHUD: KProgressHUD? = null
    private var mediaProgressKHUD: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videotrim)
        var path = ""
        path = DialogChat.videoPath

        try {
            this.supportActionBar!!.hide()
        } catch (e: Exception) {

        }


        rlBackArrow.setOnClickListener {
            finish()
        }


        progressKHUD = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(10f)
            .show()

        createUploadMediaProgressbar()



        mVideoTrimmer =
            findViewById<View>(R.id.timeLine) as K4LVideoTrimmer



        if (mVideoTrimmer != null) {

            mVideoTrimmer!!.setMaxDuration(30)
            mVideoTrimmer!!.setOnTrimVideoListener(this)
            mVideoTrimmer!!.setOnK4LVideoListener(this)
            //mVideoTrimmer.setDestinationPath("/storage/emulated/0/DCIM/CameraCustom/");
            mVideoTrimmer!!.setVideoURI(Uri.parse(path))
            mVideoTrimmer!!.setVideoInformationVisibility(true)

        }
    }

    override fun onTrimStarted() {
        progressKHUD!!.show()
    }

    override fun getResult(uri: Uri) {


        runOnUiThread {
            progressKHUD?.dismiss()
            uri.path?.let { compressVideoFun(it) }
        }


    }

    override fun cancelAction() {
        progressKHUD!!.dismiss()
        mVideoTrimmer!!.destroy()
        finish()
    }

    override fun onError(message: String) {
        progressKHUD!!.dismiss()
        runOnUiThread {
            Toast.makeText(
                this@TrimmerActivity,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onVideoPrepared() {
        runOnUiThread {
            progressKHUD?.dismiss()
        }
    }


    var compressFilePath = ""
    val APP_DIR = "MaulijCompressor"
    val COMPRESSED_DIR = "/CompressedMualij/"
    val TEMP_DIR = "/Temp/"


    private fun try2CreateCompressDir() {
        var f = File(
            Environment.getExternalStorageDirectory(),
            File.separator + APP_DIR
        )
        f.mkdirs()
        f = File(
            Environment.getExternalStorageDirectory(),
            File.separator + APP_DIR + COMPRESSED_DIR
        )
        f.mkdirs()
        f = File(
            Environment.getExternalStorageDirectory(),
            File.separator + APP_DIR + TEMP_DIR
        )
        f.mkdirs()
    }


    private fun getFilePath(): String {
        try2CreateCompressDir()
        return (Environment.getExternalStorageDirectory()
            .toString() + File.separator
                + APP_DIR
                + COMPRESSED_DIR + "VIDEO_" + SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.US
        ).format(Date()) + ".mp4")
    }

    private fun compressVideoFun(path: String) {

        showMediaProgress(resources.getString(R.string.tv_please_wait_file_is_prepared))
        compressFilePath = getFilePath()
        Mp4Composer(path, compressFilePath)
            .size(540, 610)
            .fillMode(FillMode.PRESERVE_ASPECT_FIT)
            .listener(object : Mp4Composer.Listener {
                override fun onProgress(progress: Double) {
                    mediaProgressKHUD!!.setProgress((progress * 100).toInt())
                }

                override fun onCompleted() {

                    runOnUiThread {

                        hideMediaProgress()
                        DialogChat.videoPath = compressFilePath
                        val intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        finish()

                    }
                }

                override fun onCanceled() {
                    progressKHUD!!.dismiss()
                }

                override fun onFailed(exception: java.lang.Exception?) {
                    progressKHUD!!.dismiss()
                }
            })
            .start()
    }


    private fun createUploadMediaProgressbar() {
        mediaProgressKHUD = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
            .setCancellable(false)
            .setLabel(resources.getString(R.string.tv_please_wait_file_is_uploading))
            .setMaxProgress(100)
            .setDimAmount(10f)

    }


    private fun showMediaProgress(msg: String) {
        try {

            AppUtils.touchScreenDisable(this)
            if (mediaProgressKHUD != null) {

                if (!mediaProgressKHUD!!.isShowing) {

                    mediaProgressKHUD!!.setLabel(msg)
                    mediaProgressKHUD!!.setProgress(0)
                    mediaProgressKHUD!!.show() // Dismiss Progress Dialog
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    private fun hideMediaProgress() {
        try {
            AppUtils.touchScreenEnable(this)
            if (mediaProgressKHUD != null)
                if (mediaProgressKHUD!!.isShowing)
                    mediaProgressKHUD!!.dismiss() // Dismiss Progress Dialog
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}