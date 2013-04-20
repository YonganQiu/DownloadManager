package com.elvishew.download.library.client;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;

import com.elvishew.download.library.Constants;
import com.elvishew.download.library.R;
import com.elvishew.download.library.model.DownloadRequest;
import com.elvishew.download.library.model.DownloadedItem;
import com.elvishew.download.library.model.DownloadingItem;
import com.elvishew.download.library.server.IDownloadManager;

public class DownloadManager {

    protected final String TAG = "DownloadManager";

    public static final String DOWNLOAD_MANAGER_SERVICE = "ELVIS_download_manager";

    private static final String ACTION_DOWNLOAD_MANAGER_SERVICE = "com.elvishew.action.download";

    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_ALREADY_DOWNLOADED = 1;
    public static final int ERROR_ALREADY_ADDED = 2;
    public static final int ERROR_SDCARD_NOT_FOUND = 3;
    public static final int ERROR_SDCARD_NOT_WRITABLE = 4;
    public static final int ERROR_STORAGE_NOT_ENOUGH = 5;
    public static final int ERROR_DOWNLOADING_LIST_FULL = 6;
    public static final int ERROR_NETWORK_NOT_AVAILABLE = 7;
    public static final int ERROR_NETWORK_ERROR = 8;
    public static final int ERROR_FETCH_FILE_LENGTH_FAIL = 9;
    public static final int ERROR_CREATE_FILE_FAIL = 10;
    public static final int ERROR_DOWNLOAD_INIT_FAIL = 11;
    public static final int ERROR_DOWNLOAD_PARAMS_ERROR = 12;
    public static final int ERROR_TEMP_FILE_LOST = 13;
    public static final int ERROR_IO_ERROR = 14;
    public static final int ERROR_DUPLICATE_DOWNLOAD_REQUEST = 15;
    public static final int ERROR_UNKNOWN_ERROR = 16;
    public static final int ERROR_NETWORK_TIME_OUT = 17;
    public static final int ERROR_ALREADY_RESUMED = 18;
    public static final int ERROR_ALREADY_PAUSED = 19;
    public static final int ERROR_DOWNLOAD_NOT_FOUND = 20;
    public static final int ERROR_DOWNLOADED_DELETE_FAIL = 21;
    public static final int ERROR_DOWNLOADED_NOT_FOUND = 22;
    public static final int ERROR_DOWNLOAD_CLIENT_NOT_FOUND = 23;

    private IDownloadManager sServices;

    public interface ServiceConnectionListener {
        public void onServiceConnectionSuccess(DownloadManager downloadManager);
        public void onServiceConnectionFailed();
        public void onServiceConnectionDisconnected();
    }

