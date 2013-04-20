package com.elvishew.download.library.server;

import android.os.AsyncTask;

import com.elvishew.download.library.DownloadException;
import com.elvishew.download.library.client.DownloadManager;
import com.elvishew.download.library.model.DownloadRequest;
import com.elvishew.download.library.model.DownloadableItem;
import com.elvishew.download.library.model.DownloadingItem;
import com.elvishew.download.library.utils.HttpUtil;
import com.elvishew.download.library.utils.PathUtil;

class CreateLoaclFileTask extends AsyncTask<DownloadRequest, Void, DownloadingItem> {

    private Callback mCallback;

    private int mErrorCode = DownloadManager.ERROR_UNKNOWN_ERROR;

    CreateLoaclFileTask(Callback callback) {
        super();
        mCallback = callback;
    }

    @Override
    protected DownloadingItem doInBackground(DownloadRequest... params) {
        if (params == null || params.length < 1) {
            mErrorCode = DownloadManager.ERROR_DOWNLOAD_PARAMS_ERROR;
            return null;
        }
        long fileLength = -1;
        DownloadRequest param = params[0];
        DownloadableItem downloadable = param.downloadable;
        String savePath = param.savePath;
        try {
            fileLength = HttpUtil.getRemoteFileLength(downloadable.getUrl());
            HttpUtil.createLocalTempFile(PathUtil.getTempFile(savePath), fileLength);
            return new DownloadingItem(downloadable, param.requester, savePath, (int) fileLength,
                    System.currentTimeMillis());
        } catch (DownloadException e) {
            e.printStackTrace();
            mErrorCode = e.errorCode;
        }
        return null;
    }

    @Override
    protected void onPostExecute(DownloadingItem result) {
        if (result == null) {
            mCallback.onError(new DownloadException(mErrorCode));
        } else {
            mCallback.onDone(result);
        }
    }

    @Override
    protected void onCancelled() {
        mCallback.onError(new DownloadException(DownloadManager.ERROR_DOWNLOAD_INIT_FAIL));
    }

    public interface Callback {
        public void onError(DownloadException exception);
        public void onDone(DownloadingItem fileLength);
    }
}
