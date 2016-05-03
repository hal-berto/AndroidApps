package librorisposte.android.app.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Random;

/**
 * Created by hal-berto on 03/05/2016.
 */
public class ResponseDAO {

    DataBaseHelper dbHelper = null;
    Context context = null;

    public ResponseDAO(Context context){
        this.context = context;
    }

    public String getRandomResponse(){
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getReadableDatabase();

        String selectQuery = "SELECT COUNT(*)" +
                " FROM " + LibroRisposteContract.Response.TABLE_NAME;
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();
        int rowCount= c.getInt(0);
        c.close();

        int randomId = 0;

        Random rn = new Random();
        int randomNum =  rn.nextInt(rowCount);
        if(c.moveToPosition(randomNum)){
            randomId = c.getInt(0);
        }
        return getResponseById(randomId);
    }

    public String getResponseById(int id){
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getReadableDatabase();

        String response = "";
        String selectQuery = "SELECT " + LibroRisposteContract.Response.COLUMN_NAME_RESPONSE_TEXT +
                " FROM " + LibroRisposteContract.Response.TABLE_NAME +
                " WHERE " + LibroRisposteContract.Response._ID + "=?";
        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(id) });
        if (c.moveToFirst()) {
            response = c.getString(c.getColumnIndex(LibroRisposteContract.Response.COLUMN_NAME_RESPONSE_TEXT));
        }
        c.close();
        return response;
    }

}
