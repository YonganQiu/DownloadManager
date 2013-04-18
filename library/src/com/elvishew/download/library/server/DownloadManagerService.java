package com.elvishew.download.library.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownloadManagerService extends Service {

    private IBinder mDownloadManagerService;

    @Override
    public IBinder onBind(Intent intent) {
        if (mDownloadManagerService == null) {
            mDownloadManagerService = DownloadManagerImpl.getInstance(this).asBinder();
        }
        return mDownloadManagerService;
    }

}
