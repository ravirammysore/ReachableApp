package in.appfocus.reachable;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;


import static android.content.Context.NOTIFICATION_SERVICE;
import static android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION;

/**
 * Created by User on 18-10-2017.
 */

public class MyReceiver extends BroadcastReceiver {
    SharedPreferences sp;

    Context context;
    Intent intent;

    MediaPlayer mp;

    String APP_NAME, AUTO_RESPONSE_MESSAGE;

    public MyReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        this.intent = intent;

        APP_NAME = context.getResources().getString(R.string.app_name);

        String strAction = intent.getAction();

        if (strAction.equals(SMS_RECEIVED_ACTION)) {
            smsReceived();
        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            //VVIP: we need to respond only to RINGING and not to IDLE or any other states
            if (stateStr != null && !stateStr.isEmpty() && stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                callReceived();
            }
        }
    }

    private void smsReceived() {

        SmsMessage smsMessage;
        String strSMS;
        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);

                //works fine -ravi
                smsMessage = msgs[0];
                strSMS = smsMessage.getDisplayMessageBody();

                if (strSMS.toLowerCase().contains("urgent") || strSMS.toLowerCase().contains("emergency")) {
                    triggerAlarm();
                    showNotification();
                }
            }
        } catch (Exception e) {
            Log.d("mytag", e.getMessage());
        }
    }

    private void callReceived() {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            /*sp=context.getApplicationContext().getSharedPreferences("myPreferences",0);
            Boolean isSwAutoResponseEnabled = sp.getBoolean("isSwAutoResponseEnabled",false);
            if(isSwAutoResponseEnabled){
                sendSMStoCaller(incomingNumber);
            }*/
            sendSMStoCaller(incomingNumber);
        }
    }

    private void sendSMStoCaller(String caller) {

        try {
            AUTO_RESPONSE_MESSAGE = context.getResources().getString(R.string.auto_response_message);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(caller, null, AUTO_RESPONSE_MESSAGE, null, null);
        } catch (Exception e) {
            Log.d("mytag", e.getMessage());
        }
    }

    private void triggerAlarm() {
        /*
        https://developer.android.com/guide/components/broadcasts.html

        Do not start activities from broadcast receivers because the user experience is jarring;
        especially if there is more than one receiver.
        Instead, consider displaying a notification.

        To perform long running work, we recommend:
        Calling goAsync() in your receiver's onReceive() method and passing the
        BroadcastReceiver.PendingResult to a background thread.

        This keeps the broadcast active after returning from onReceive().

        However, even with this approach the system expects you to finish with the
        broadcast very quickly (under 10 seconds)
        * */

        Log.d("mytag", "attempting to start alarm...");

        //approach 2
        //works well, but no UI to stop - not a good design, but ok for now!

        //// TODO: 24/10/17 show a notification first and then start media player,

        //Utilities.putPhoneToSNormalMode(context);
        Utilities.increaseMediaVolume(context);
        mp = MediaPlayer.create(context, R.raw.smartnap);
        mp.start();

        //approach 3

        //approach seemed good, i have also set the AlertActivity launch mode to SingleInstance
        // in the manifest and verified that it is being launched only once in spite of receiving multiple messages back to back

        //but media player won't play when locked :(

        /*Intent intent = new Intent(context,AlertActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);*/

    }

    private void showNotification() {

        NotificationManager notificationManager;
        notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(APP_NAME)
                .setContentText("You received an urgent message!")
                .setSmallIcon(R.drawable.ic_stat_error_outline)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        Notification notification = builder.build();
        notificationManager.notify(2000, notification);
    }
}