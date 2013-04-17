package com.elvishew.download.demo.model;

import me.elvishew.download.R;
import android.content.res.Resources;
import android.os.Parcel;


/**
 * Video that you are downloading, not finished yet.
 * 
 * @author yongan.qiu@gmail.com
 */
public class DownloadingItem extends BaseDownloadItem {

    public static final int STATE_ORIGIN = 0;
    public static final int STATE_DOWNLOADING = 0x1;
    public static final int STATE_PAUSED = 0x2;
    public static final int STATE_PENDING = 0x4;

    public static final int STATE_MASK = 0x7;

    private long mStartTime;

    private int mCompletedLength;

    private int mState;

    public DownloadingItem(VideoItem video, String requester, String savePath, int fileLength,
            long startTime) {
        this(video.getName(), video.getUrl(), video.getThumbUrl(), requester, savePath, fileLength,
                startTime);
    }

    public DownloadingItem(String name, String url, String thumbUrl, String requester,
            String savePath, int fileLength, long startTime) {
        this(name, url, thumbUrl, requester, savePath, fileLength, startTime, 0, STATE_ORIGIN);
    }

    public DownloadingItem(String name, String url, String thumbUrl, String requester,
            String savePath, int fileLength, long startTime, int completedLength, int state) {
        super(name, url, thumbUrl, requester, savePath, fileLength);
        mStartTime = startTime;
        mCompletedLength = completedLength;
        mState = state;
    }

    public DownloadingItem(Parcel source) {
        super(source);
        mStartTime = source.readLong();
        mCompletedLength = source.readInt();
        mState = source.readInt();
    }

    /**
     * Return the time you start download this video.
     * 
     * @return the time you start download this video
     */
    public long getStartTime() {
        return mStartTime;
    }

    /**
     * Return the completed length of the video file you are downloading.
     * 
     * @return the completed length of the video file
     */
    public int getCompletedLength() {
        return mCompletedLength;
    }

    /**
     * Update the completed length of the video file you are downloading.
     * 
     * @param completedLength the completed length of the video file
     */
    public void updateCompletedLength(int completedLength) {
        mCompletedLength = completedLength;
    }

    /**
     * Return the download state of this video.
     * 
     * @return
     */
    public int getState() {
        return mState;
    }

    /**
     * Update download state.
     * 
     * @param state the new download state
     */
    public void updateState(int state) {
        mState = state;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(mStartTime);
        dest.writeInt(mCompletedLength);
        dest.writeInt(mState);
    }

    public static final Creator<DownloadingItem> CREATOR = new Creator<DownloadingItem>() {
        @Override
        public DownloadingItem createFromParcel(Parcel source) {
            return new DownloadingItem(source);
        }

        @Override
        public DownloadingItem[] newArray(int size) {
            return new DownloadingItem[size];
        }
    };

    @Override
    public DownloadingItem copy() {
        return new DownloadingItem(mName, mUrl, mThumbUrl, mRequester, mSavePath, mFileLength,
                mStartTime, mCompletedLength, mState);
    }

    public static String stateToString(Resources res, int state) {
        String stateStr;
        switch (state) {
            case STATE_DOWNLOADING:
                stateStr = res.getString(R.string.state_downloading);
                break;
            case STATE_PAUSED:
                stateStr = res.getString(R.string.state_paused);
                break;
            case STATE_PENDING:
                stateStr = res.getString(R.string.state_pending);
                break;
            default:
                stateStr = "";
                break;
        }
        return stateStr;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("DownloadingItem[name=");
        builder.append(getName()).append(" url=").append(getUrl()).append(" thumbUrl=")
                .append(getThumbUrl()).append(" requester=").append(getRequester())
                .append(" savePath=").append(getSavePath())
                .append(" fileLength=").append(getFileLength()).append(" startTime=")
                .append(getStartTime()).append(" completedLength=").append(getCompletedLength())
                .append(" state=").append(getState()).append("]");
        return builder.toString();
    }
}
