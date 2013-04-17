package com.elvishew.download.library.server;

import android.net.Uri;

import com.elvishew.download.library.BaseDownloadableColumns;

public class DownloadModel {

    public static final String AUTHORITY = "me.elvishew.download";

    public static final String PARAMETER_NOTIFY = "notify";

    public static final String DB_FILE = "download.db";

    /**
     * All tables names are defined here.
     */
    public static class Tables {
        public static final String DOWNLOADED = "downloaded";
        public static final String DOWNLOADING = "downloading";
    }

    /**
     * Base columns of an download-like item.
     */
    public interface BaseDownload extends BaseDownloadableColumns {
        /**
         * The requester of download.
         * <P>Type: TEXT</P>
         */
        public static final String REQUESTER = "REQUESTER";

        /**
         * The save path of local video.
         * <P>Type: TEXT</P>
         */
        public static final String SAVE_PATH = "save_path";

        /**
         * The file length of video.
         * <P>Type: INTEGER</P>
         */
        public static final String FILE_LENGTH = "file_length";
    }

    /**
     * Downloaded videos.
     */
    public static class Downloaded implements BaseDownload {
        /**
         * The content:// style URI for all downloaded videos.
         */
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/" + Tables.DOWNLOADED);

        /**
         * The content:// style URI for all downloaded videos, with no notification when
         * downloaded videos change.
         */
        public static final Uri CONTENT_URI_NO_NOTIFICATION = Uri
                .parse("content://" + AUTHORITY + "/" + Tables.DOWNLOADED + "?"
                            + PARAMETER_NOTIFY + "=false");

        /**
         * The finish time of downloaded video.
         * <P>Type: INTEGER</P>
         */
        public static final String FINISH_TIME = "finish_time";

        /**
         * SQL to create a new downloaded table, used when the app first opened.
         */
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + Tables.DOWNLOADED +" (" + 
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
                NAME + " TEXT NOT NULL," + 
                URL + " TEXT NOT NULL," + 
                REQUESTER + " TEXT NOT NULL," + 
                SAVE_PATH + " TEXT NOT NULL," +
                FILE_LENGTH + " INTEGER NOT NULL DEFAULT 0," +
                FINISH_TIME + " INTEGER NOT NULL DEFAULT 0);";
    }

    /**
     * Downloading videos, we can pause, resume or cancel any download task
     * while downloading.
     */
    public static class Downloading implements BaseDownload {
        /**
         * The content:// style URI for all downloading videos.
         */
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/" + Tables.DOWNLOADING);

        /**
         * The content:// style URI for all downloading videos, with no notification when
         * downloading videos change.
         */
        public static final Uri CONTENT_URI_NO_NOTIFICATION = Uri
                .parse("content://" + AUTHORITY + "/" + Tables.DOWNLOADING + "?"
                            + PARAMETER_NOTIFY + "=false");

        /**
         * The start time of downloading video.
         * <P>Type: INTEGER</P>
         */
        public static final String START_TIME = "start_time";

        /**
         * The completed length of downloading video.
         * <P>Type: INTEGER</P>
         */
        public static final String COMPLETED_LENGTH = "completed_length";

        /**
         * The download state of downloading video.
         * <P>Type: INTEGER</P>
         */
        public static final String STATE = "state";

        /**
         * SQL to create a new downloading table, used when the app first opened.
         */
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + Tables.DOWNLOADING +" (" + 
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
                NAME + " TEXT NOT NULL," + 
                URL + " TEXT NOT NULL," + 
                REQUESTER + " TEXT NOT NULL," + 
                SAVE_PATH + " TEXT NOT NULL," +
                FILE_LENGTH + " INTEGER NOT NULL DEFAULT 0," +
                START_TIME + " INTEGER NOT NULL DEFAULT 0," + 
                COMPLETED_LENGTH + " INTEGER NOT NULL DEFAULT 0," + 
                STATE + " INTEGER NOT NULL DEFAULT 0);";
    }
}
