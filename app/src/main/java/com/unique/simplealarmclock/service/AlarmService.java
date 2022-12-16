package com.unique.simplealarmclock.service;

import android.app.Notification;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;

import com.unique.simplealarmclock.broadcastreciever.AdminReceiver;
import com.unique.simplealarmclock.model.Alarm;
import com.unique.simplealarmclock.R;

import static com.unique.simplealarmclock.App.CHANNEL_ID;

public class AlarmService extends LifecycleService {
    private ComponentName mCN;
    private DevicePolicyManager dpm;
    private Alarm alarm;

    @Override
    public void onCreate() {
        super.onCreate();

        mCN = new ComponentName(this.getBaseContext(), AdminReceiver.class); // Receiver, not Activity!
        dpm = (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("AlarmService", "onStartCommand enter.");

        // read intent
        Bundle bundle = intent.getBundleExtra(getString(R.string.bundle_alarm_obj));
        if (bundle != null)
            alarm = (Alarm) bundle.getSerializable(getString(R.string.arg_alarm_obj));

        // show notification
        showNotification();

        // dismiss alarm
        dismissAlarm();

        // set next lock alarm
        onAlarm(intent);

        // lock now
        lockScreen();

        stopSelf();

        return START_STICKY;
    }

    private void showNotification() {
        String alarmTitle=getString(R.string.alarm_title);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Ring Ring .. Ring Ring")
                .setContentText(alarmTitle)
                .setSmallIcon(R.drawable.ic_alarm_white_24dp)
                .setSound(null)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .setFullScreenIntent(pendingIntent,true)
                .build();
        startForeground(1, notification);
    }

    private void dismissAlarm(){
        if(alarm!=null) {
            alarm.setStarted(false);
            alarm.cancelAlarm(getBaseContext());
        }
    }

    private void lockScreen(){
        Log.i("AlarmService", "lockScreen enter.");
        if (dpm.isAdminActive(mCN)) {
            Log.i("AlarmService", "lockNow.");
            dpm.lockNow();
        }
        else{
            Log.i("AlarmService", "not admin.");
            Toast.makeText(this.getBaseContext(), "not admin", Toast.LENGTH_LONG).show();
        }
        Log.i("AlarmService", "lockScreen leave.");
    }

    private void onAlarm(Intent intent){
        Log.i("AlarmService", "onAlarm enter.");
        if(alarm==null)
            return;

        int nextSecond = alarm.getNextAlarmSub();
        Log.i("AlarmService", "nextSecond " + nextSecond);
        if (nextSecond <= 0) {
            alarm.scheduleRecurring(getApplicationContext(), getApplication());
            return;
        }

        alarm.schedule(getApplicationContext(), nextSecond);

        Log.i("AlarmService", "onAlarm leave.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }
}
