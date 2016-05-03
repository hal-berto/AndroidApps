package librorisposte.android.app.database;

import android.provider.BaseColumns;

/**
 * Created by hal-berto on 29/04/2016.
 */
public final class LibroRisposteContract {

    public LibroRisposteContract(){}

    public static abstract class Response implements BaseColumns {
        public static final String TABLE_NAME = "RESPONSE";
        public static final String COLUMN_NAME_RESPONSE_TEXT = "RESPONSE_TEXT";
        public static final String COLUMN_NAME_IS_DEFAULT = "IS_DEFAULT";
    }
}
