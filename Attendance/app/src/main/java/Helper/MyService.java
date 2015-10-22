package Helper;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by rujoota on 21-10-2015.
 */
public class MyService extends IntentService
{
    public MyService() {
        super("MyServiceName");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Toast.makeText(this,"Hello from service",Toast.LENGTH_LONG).show();
    }
}
