package Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by rujoota on 21-10-2015.
 */
public class FileUtils
{
    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = "AttendanceHelper";
    static public void showFileChooser(Context context,Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try
        {
            activity.startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        }
        catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(context, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }
    public static String readFile(String path,String allowedExtn)
    {
        String fileContent="";
        try
        {
            File file = new File(path);
            if(allowedExtn.equals(MimeTypeMap.getFileExtensionFromUrl(path)))
            {
                InputStream is = new FileInputStream(file);
                Reader reader = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    fileContent += line + "\n"; //coz each attendee is on separate line
                }
                reader.close();
                is.close();
            }
            else
            {
                fileContent="Wrong extension";
            }
        }
        catch(Exception ex)
        {
            Log.e(TAG,ex.getMessage());
            fileContent="Exception:"+ex.toString();
        }
        return fileContent;
    }
    public static String getPath(Context context, Uri uri) throws URISyntaxException
    {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
