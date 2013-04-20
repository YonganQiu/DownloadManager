package com.elvishew.download.library.server;

import android.net.Uri;

class DownloadModel {

    static final String AUTHORITY = "com.elvishew.download";

    static final String PARAMETER_NOTIFY = "notify";

    static final String DB_FILE = "download.db";

    /**
     * All tables names are defined here.
     */
    static class Tables {
        static final String DOWNLOADED = "downloaded";
        static final String DOWNLOADING = "downloading";
    }

    /**
     * Base columns of an downloadable item.
     */
    interface BaseDownloadableColumns {

        /**
         * ID column.
         * <p>
         * Type: INTEGER
         * </p>
         */
        static final String _ID = "_id";

        /**
         * The name of the downloadable.
         * <P>
         * Type: TEXT
         * </P>
         */
        static final String NAME = "name";

        /**
         * The remote url of the downloadable, like
         * "http://www.qipaoxian.com/test.mp4".
         * <P>
         * Type: TEXT
         * </P>
         */
        static final String URL = "url";
    }

    /**
     * Base columns of an download-like item.
     */
    interface BaseDownload extends BaseDownloadableColumns {
        /**
         * The requester of download.
         * <P>Type: TEXT</P>
         */
        static final String REQUESTER = "REQUESTER";

        /**
         * The save path of local downloadable.
         * <P>Type: TEXT</P>
         */
        static final String SAVE_PATH = "save_path";

        /**
         * The file length of downloadable.
         * <P>Type: INTEGER</P>
         */
        static final String FILE_LENGTH = "file_length";
    }

    /**
     * Downloaded downloadables.
     */
    static class Downloaded implements BaseDownload {
        /**
         * The content:// style URI for all downloaded downloadables.
         */
        static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/" + Tables.DOWNLOADED);

        /**
         * The content:// style URI for all downloaded downloadables, with no notification when
         * downloaded downloadables change.
         */
        static final Uri CONTENT_URI_NO_NOTIFICATION = Uri
                .parse("content://" + AUTHORITY + "/" + Tables.DOWNLOADED + "?"
                            + PARAMETER_NOTIFY + "=false");

        /**
         * The finish time of downloaded downloadable.
         * <P>Type: INTEGER</P>
         */
        static final String FINISH_TIME = "finish_time";

        /**
         * SQL to create a new downloaded table, used when the app first opened.
         */
        static final String SQL_CREATE_TABLE = "CREATE TABLE " + Tables.DOWNLOADED +" (" + 
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
                NAME + " TEXT NOT NULL," + 
                URL + " TEXT NOT NULL," + 
                REQUESTER + " TEXT NOT NULL," + 
                SAVE_PATH + " TEXT NOT NULL," +
                FILE_LENGTH + " INTEGER NOT NULL DEFAULT 0," +
                FINISH_TIME + " INTEGER NOT NULL DEFAULT 0);";
    }

    /**
     * Downloading downloadables, we can pause, resume or cancel any download task
     * while downloading.
     */
    static class Downloading implements BaseDownload {
        /**
         * The content:// style URI for all downloading downloadables.
         */
        static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/" + Tables.DOWNLOADING);

        /**
         * The content:// style URI for all downloading downloadables, with no notification when
         * downloading downloadables change.
         */
        static final Uri CONTENT_URI_NO_NOTIFICATION = Uri
                .parse("content://" + AUTHORITY + "/" + Tables.DOWNLOADING + "?"
                            + PARAMETER_NOTIFY + "=false");

        /**
         * The start time of downloading downloadable.
         * <P>Type: INTEGER</P>
         */
        static final String START_TIME = "start_time";

        /**
         * The completed length of downloading downloadable.
         * <P>Type: INTEGER</P>
         */
        static final String COMPLETED_LENGTH = "completed_length";

        /**
         * The download state of downloading downloadable.
         * <P>Type: INTEGER</P>
         */
        static final String STATE = "state";

        /**
         * SQL to create a new downloading table, used when the app first opened.
         */
        static final String SQL_CREATE_TABLE = "CREATE TABLE " + Tables.DOWNLOADING +" (" + 
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
