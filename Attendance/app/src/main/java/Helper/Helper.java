package Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.util.Log;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import att.attendanceapp.R;

/**
 * Created by rujoota on 08-10-2015.
 */
public class Helper
{
    static String sharedPrefFileName="userInfo";
    static String TAG="ATTENDANCE HELPER";
    public static String convertDateToFormat(String date,String format)
    {

        Date dt=null;
        SimpleDateFormat sdf=null;
        try
        {
            sdf = new SimpleDateFormat(format, Locale.US);
            dt=new Date(date);
        }
        catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
        return sdf.format(dt);
    }
    public static String convertDateToFormat(Date date,String format)
    {

        SimpleDateFormat sdf=null;
        try
        {
            sdf = new SimpleDateFormat(format, Locale.US);

        }
        catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
        return sdf.format(date);
    }
    public static String convertDateFromSQLToUS(String date)
    {
        String formattedDate="";
        try
        {
            DateFormat originalFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("mm/dd/yyyy");
            Date dt = originalFormat.parse(date);
            formattedDate = targetFormat.format(dt);
        }
        catch (Exception ex){}
        return formattedDate;
    }
    public static String convertDateFromUSToSQL(String date)
    {
        String formattedDate="";
        try
        {
            DateFormat originalFormat = new SimpleDateFormat("mm/dd/yyyy", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("yyyy-mm-dd");
            Date dt = originalFormat.parse(date);
            formattedDate = targetFormat.format(dt);
        }
        catch (Exception ex){}
        return formattedDate;
    }
    public static Boolean isUserLoggedIn(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
        String isUserLoggedIn = sharedPreferences.getString(context.getString(R.string.isLoggedIn_sharedPref_string), "").toLowerCase();
        if(isUserLoggedIn.equals("yes"))
            return true;
        else
            return false;
    }
    public static void removeSharedPref(Context context,String key)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }
    public static String getSharedPref(Context context,String key)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "");
        return value;
    }
    public static String getCurrentLoggedinUser(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
        String user = sharedPreferences.getString(context.getString(R.string.loggedInUser_sharedPref_string), "");
        return user;
    }
    public static void putSharedPref(Context context,String key,String value)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
        String isUserLoggedIn = sharedPreferences.getString(key, "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
