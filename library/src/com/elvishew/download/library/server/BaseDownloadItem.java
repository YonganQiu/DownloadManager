
package com.elvishew.download.library.server;

import android.os.Parcel;

/**
 * Video that you have try to download, no matter downloading has been finished
 * or not.
 * 
 * @author yongan.qiu@gmail.com
 */
public abstract class BaseDownloadItem extends DownloadableItem {

    protected String mRequester;

    protected String mSavePath;

    protected int mFileLength;

    public BaseDownloadItem(String name, String url, String requester, String savePath,
            int fileLength) {
        super(name, url);
        mRequester = requester;
        mSavePath = savePath;
        mFileLength = fileLength;
    }

    public BaseDownloadItem(Parcel source) {
        super(source);
        mRequester = source.readString();
        mSavePath = source.readString();
        mFileLength = source.readInt();
    }

    /**
     * Return the requester of this download.
     * 
     * @return the requester of this download
     */
    public String getRequester() {
        return mRequester;
    }

    /**
     * Return the path you save this video.
     * 
     * @return the path you save this video
     */
    public String getSavePath() {
        return mSavePath;
    }

    /**
     * Return the length of the video file you try to download.
     * 
     * @return the length of the video file
     */
    public int getFileLength() {
        return mFileLength;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mRequester);
        dest.writeString(mSavePath);
        dest.writeInt(mFileLength);
    }

}
