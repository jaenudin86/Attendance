package DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rujoota on 01-10-2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final int    DATABASE_VERSION = 1;

    private static final String DATABASE_NAME    = "AttendanceData";

    private static final String TABLE_HOLIDAYS   = "holidays";

    private static final String KEY_HOLIDAYNAME    = "holiday_name";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TAGS_TABLE = "CREATE TABLE " + TABLE_HOLIDAYS + "(" + KEY_HOLIDAYNAME
                + " TEXT NOT NULL)";
        db.execSQL(CREATE_TAGS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOLIDAYS);
        onCreate(db);
    }
    public void addHoliday(Holiday holiday) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_HOLIDAYNAME, holiday.getHolidayName());
        db.insert(TABLE_HOLIDAYS, null, values);
        db.close();
    }
}
