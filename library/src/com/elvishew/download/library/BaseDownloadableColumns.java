
package com.elvishew.download.library;

/**
 * Base columns of an downloadable item.
 */
public interface BaseDownloadableColumns {

    /**
     * ID column.
     * <p>
     * Type: INTEGER
     * </p>
     */
    public static final String _ID = "_id";

    /**
     * The name of the video.
     * <P>
     * Type: TEXT
     * </P>
     */
    public static final String NAME = "name";

    /**
     * The remote url of the video, like "http://www.qipaoxian.com/test.mp4".
     * <P>
     * Type: TEXT
     * </P>
     */
    public static final String URL = "url";
}
