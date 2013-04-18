package com.elvishew.download.library.server;

import android.os.Parcel;
import android.os.Parcelable;

public class DownloadRequest implements Parcelable{

    DownloadableItem downloadable;
    String requester;
    String savePath;

    public DownloadRequest(DownloadableItem downloadable, String requester, String savePath) {
        this.downloadable = downloadable;
        this.requester = requester;
        this.savePath = savePath;
    }

    public DownloadRequest(Parcel source) {
        this.downloadable = source.readParcelable(null);
        this.requester = source.readString();
        this.savePath = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(downloadable, flags);
        dest.writeString(requester);
        dest.writeString(savePath);
    }

    public static final Creator<DownloadRequest> CREATOR = new Creator<DownloadRequest>() {
        @Override
        public DownloadRequest createFromParcel(Parcel source) {
            return new DownloadRequest(source);
        }

        @Override
        public DownloadRequest[] newArray(int size) {
            return new DownloadRequest[size];
        }
    };

}

