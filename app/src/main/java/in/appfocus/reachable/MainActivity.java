package in.appfocus.reachable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Switch swReceiver;
    //i am removing this switch for now to make the "one touch"
    //Switch swAutoResponseSMS;

    ComponentName SMSReceiverComponent;
    PackageManager pm;
    int intReceiverStatus;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    Intent intent;

    NotificationManager notificationManager;

    public String APP_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("mytag","oncreateMain-"+this.hashCode());

        setContentView(R.layout.activity_main);

        APP_NAME = getResources().getString(R.string.app_name);

        if(!Utilities.hasAllPermissions(getApplicationContext()))
            Utilities.requestMissingPermissions(this);

        SMSReceiverComponent = new ComponentName(this, MyReceiver.class);
        pm = this.getPackageManager();

        sp=getApplication().getSharedPreferences("myPreferences",0);
        editor = sp.edit();

        notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        initSwitches();

    }

    @Override
    protected void onResume() {
        super.onResume();
        recallSwitchStates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("mytag","mainactivity-destroyed");
    }

    private void initSwitches(){
        TextView tvSwitchName = (TextView)findViewById(R.id.tvSwitchName);
        tvSwitchName.setText("Enable " + APP_NAME);
        swReceiver = (Switch)  findViewById(R.id.swReceiver);

        swReceiver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                swReceiverStatusChanged(isChecked);
            }
        });

        /*swAutoResponseSMS = (Switch)  findViewById(R.id.swAutoResponseSMS);

          swAutoResponseSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                swAutoResponseSMSStatusChanged(isChecked);
            }
        });*/
    }

    private void recallSwitchStates(){
        intReceiverStatus = pm.getComponentEnabledSetting(SMSReceiverComponent);

        if(intReceiverStatus == PackageManager.COMPONENT_ENABLED_STATE_ENABLED){
            swReceiver.setChecked(true);
        }

        else{
            swReceiver.setChecked(false);
        }

        /*Boolean isSwAutoResponseEnabled =sp.getBoolean("isSwAutoResponseEnabled",false);
        swAutoResponseSMS.setChecked(isSwAutoResponseEnabled);*/
    }

    private void swReceiverStatusChanged(Boolean status){

        if(status==true){

            enableReceiver();
            showStickyNotification();
            Utilities.putPhoneToSilentMode(this);
        }
        else{
            removeStickyNotification();
            Utilities.putPhoneToSNormalMode(this);
            disableReceiver();
        }
    }

    private void enableReceiver(){
        if(intReceiverStatus != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            pm.setComponentEnabledSetting(SMSReceiverComponent,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            intReceiverStatus=PackageManager.COMPONENT_ENABLED_STATE_ENABLED;

            String msg = APP_NAME + " is now enabled, You can close the app";

            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }

        //lets enable the switch, DO NOT on the switch! Leave it to the user to decide
        /*swAutoResponseSMS.setEnabled(true);*/
    }

    private void disableReceiver(){
        if (intReceiverStatus != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            pm.setComponentEnabledSetting(SMSReceiverComponent,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);

            intReceiverStatus=PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

            String msg = APP_NAME + " is now disabled";

            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
        //let's off auto response and disable the switch
       /* swAutoResponseSMS.setChecked(false);
        swAutoResponseSMS.setEnabled(false);*/
    }

    private void swAutoResponseSMSStatusChanged(Boolean status){
        //lets remember the switch state
        editor.putBoolean("isSwAutoResponseEnabled", status);
        editor.commit();
    }

    private void showStickyNotification() {
        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                1234, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(APP_NAME)
                .setContentText("Phone is in " + APP_NAME.toLowerCase() + " mode")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_stat_error_outline)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

        Notification notification=builder.build();

        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        notificationManager.notify(1234, notification);
    }

    private void removeStickyNotification(){
        notificationManager.cancel(1234);
    }

}
