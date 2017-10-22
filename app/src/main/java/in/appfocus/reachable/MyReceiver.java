package in.appfocus.reachable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import static android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION;

/**
 * Created by User on 18-10-2017.
 */

public class MyReceiver extends BroadcastReceiver {
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Context context;

    public MyReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        String strAction=intent.getAction();

        if(strAction.equals(SMS_RECEIVED_ACTION)){
            //android.provider.Telephony.SMS_RECEIVED
            smsReceived();
        }else{
            //android.intent.action.PHONE_STATE
            callReceived();
        }
    }
    
    private void smsReceived(){
        // TODO: 18-10-2017 check if sms starts with urgent, if so trigger alarm 
    }
    
    private void callReceived(){
        sp=context.getApplicationContext().getSharedPreferences("myPreferences",0);
        Boolean isSwAutoResponseEnabled = sp.getBoolean("isSwAutoResponseEnabled",false);
        if(isSwAutoResponseEnabled){
            sendSMStoCaller();
        }
    }
    
    private void sendSMStoCaller(){
        // TODO: 18-10-2017 send sms with minimal no of characters in it 
    }
    
    private void triggerAlarm(){
        // TODO: 18-10-2017 user should see main activity (with siren on), with an alert and a button to stop siren
    }
}
