package com.elementary.mualijpro.utils;


import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {
    private File mFile;
    private String mPath;
    private UploadCallbacks mListener;
    String contentType;
    boolean isUpdateStart = false;
    int aTempInt = 0;
    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);
    }

    public ProgressRequestBody(final File file, String content_type, final UploadCallbacks listener) {
        mFile = file;
        mListener = listener;
        contentType = content_type;
        isUpdateStart = false;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(contentType + "/*");
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {


                // update progress on UI thread
                handler.post(new ProgressUpdater(uploaded, fileLength));

                uploaded += read;
                sink.write(buffer, 0, read);
                sink.flush();
            }
        } finally {
            in.close();
        }
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;

        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {


            int progress = (int) (100 * mUploaded / mTotal);
            mListener.onProgressUpdate(progress);
         /*   if (progress == 99)
                aTempInt = aTempInt + 1;
            if (aTempInt > 0 && progress == 0) {
                isUpdateStart = true;
            }



            if (isUpdateStart)
                mListener.onProgressUpdate(progress);
            else
                mListener.onProgressUpdate(0);*/

        }
    }
}