
package com.elvishew.download.demo;

import java.util.List;

import com.elvishew.download.library.DownloadClient;
import com.elvishew.download.library.DownloadManager;
import com.elvishew.download.library.DownloadProgressData;
import com.elvishew.download.library.DownloadRequest;
import com.elvishew.download.library.DownloadedItem;
import com.elvishew.download.library.DownloadingItem;
import com.elvishew.download.library.VideoItem;
import com.elvishew.download.library.utils.PathUtil;
import com.elvishew.download.library.utils.StorageUtils;

import me.elvishew.download.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class DownloadListActivity extends Activity {

    public static VideoItem[] videos = {
            new VideoItem("小毛驴", "http://rs.qipaoxian.com/mp4/001.mp4",
                    "http://www.qipaoxian.com/iso/pic/1356156732538.jpg"),
            new VideoItem("世上只有妈妈好", "http://rs.qipaoxian.com/mp4/002.mp4",
                    "http://www.qipaoxian.com/iso/pic/1356156805432.jpg"),
            new VideoItem("爱我你就抱抱我", "http://rs.qipaoxian.com/mp4/003.mp4",
                    "http://www.qipaoxian.com/iso/pic/1356156858206.jpg"),
            new VideoItem("春天在哪里", "http://rs.qipaoxian.com/mp4/004.mp4",
                    "http://www.qipaoxian.com/iso/pic/1356156893482.jpg"),
            new VideoItem("种太阳", "http://rs.qipaoxian.com/mp4/055.MP4",
                    "http://www.qipaoxian.com/iso/pic/1356147389136.jpg")
    };

    private static final String TAG = "DownloadActivity";

    private DownloadManager mDownloadManager;

    private ListView mList;
    private Button mAddDownloadButton;
    private Button mPauseAllButton;

    private DownloadClient mDownloadClient;

    private DownloadListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.download_list_activity);

        mDownloadManager = DownloadManager.getDefault(this);
        mDownloadClient = new DefaultDownloadClient();
        mDownloadManager.registerClient("demo", mDownloadClient.getIDownloadClient());

        if (!StorageUtils.isSDCardPresent()) {
            Toast.makeText(this, "未发现SD卡", Toast.LENGTH_LONG).show();
            return;
        }

        if (!StorageUtils.isSdCardWrittenable()) {
            Toast.makeText(this, "SD卡不能读写", Toast.LENGTH_LONG).show();
            return;
        }

        mList = (ListView) findViewById(R.id.download_list);
        mAdapter = new DownloadListAdapter(this, mDownloadClient, mDownloadManager);
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
                int result = mDownloadManager.pauseAllDownloadings("demo");
                if (result != DownloadManager.ERROR_NO_ERROR) {
                    Toast.makeText(DownloadListActivity.this, "Error happened: " + result, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void addDownload(int index) {
        if (index >= videos.length) {
            index = 0;
        }
        VideoItem video = videos[index];
        int result = mDownloadManager.addDownloading(new DownloadRequest(video, "demo", PathUtil.getVideoFilePath(video.getName(), video.getUrl())));
        if (result != DownloadManager.ERROR_NO_ERROR) {
            Toast.makeText(DownloadListActivity.this, "Error happened: " + result, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDownloadManager.pauseAllDownloadings("demo");
        mDownloadManager.unregisterClient("demo");
    }

    class DefaultDownloadClient extends DownloadClient {

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