    public static void bindService(final Context context, final ServiceConnectionListener l) {
        Intent service = new Intent(ACTION_DOWNLOAD_MANAGER_SERVICE);
        context.bindService(service, new ServiceConnection() {
            
            @Override
            public void onServiceDisconnected(ComponentName name) {
                l.onServiceConnectionDisconnected();
            }
            
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DownloadManager downloadManager = new DownloadManager(context,
                        IDownloadManager.Stub.asInterface(service));
                try {
                    service.linkToDeath(new DeathRecipient() {
                        @Override
                        public void binderDied() {
                            l.onServiceConnectionDisconnected();
                        }
                    }, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    l.onServiceConnectionFailed();
                }
                l.onServiceConnectionSuccess(downloadManager);
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private DownloadManager(Context context, IDownloadManager service) {
        sServices = service;
    }

    public static String errorToString(Context context, int errorCode) {
        int errorResId = Constants.INVALID_RES_ID;
        switch (errorCode) {
            case ERROR_ALREADY_DOWNLOADED:
                errorResId = R.string.download_error_already_downloaded;
                break;
            case ERROR_ALREADY_ADDED:
                errorResId = R.string.download_error_already_added;
                break;
            case ERROR_SDCARD_NOT_FOUND:
                errorResId = R.string.download_error_sdcard_not_found;
                break;
            case ERROR_SDCARD_NOT_WRITABLE:
                errorResId = R.string.download_error_sdcard_not_writable;
                break;
            case ERROR_STORAGE_NOT_ENOUGH:
                errorResId = R.string.download_error_storage_not_enough;
                break;
            case ERROR_DOWNLOADING_LIST_FULL:
                errorResId = R.string.download_error_downloading_list_full;
                break;
            case ERROR_NETWORK_NOT_AVAILABLE:
                errorResId = R.string.download_error_network_not_available;
                break;
            case ERROR_NETWORK_ERROR:
                errorResId = R.string.download_error_network_error;
                break;
            case ERROR_FETCH_FILE_LENGTH_FAIL:
                errorResId = R.string.download_error_fetch_file_length_fail;
                break;
            case ERROR_CREATE_FILE_FAIL:
                errorResId = R.string.download_error_create_file_fail;
                break;
            case ERROR_DOWNLOAD_INIT_FAIL:
                errorResId = R.string.download_error_download_init_fail;
                break;
            case ERROR_DOWNLOAD_PARAMS_ERROR:
                errorResId = R.string.download_error_download_params_error;
                break;
            case ERROR_TEMP_FILE_LOST:
                errorResId = R.string.download_error_temp_file_lost;
                break;
            case ERROR_IO_ERROR:
                errorResId = R.string.download_error_io_error;
                break;
            case ERROR_DUPLICATE_DOWNLOAD_REQUEST:
                errorResId = R.string.download_error_duplicate_download_request;
                break;
            case ERROR_UNKNOWN_ERROR:
                errorResId = R.string.download_error_unknown_error;
                break;
            case ERROR_NETWORK_TIME_OUT:
                errorResId = R.string.download_error_network_time_out;
                break;
            case ERROR_ALREADY_RESUMED:
                errorResId = R.string.download_error_already_resumed;
                break;
            case ERROR_ALREADY_PAUSED:
                errorResId = R.string.download_error_already_paused;
                break;
            case ERROR_DOWNLOAD_NOT_FOUND:
                errorResId = R.string.download_error_download_not_found;
                break;
            case ERROR_DOWNLOADED_DELETE_FAIL:
                errorResId = R.string.download_error_downloaded_delete_fail;
                break;
            case ERROR_DOWNLOADED_NOT_FOUND:
                errorResId = R.string.download_error_downloaded_not_found;
                break;
            case ERROR_DOWNLOAD_CLIENT_NOT_FOUND:
                errorResId = R.string.download_error_download_client_not_found;
                break;

        }
        if (errorResId == Constants.INVALID_RES_ID) {
            return "";
        } else {
            return context.getResources().getString(errorResId);
        }
    }

    public void registerClient(String requester, IDownloadClient client) {
        try {
            sServices.registerClient(requester, client);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unregisterClient(String requester) {
        try {
            sServices.unregisterClient(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public List<DownloadingItem> getAllDownloadings(String requester) {
        try {
            return sServices.getAllDownloadings(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DownloadedItem> getAllDownloadeds(String requester) {
        try {
            return sServices.getAllDownloadeds(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasDownloading(String requester, String url) {
        try {
            return sServices.hasDownloading(requester, url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasDownloadings(String requester) {
        try {
            return sServices.hasDownloadings(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasDownloaded(String requester, String url) {
        try {
            return sServices.hasDownloaded(requester, url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasDownloadeds(String requester) {
        try {
            return sServices.hasDownloadeds(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int addDownloading(DownloadRequest request) {
        try {
            return sServices.addDownloading(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public int resumeDownloading(String requester, String url) {
        try {
            return sServices.resumeDownloading(requester, url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public int deleteDownloading(String requester, String url) {
        try {
            return sServices.deleteDownloading(requester, url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public int deleteAllDownloadings(String requester) {
        try {
            return sServices.deleteAllDownloadings(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public int pauseDownloading(String requester, String url) {
        try {
            return sServices.pauseDownloading(requester, url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public int pauseAllDownloadings(String requester) {
        try {
            return sServices.pauseAllDownloadings(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public int deleteDownloaded(String requester, String url) {
        try {
            return sServices.deleteDownloaded(requester, url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public int deleteAllDownloadeds(String requester) {
        try {
            return sServices.deleteAllDownloadeds(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public String getLocalUrlIfDownloaded(String requester, String url) {
        try {
            return sServices.getLocalUrlIfDownloaded(requester, url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
