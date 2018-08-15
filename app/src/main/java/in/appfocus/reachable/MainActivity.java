package in.appfocus.reachable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    Switch swReceiver;
    //i am removing this switch for now to make the app "one touch"
    //Switch swAutoResponseSMS;

    ComponentName SMSReceiverComponent;
    PackageManager pm;
    int intReceiverStatus;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    NotificationManager notificationManager;

    public String APP_NAME;
    //negligible change again again
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        Log.d("mytag","oncreateMain-"+this.hashCode());

        setContentView(R.layout.activity_main);

        APP_NAME = getResources().getString(R.string.app_name);

        //ask permissions
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //if user did not grant atleast one permission, close the app
        if(!Utilities.hasAllPermissions(this))
            showErrorMessageAndCloseApp();
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

        try{
            if(status==true){
            //new approach starting from android 7.0
            requestDNDAndThenDoOtherStuff();
            }
            else{
            removeStickyNotification();
            //no need of any permissions, since it would already be done while enabling
            Utilities.putPhoneToSNormalMode(this);

            disableReceiver();
            }
        }
        catch (Exception ex){
            Toast.makeText(this, "Oops, There was an error!", Toast.LENGTH_SHORT).show();

            //lets try and restore things back!
            disableReceiver();
            removeStickyNotification();
            Utilities.putPhoneToSNormalMode(this);
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

    private void requestDNDAndThenDoOtherStuff() {
        //no need of 'isNotificationPolicyAccessGranted' check
        if( Build.VERSION.SDK_INT <=23 ) {
            enableReceiver();
            showStickyNotification();
            Utilities.putPhoneToSilentMode(this);

        }
        else{
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if ( notificationManager.isNotificationPolicyAccessGranted()) {
                enableReceiver();
                showStickyNotification();
                Utilities.putPhoneToSilentMode(this);
            } else{
                // Ask the user to grant access
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Grant Permission");
                alert.setMessage("Starting from Android 7.0, Users need to grant DND permission. " +
                        "Without this the app will not work. Click OK to continue to settings " +
                        "and grant permission for "+APP_NAME);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivityForResult( intent, 9999 );
                        //once we get the result, we will again call this function!
                    }
                });

                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showErrorMessageAndCloseApp();
                    }
                });

                alert.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9999) {
            this.requestDNDAndThenDoOtherStuff();
        }
    }

    private void showErrorMessageAndCloseApp(){
        Toast.makeText(this, "Error:Permission Denied", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void tvInstructionsClicked(View v){
        //startActivity(new Intent(this,AlertActivity.class));
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(APP_NAME);
        alert.setMessage("Once enabled, It puts your phone to silent mode. When you receive a call, SmartSilent automatically sends an SMS to the caller saying \"My phone is in silent mode. If really urgent, alert me by sending an SMS with the word urgent in it\"" +
                "\n\n" +
                "In case any one sends an SMS to you with the word urgent in it, Your phone will alert you with an audible tone for a few times.");

        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();

    }
}
