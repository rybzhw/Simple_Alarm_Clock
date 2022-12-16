package com.unique.simplealarmclock.activities;

import static com.unique.simplealarmclock.App.CHANNEL_ID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.unique.simplealarmclock.R;
import com.unique.simplealarmclock.model.Alarm;

import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;

    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        edit = sharedPreferences.edit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.dayNigthMode){
            boolean dn=sharedPreferences.getBoolean(getString(R.string.dayNightTheme),true);
            if(dn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                edit.putBoolean(getString(R.string.dayNightTheme),false).apply();
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                edit.putBoolean(getString(R.string.dayNightTheme),true).apply();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void startTest(View view) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        alarm = new Alarm(
                new Random().nextInt(Integer.MAX_VALUE),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                "Snooze",
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                RingtoneManager.getActualDefaultRingtoneUri(getBaseContext(), RingtoneManager.TYPE_ALARM).toString(),
                false
        );

        alarm.setMaxLockTime(8/(float)60);
        alarm.setMinLockTime(2/(float)60);
        alarm.setChangeInterval(20/(float)60);
        alarm.setWorkTime(60/(float)60);

        alarm.schedule(this, 8);

        String toastText = String.format("Alarm startTest");
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
    }

    public void startTest2(View view) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 8);

        alarm = new Alarm(
                new Random().nextInt(Integer.MAX_VALUE),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                "Snooze",
                true,
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                RingtoneManager.getActualDefaultRingtoneUri(getBaseContext(), RingtoneManager.TYPE_ALARM).toString(),
                false
        );

        alarm.setMaxLockTime(8/(float)60);
        alarm.setMinLockTime(2/(float)60);
        alarm.setChangeInterval(20/(float)60);
        alarm.setWorkTime(60/(float)60);

        alarm.setSecond(calendar.get(Calendar.SECOND));
        alarm.schedule(this);

        String toastText = String.format("Alarm startTest2");
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
    }

    public void showNotification(View view) {
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
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
        manager.notify(1, notification);
    }
}