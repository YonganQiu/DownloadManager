
package com.elvishew.download.clientdemo;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.elvishew.download.library.DownloadListener;
import com.elvishew.download.library.client.DownloadService;
import com.elvishew.download.library.client.DownloadService.OnServiceStateChangedListener;
import com.elvishew.download.library.server.DownloadManager;
import com.elvishew.download.library.server.DownloadProgressData;
import com.elvishew.download.library.server.DownloadRequest;
import com.elvishew.download.library.server.DownloadableItem;
import com.elvishew.download.library.server.DownloadedItem;
import com.elvishew.download.library.server.DownloadingItem;
import com.elvishew.download.library.utils.PathUtil;
import com.elvishew.download.library.utils.StorageUtils;

public class DownloadListActivity extends Activity {

    public static DownloadableItem[] downloadables = {
            new DownloadableItem("小毛驴", "http://rs.qipaoxian.com/mp4/001.mp4"),
            new DownloadableItem("世上只有妈妈好", "http://rs.qipaoxian.com/mp4/002.mp4"),
            new DownloadableItem("爱我你就抱抱我", "http://rs.qipaoxian.com/mp4/003.mp4"),
            new DownloadableItem("春天在哪里", "http://rs.qipaoxian.com/mp4/004.mp4"),
            new DownloadableItem("种太阳", "http://rs.qipaoxian.com/mp4/055.MP4")
    };

    private static final String TAG = "DownloadActivity";

    private DownloadService mDownloadService;

    private ListView mList;
    private Button mAddDownloadButton;
    private Button mPauseAllButton;

    private DownloadListener mDownloadListener;

    private DownloadListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.download_list_activity);

        mDownloadService = new DownloadService(this, "demo", new DownloadService.OnServiceStateChangedListener() {
            @Override
            public void onServiceStateChanged(boolean alive) {
                // TODO Auto-generated method stub
                mDownloadService.registerDownloadListener(mDownloadListener);
                mDownloadListener = new DefaultDownloadClient();

                if (!StorageUtils.isSDCardPresent()) {
                    Toast.makeText(DownloadListActivity.this, "未发现SD卡", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!StorageUtils.isSdCardWrittenable()) {
                    Toast.makeText(DownloadListActivity.this, "SD卡不能读写", Toast.LENGTH_LONG).show();
                    return;
                }

                mList = (ListView) findViewById(R.id.download_list);
                mAdapter = new DownloadListAdapter(DownloadListActivity.this, mDownloadService);
                mList.setAdapter(mAdapter);

                mAddDownloadButton = (Button) findViewById(R.id.btn_add1);
                mPauseAllButton = (Button) findViewById(R.id.btn_pause_all);

                mAddDownloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addDownload(0);
                    }
                });

                findViewById(R.id.btn_add2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addDownload(1);
                    }
                });
                findViewById(R.id.btn_add3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addDownload(2);
                    }
                });
                findViewById(R.id.btn_add4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addDownload(3);
                    }
                });
                findViewById(R.id.btn_add5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addDownload(4);
                    }
                });
                mPauseAllButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int result = mDownloadService.pauseAllDownloadings();
                        if (result != DownloadManager.ERROR_NO_ERROR) {
                            Toast.makeText(DownloadListActivity.this, "Error happened: " + result, Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

    }

    private void addDownload(int index) {
        if (index >= downloadables.length) {
            index = 0;
        }
        DownloadableItem downloadable = downloadables[index];
        int result = mDownloadService.addDownloading(downloadable,
                PathUtil.getVideoFilePath(downloadable.getName(), downloadable.getUrl()));
        if (result != DownloadManager.ERROR_NO_ERROR) {
            Toast.makeText(DownloadListActivity.this, "Error happened: " + result, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDownloadService.pauseAllDownloadings();
        mDownloadService.unregisterDownloadListener(mDownloadListener);
    }

    class DefaultDownloadClient implements DownloadListener {

        @Override
        public void onDownloadAddingFail(DownloadRequest request, int errorCode) {
            Toast.makeText(DownloadListActivity.this, "Init fail: " + errorCode, Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onDownloadingAdded(DownloadingItem download) {
            Log.i(TAG, "onDownloadingAdded(): " + download);
            mAdapter.addItem(download);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDownloadingStateChanged(DownloadingItem download) {
            Log.i(TAG, "onDownloadingStateChanged(): " + download);
            mAdapter.updateItemState(download.getUrl(), download.getState());
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDownloadingsStateChanged(List<DownloadingItem> downloads)
                {
            Log.i(TAG, "onDownloadingsStateChanged()");
            for (DownloadingItem item : downloads) {
                mAdapter.updateItemState(item.getUrl(), item.getState());
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDownloadingDeleted(DownloadingItem download) {
            Log.i(TAG, "onDownloadingDeleted(): " + download);
            mAdapter.removeItem(download.getUrl());
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDownloadingProgressUpdate(DownloadingItem download,
                DownloadProgressData progress) {
            Log.i(TAG, "onDownloadingProgressUpdate(): " + download);
            mAdapter.updateProgress(download.getUrl(), progress);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDownloadingError(DownloadingItem download, int errorCode) {
            Toast.makeText(DownloadListActivity.this, "Downloading fail: " + errorCode,
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDownloadedDeleted(DownloadedItem download) {
            
        }

        @Override
        public void onDownloadingsDeleted(List<DownloadingItem> downloads) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onDownloadedAdded(DownloadedItem download) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onDownloadedsDeleted(List<DownloadedItem> downloads) {
            // TODO Auto-generated method stub
            
        }
        
    }

}
