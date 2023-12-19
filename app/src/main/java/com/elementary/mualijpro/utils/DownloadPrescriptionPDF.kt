package com.elementary.mualijpro.utils

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.elementary.mualijpro.R
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@Suppress("DEPRECATION")
class DownloadPrescriptionPDF(private var activity: Activity) : AsyncTask<String, String, String>() {

    private var fileName = "prescription.pdf"
    private var folder = ""

    override fun onPreExecute() {
        super.onPreExecute()
        Loader.showLoader(activity!!)
    }

    override fun doInBackground(vararg fileURL: String): String {
        var count: Int
        try {
            val url = URL(fileURL[0])
            val connection = url.openConnection()
            connection.connect()
            val lengthOfFile = connection.contentLength
            val input = BufferedInputStream(url.openStream(), 8192)
            folder = Environment.getExternalStorageDirectory().toString() + File.separator + Constants.directoryName

            val directory = File(folder!!)
            if (!directory.exists())
                directory.mkdirs()

            val output = FileOutputStream(folder!! + fileName!!)
            val data = ByteArray(1024)
            var total: Long = 0
            count = input.read(data)
            while (count != -1) {
                total += count.toLong()
                publishProgress("" + (total * 100 / lengthOfFile).toInt())
                output.write(data, 0, count)
                count = input.read(data)
            }
            output.flush()
            output.close()
            input.close()
            return "$folder$fileName"

        } catch (e: Exception) {

        }
        return ""
    }

    override fun onPostExecute(message: String) {
        Loader.hideLoader()
        if (message.isEmpty())
            CustomAlert.showDropDownNotificationError(
                activity!!,
                activity!!.resources.getString(R.string.txt_alert_information),
                activity!!.resources.getString(R.string.txt_something_went_wrong)
            )
        else {
            var pdfUri =
                FileProvider.getUriForFile(activity!!, "com.elementary.mualijpro.provider", File(message))
            var share = Intent()
            share.action = Intent.ACTION_SEND;
            share.type = "application/pdf"
            share.putExtra(Intent.EXTRA_STREAM, pdfUri)
            activity!!.startActivity(share)
        }
    }
}