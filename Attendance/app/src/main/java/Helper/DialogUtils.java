package Helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import att.attendanceapp.R;

/**
 * Created by rujoota on 19-11-2015.
 */
public abstract class DialogUtils {
    static AlertDialog dialog;
    public static void displayErrorDialog(Context context, String title, String message) {
        dialog=new AlertDialog.Builder(context)
                .setTitle(title).setIcon(R.drawable.icon_error)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                })
                .show();
    }

    public static void displayInfoDialog(Context context, String title, String message) {
        dialog=new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                })
                .show();
    }
    public static void cancelDialog()
    {
        dialog.dismiss();
    }
}
