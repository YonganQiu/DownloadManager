package com.elvishew.download.demo;

import android.app.Application;
import android.os.IBinder;

import com.elvishew.download.library.DownloadManager;
import com.elvishew.download.library.DownloadManagerService;
import com.elvishew.download.library.IDownloadManager;

public class DownloadApplication extends Application {

    private IBinder mDownloadManagerService;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public Object getSystemService(String name) {
        if (DownloadManager.DOWNLOAD_MANAGER_SERVICE.equals(name)) {
            if (mDownloadManagerService == null) {
                mDownloadManagerService = new DownloadManagerService(this);
            }
            return IDownloadManager.Stub.asInterface(mDownloadManagerService);
        }
        return super.getSystemService(name);
    }

}
