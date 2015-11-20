package Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import att.attendanceapp.R;

/**
 * Created by rujoota on 08-10-2015.
 */
public class HelperMethods
{
    static String sharedPrefFileName="userInfo";
    public static String TAG="ATTENDANCE";

    public static String getResponse(String serviceUrl,String[] keys, String[] values)
    {
        String response = "";
        InputStream is = null;
        try
        {
            URL url = new URL(serviceUrl);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setDoOutput(true);
            OutputStream outputStream = httpUrlConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String data="";
            for(int i=0;i<keys.length;i++)
            {
                data += URLEncoder.encode(keys[i], "UTF-8")+"=";
                data += URLEncoder.encode(values[i], "UTF-8") + "&";
            }
            data=data.substring(0,data.length()-1);
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            is = httpUrlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                response += line;
            }
            bufferedReader.close();
            is.close();
            httpUrlConnection.disconnect();

        }
        catch (Exception ex)
        {
            response="Exception:"+ex.getMessage();
            Log.e(TAG,ex.getMessage());
        }
        return response;
    }
    public static int generateRandom(int min,int max)
    {
        Random random = new Random();
        int randomInt = random.nextInt((max - min) + 1) + min;
        if (randomInt > max)
        {
            randomInt -= min;
        }
        return randomInt;
    }
    public static String convertToStandardTime(String time)
    {
        String arr[]=time.split(":");
        return arr[0]+":"+arr[1];
    }
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
    public static boolean isEmpty(EditText target)
    {
        String txt=target.getText().toString().trim();
        return TextUtils.isEmpty(txt);
    }
    public static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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
    public static void removeAllSharedPref(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
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
    public static String getCurrentLoggedinUserType(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
        String user = sharedPreferences.getString(context.getString(R.string.userType_sharedPref_string), "");
        return user;
    }
    public static void signout(Context context)
    {
        removeAllSharedPref(context);
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
