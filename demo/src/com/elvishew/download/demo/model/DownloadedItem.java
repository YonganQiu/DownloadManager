package com.elvishew.download.demo.model;

import android.os.Parcel;

/**
 * Video you have downloaded.
 * 
 * @author yongan.qiu@gmail.com
 */
public class DownloadedItem extends BaseDownloadItem {

    protected long mFinishTime;

    public DownloadedItem(String name, String url, String thumbUrl, String requester,
            String savePath, int fileLength, long finishTime) {
        super(name, url, thumbUrl, requester, savePath, fileLength);
        mFinishTime = finishTime;
    }

    public DownloadedItem(Parcel source) {
        super(source);
        mFinishTime = source.readLong();
    }

    /**
     * Return the time this video is completely downloaded.
     * 
     * @return the time this video is completely downloaded.
     */
    public long getFinishTime() {
        return mFinishTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(mFinishTime);
    }

    public static final Creator<DownloadedItem> CREATOR = new Creator<DownloadedItem>() {
        @Override
        public DownloadedItem createFromParcel(Parcel source) {
            return new DownloadedItem(source);
        }

        @Override
        public DownloadedItem[] newArray(int size) {
            return new DownloadedItem[size];
        }
    };

    @Override
    public DownloadedItem copy() {
        return new DownloadedItem(mName, mUrl, mThumbUrl, mRequester, mSavePath, mFileLength,
                mFinishTime);
    }

}
