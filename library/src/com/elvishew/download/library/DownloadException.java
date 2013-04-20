
package com.elvishew.download.library;

public class DownloadException extends Exception {

    private static final long serialVersionUID = 1L;

    public int errorCode;

    public DownloadException(int errorCode) {
        this.errorCode = errorCode;
    }

    public String errorString() {
        return String.valueOf(errorCode);
    }
}
