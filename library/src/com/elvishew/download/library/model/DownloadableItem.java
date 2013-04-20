package com.elvishew.download.library.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DownloadableItem implements Parcelable {

    protected String mName;

    protected String mUrl;

    public DownloadableItem(String name, String url) {
        mName = name;
        mUrl = url;
   }

    public DownloadableItem(Parcel source) {
        mName = source.readString();
        mUrl = source.readString();
    }

    /**
     * Return name of this downloadable.
     * 
     * @return name of this downloadable
     */
    public String getName() {
        return mName;
    }

    /**
     * Return remote url of this downloadable.
     * 
     * @return remote url of this downloadable
     */
    public String getUrl() {
        return mUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mUrl);
    }

    public static final Creator<DownloadableItem> CREATOR = new Creator<DownloadableItem>() {
        @Override
        public DownloadableItem createFromParcel(Parcel source) {
            return new DownloadableItem(source);
        }

        @Override
        public DownloadableItem[] newArray(int size) {
            return new DownloadableItem[size];
        }
    };

    @Override
    public String toString() {
        return "DownloadableItem[name=" + mName + " url=" + mUrl + "]";
    };

    /**
     * Copy a new instance form itself.
     * 
     * @return a new instance copied
     */
    public DownloadableItem copy() {
        return new DownloadableItem(mName, mUrl);
    }
}
