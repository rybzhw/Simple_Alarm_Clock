package com.unique.simplealarmclock.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProviders;

import com.unique.simplealarmclock.broadcastreciever.AdminReceiver;
import com.unique.simplealarmclock.model.Alarm;
import com.unique.simplealarmclock.R;
import com.unique.simplealarmclock.activities.RingActivity;
import com.unique.simplealarmclock.viewmodel.AlarmListViewModel;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import static com.unique.simplealarmclock.App.CHANNEL_ID;

public class AlarmService extends Service {
    private ComponentName mCN;
    private DevicePolicyManager dpm;
    private Alarm alarm;
    //private AlarmListViewModel alarmsListViewModel;

    @Override
    public void onCreate() {
        super.onCreate();

        //alarmsListViewModel = ViewModelProviders.of(this.getBaseContext()).get(AlarmListViewModel.class);

        mCN = new ComponentName(this.getBaseContext(), AdminReceiver.class); // Receiver, not Activity!
        dpm = (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("AlarmService", "onStartCommand enter.");

        // read intent
        Bundle bundle=intent.getBundleExtra(getString(R.string.bundle_alarm_obj));
        if (bundle!=null)
            alarm =(Alarm)bundle.getSerializable(getString(R.string.arg_alarm_obj));

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
            //alarmsListViewModel.update(alarm);
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
        if (nextSecond <= 0)
            return;

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
        return null;
    }
}
