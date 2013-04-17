package com.elvishew.download.library.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.elvishew.download.library.utils.HttpChecker;
import com.elvishew.download.library.utils.PathUtil;

class DownloadTask extends AsyncTask<Void, Integer, Long> {

    private static final boolean DEBUG = true;
    private static final String TAG = "DownloadTask";

    /** {@value} */
    private static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
    /** {@value} */
    private static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds
    /** {@value} */
    private static final int BUFFER_SIZE = 8 * 1024; // 8 Kb

    private Object mLock = new Object();

    private Context mContext;
    private DownloadCallbacks mDownloadCallbacks;

    private DownloadingItem mDownloadingItem;
    private File mFile;
    private File mTmpFile;

    private long mDownloadedLengthInThisTime;
    private long mCompletedLengthTillLastTime;

    private long mRealTimeSpeed;
    private long mStartTime;
    private DownloadException mError;
    private boolean mShouldBeCanceled = false;
    private boolean mFinished = false;

    private Handler mUpdateProgressHandler = new Handler() {

        private long mLastUpdateTime = mStartTime;
        private long mLastUpdateLength = mCompletedLengthTillLastTime;

        @Override
        public void handleMessage(Message msg) {
            if (!mShouldBeCanceled && !mFinished) {
                sendEmptyMessageDelayed(0, 1000);
            }

            Log.i(TAG, "mUpdateProgressHandler#handleMessage: ");
            long lengthThisTime = getDownloadedLengthInThisTime();
            long completedLength = lengthThisTime + mCompletedLengthTillLastTime;
            mDownloadingItem.updateCompletedLength((int) completedLength);

            long now = System.currentTimeMillis();
            long costTime = now - mLastUpdateTime;
            mLastUpdateTime = now;
            long downloadLength = completedLength - mLastUpdateLength;
            mLastUpdateLength = completedLength;
            mRealTimeSpeed = downloadLength / costTime;

            mDownloadCallbacks.onDownloadProgressUpdate(DownloadTask.this, completedLength,
                    mRealTimeSpeed);
        };
    };

    private AndroidHttpClient client;

    DownloadTask(Context context, DownloadingItem downloading, DownloadCallbacks listener) {
        super();
        mContext = context;
        mDownloadingItem = downloading;
        mCompletedLengthTillLastTime = mDownloadingItem.getCompletedLength();
        mFile = new File(downloading.getSavePath());
        mTmpFile = new File(PathUtil.getVideoTempFilePath(downloading.getName(),
                downloading.getUrl()));

        mDownloadCallbacks = listener;
    }

    public String getRequester() {
        return mDownloadingItem.getRequester();
    }

    public DownloadingItem getDownloadingItem() {
        return mDownloadingItem;
    }

    public String getUrl() {
        return mDownloadingItem.getUrl();
    }

    private long getDownloadedLengthInThisTime() {
        long length = 0;
        synchronized (mLock) {
            length = mDownloadedLengthInThisTime;
        }
        return length;
    }

    private void setDownloadedLengthInThisTime(long length) {
        synchronized (mLock) {
            mDownloadedLengthInThisTime = length;
        }
    }

    @Override
    protected void onPreExecute() {
        mStartTime = System.currentTimeMillis();
        if (mDownloadCallbacks != null)
            mDownloadCallbacks.onPreDownload(this);
    }

