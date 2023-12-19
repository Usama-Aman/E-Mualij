package com.elementary.mualijpro.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class WriteFCMToFile {

    public static void objectToFile(Object object) throws IOException {

        if (isExternalStorageWritable()) {

            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/FCM-Patient");
            File logFile = new File(appDirectory, "FCMPatient" + ".txt");

            // create app folder
            if (!appDirectory.exists()) {
                appDirectory.mkdir();
            }

            // create log folder
            if (!appDirectory.exists()) {
                appDirectory.mkdir();
            }

            // create log folder
            if (!logFile.exists()) {
                logFile.createNewFile();
            }



            // clear the previous logcat and then write the new one to the file
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(logFile, true));
                objectOutputStream.writeObject(object);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
