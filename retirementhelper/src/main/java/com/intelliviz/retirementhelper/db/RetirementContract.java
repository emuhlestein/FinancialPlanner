package com.intelliviz.retirementhelper.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by edm on 3/27/2017.
 */

public class RetirementContract {
    public static final String CONTENT_AUTHORITY =
            "com.intelliviz.retirementhelper.db.MovieProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PERSONALINFO = "personalinfo";

    private RetirementContract() {}

    public static final class PeronsalInfoEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PERSONALINFO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSONALINFO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSONALINFO;

        public static final String TABLE_NAME = PATH_PERSONALINFO;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_BIRTHDATE = "birthdate";
    }
}