    @Override
    protected Long doInBackground(Void... params) {
        long result = -1;
        try {
            result = download();
        } catch (DownloadException e) {
            mError = e;
        } catch (FileNotFoundException e) {
            mError = new DownloadException(DownloadManager.ERROR_TEMP_FILE_LOST);
        } catch (IOException e) {
            mError = new DownloadException(DownloadManager.ERROR_IO_ERROR);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Long result) {
        if (result < 0 || mShouldBeCanceled || mError != null) {
            // Some error.
            if (mDownloadCallbacks != null) {
                mDownloadCallbacks.onDownloadError(this, mError);
            }
        } else {
            // Finish download
            mTmpFile.renameTo(mFile);
            if (mDownloadCallbacks != null) {
                mDownloadCallbacks.onPostDownload(this);
            }
        }

        mFinished = true;
    }

    @Override
    public void onCancelled() {
        super.onCancelled();
        mShouldBeCanceled = true;
    }

    private long download() throws IOException, DownloadException, FileNotFoundException {
        // Check network.
        if (!HttpChecker.isNetworkAvailable(mContext)) {
            throw new DownloadException(DownloadManager.ERROR_NETWORK_NOT_AVAILABLE);
        }

        // Check file exist.
        if (mFile.exists()) {
            if (DEBUG) {
                Log.w(TAG, "File already exists, skipping download.");
            }
            throw new DownloadException(DownloadManager.ERROR_ALREADY_DOWNLOADED);
        } else if (!mTmpFile.exists()) {
            throw new DownloadException(DownloadManager.ERROR_TEMP_FILE_LOST);
        }

        client = AndroidHttpClient.newInstance("QiPaoXianClient");
        HttpGet httpGet = new HttpGet(mDownloadingItem.getUrl());
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_HTTP_CONNECT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_HTTP_READ_TIMEOUT);
        httpGet.setParams(httpParams);
        httpGet.addHeader("Range", "bytes=" + mDownloadingItem.getCompletedLength() + "-" + (mDownloadingItem.getFileLength() - 1));
        HttpResponse response = client.execute(httpGet);

        // Start download.
        RandomAccessFile outputFile = new RandomAccessFile(mTmpFile, "rw");

        mUpdateProgressHandler.sendEmptyMessage(0);

        InputStream input = response.getEntity().getContent();
        int bytesCopied = copy(input, outputFile);

        if (!mShouldBeCanceled
                && (mCompletedLengthTillLastTime + bytesCopied) != mDownloadingItem.getFileLength()) {
            throw new DownloadException(DownloadManager.ERROR_UNKNOWN_ERROR);
        }

        if (DEBUG) {
            Log.v(TAG, "Download completed successfully: " + mDownloadingItem);
        }

        return bytesCopied;

    }

    private int copy(InputStream input, RandomAccessFile output) throws IOException, DownloadException {
        if (input == null || output == null) {
            client.close(); // must close client first
            client = null;
            return -1;
        }

        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);

        int copyedAll = 0, copyed = 0;
        setDownloadedLengthInThisTime(copyedAll);
        long previousTime = System.currentTimeMillis(), nowTime, costTime;

        try {
            output.seek(mDownloadingItem.getCompletedLength());
            while (!mShouldBeCanceled) {
                copyed = in.read(buffer, 0, BUFFER_SIZE);
                if (copyed < 0) {
                    break;
                }
                output.write(buffer, 0, copyed);
                copyedAll += copyed;
                setDownloadedLengthInThisTime(copyedAll);

                // Check network.
                if (!HttpChecker.isNetworkAvailable(mContext)) {
                    throw new DownloadException(DownloadManager.ERROR_NETWORK_NOT_AVAILABLE);
                }

                // Check timeout.
                nowTime = System.currentTimeMillis();
                costTime = nowTime - previousTime;
                previousTime = nowTime;
                if (costTime > DEFAULT_HTTP_READ_TIMEOUT) {
                    throw new DownloadException(DownloadManager.ERROR_NETWORK_TIME_OUT);
                }
            }
        } finally {
            client.close(); // must close client first
            client = null;
            output.close();
            in.close();
            input.close();
        }
        return copyedAll;
    }

    interface DownloadCallbacks {

        void onDownloadProgressUpdate(DownloadTask task, long completedLength, long averageSpeed);

        void onPostDownload(DownloadTask task);

        void onPreDownload(DownloadTask task);

        void onDownloadError(DownloadTask task, DownloadException error);
    }

}
