package in.appfocus.reachable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by User on 18-10-2017.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: 18-10-2017 differentiate between sms received call received events and call appr function
    }
    
    private void smsReceived(){
        // TODO: 18-10-2017 check if sms starts with urgent, if so trigger alarm 
    }
    
    private void callReceived(){
        // TODO: 18-10-2017 if auto response is enabled, send sms to the caller 
    }
    
    private void sendSMStoCaller(){
        // TODO: 18-10-2017 send sms with minimal no of characters in it 
    }
    
    private void triggerAlarm(){
        // TODO: 18-10-2017 user should see main activity (with siren on), with an alert and a button to stop siren
    }
}
