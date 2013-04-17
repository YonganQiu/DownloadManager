
package com.elvishew.download.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.elvishew.download.library.DownloadClient;
import com.elvishew.download.library.DownloadManager;
import com.elvishew.download.library.DownloadProgressData;
import com.elvishew.download.library.DownloadingItem;

import me.elvishew.download.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class DownloadListAdapter extends BaseAdapter {

    private Context mContext;
    private List<DownloadingItem> mDownloads = new ArrayList<DownloadingItem>();
    private HashMap<String, DownloadProgressData> mProgressMap = new HashMap<String, DownloadProgressData>();
    private DownloadManager mDownloadManager;

    public DownloadListAdapter(Context context, DownloadClient downloadClient, DownloadManager downloadManager) {
        mContext = context;
        mDownloadManager = downloadManager;
        mDownloads = mDownloadManager.getAllDownloadings("demo");
    }

    public void setItems(List<DownloadingItem> items) {
        mDownloads = items;
    }

    @Override
    public int getCount() {
        return mDownloads.size();
    }

    public void updateProgress(String url, DownloadProgressData progress) {
        if (/*mProgressMap.containsKey(item.getUrl())*/true) {
            for (DownloadingItem item : mDownloads) {
                if (item.getUrl().equals(url)) {
                    item.updateCompletedLength((int) progress.completedLength);
                }
            }
            mProgressMap.put(url, progress);
        }
    }

    public void addItem(DownloadingItem item) {
        mDownloads.add(item);
    }

    public void removeItem(String url) {
        for (DownloadingItem item : mDownloads) {
            if (item.getUrl().equals(url)) {
                mDownloads.remove(item);
                break;
            }
        }
        mProgressMap.remove(url);
    }

    public void updateItemState(String url, int state) {
        for (DownloadingItem item : mDownloads) {
            if (url.equals(item.getUrl())) {
                item.updateState(state);
                return;
            }
        }
    }

    @Override
    public DownloadingItem getItem(int position) {
        return mDownloads.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.download_list_item, null);
        }
        DownloadingItem item = getItem(position);

        ViewHolder viewHolder = new ViewHolder(convertView);
        viewHolder.setData(item, mProgressMap.get(item.getUrl()));

        viewHolder.downloadingButton.setOnClickListener(new DownloadBtnListener(item));
        viewHolder.pausedButton.setOnClickListener(new DownloadBtnListener(item));
        viewHolder.deleteButton.setOnClickListener(new DownloadBtnListener(item));
        viewHolder.pendingButton.setOnClickListener(new DownloadBtnListener(item));

        switch (item.getState()) {
            case DownloadingItem.STATE_DOWNLOADING:
                viewHolder.downloadingButton.setVisibility(View.VISIBLE);
                viewHolder.pausedButton.setVisibility(View.GONE);
                viewHolder.pendingButton.setVisibility(View.GONE);
                break;
            case DownloadingItem.STATE_PAUSED:
                viewHolder.downloadingButton.setVisibility(View.GONE);
                viewHolder.pausedButton.setVisibility(View.VISIBLE);
                viewHolder.pendingButton.setVisibility(View.GONE);
                break;
            case DownloadingItem.STATE_PENDING:
                viewHolder.downloadingButton.setVisibility(View.GONE);
                viewHolder.pausedButton.setVisibility(View.GONE);
                viewHolder.pendingButton.setVisibility(View.VISIBLE);
                break;
            default:
                viewHolder.downloadingButton.setVisibility(View.GONE);
                viewHolder.pausedButton.setVisibility(View.GONE);
                viewHolder.pendingButton.setVisibility(View.GONE);
                break;
        }

        return convertView;
    }

    private class DownloadBtnListener implements View.OnClickListener {
        private DownloadingItem item;

        public DownloadBtnListener(DownloadingItem item) {
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            int result = -1;
            switch (v.getId()) {
                case R.id.btn_downloading:
                case R.id.btn_pending:
                    result = mDownloadManager.pauseDownloading("demo", item.getUrl());
                    break;
                case R.id.btn_paused:
                    result = mDownloadManager.resumeDownloading("demo", item.getUrl());
                    break;
                case R.id.btn_delete:
                    result = mDownloadManager.deleteDownloading("demo", item.getUrl());
                    break;
            }
            if (result != DownloadManager.ERROR_NO_ERROR) {
                Toast.makeText(mContext, "error hapened: " + result, Toast.LENGTH_LONG).show();
            }
        }
    }
}
