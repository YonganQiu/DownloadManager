package com.elvishew.download.library.server;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.elvishew.download.library.server.DownloadModel.BaseDownloadableColumns;

public class DownloadableItem implements Parcelable {

    protected String mUrl;

    protected String mName;

    public DownloadableItem(String url, String name) {
        mUrl = url;
        mName = name;
    }

    public DownloadableItem(Parcel source) {
        Log.i("elvis", "createFromParcel DownloadableItem. 1");
        mUrl = source.readString();
        mName = source.readString();
        Log.i("elvis", "createFromParcel DownloadableItem. 2");
    }

    /**
     * Return remote url of this downloadable.
     * 
     * @return remote url of this downloadable
     */
    public String getUrl() {
        return mUrl;
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
     * Create a content values based on fields of this item.
     * 
     * @return the content values created
     */
    public ContentValues createContentValues() {
        ContentValues values = new ContentValues();
        values.put(BaseDownloadableColumns.URL, mUrl);
        values.put(BaseDownloadableColumns.NAME, mName);
        return values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUrl);
        dest.writeString(mName);
        RuntimeException re = new RuntimeException();
        re.fillInStackTrace();
        Log.i("elvis", "url " + mUrl + " name " + mName, re);
    }

    public static final Creator<DownloadableItem> CREATOR = new Creator<DownloadableItem>() {
        @Override
        public DownloadableItem createFromParcel(Parcel source) {
            Log.i("elvis", "createFromParcel.");
            return new DownloadableItem(source);
        }

        @Override
        public DownloadableItem[] newArray(int size) {
            Log.i("elvis", "newArray.");
            return new DownloadableItem[size];
        }
    };

    @Override
    public String toString() {
        return "DownloadableItem[url=" + mUrl + " name=" + mName + "]";
    };

    /**
     * Copy a new instance form itself.
     * 
     * @return a new instance copied
     */
    public DownloadableItem copy() {
        return new DownloadableItem(mUrl, mName);
    }
}
