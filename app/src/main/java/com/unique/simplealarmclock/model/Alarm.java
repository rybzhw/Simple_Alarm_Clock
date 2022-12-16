package com.unique.simplealarmclock.model;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.unique.simplealarmclock.data.AlarmRepository;
import com.unique.simplealarmclock.util.DayUtil;
import com.unique.simplealarmclock.R;
import com.unique.simplealarmclock.broadcastreciever.AlarmBroadcastReceiver;

import java.io.Serializable;
import java.util.Calendar;

@Entity(tableName = "alarm_table")
public class Alarm implements Serializable {
    @PrimaryKey
    @NonNull
    private int alarmId;
    private int hour, minute, second;
    private boolean started, recurring;
    private boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private String title;
    private String tone;
    private boolean vibrate;
    private long millisecond;
    private float maxLockTime, minLockTime, changeInterval, workTime;

    public Alarm(int alarmId, int hour, int minute, String title, boolean started, boolean recurring, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday, String tone,boolean vibrate) {
        this.alarmId = alarmId;
        this.hour = hour;
        this.minute = minute;
        this.started = started;
        this.recurring = recurring;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.title = title;
        this.vibrate=vibrate;
        this.tone=tone;
        this.second = 0;
        this.millisecond = 0;
    }
    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public boolean isMonday() {
        return monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public long getMillisecond() {
        return millisecond;
    }

    public void setMillisecond(long millisecond) {
        this.millisecond = millisecond;
    }

    public float getMaxLockTime() {
        return maxLockTime;
    }
    public void setMaxLockTime(float maxLockTime) {
        this.maxLockTime = maxLockTime;
    }

    public float getMinLockTime() {
        return minLockTime;
    }
    public void setMinLockTime(float minLockTime) {
        this.minLockTime = minLockTime;
    }

    public float getChangeInterval() {
        return changeInterval;
    }
    public void setChangeInterval(float changeInterval) {
        this.changeInterval = changeInterval;
    }

    public float getWorkTime() {
        return workTime;
    }
    public void setWorkTime(float workTime) {
        this.workTime = workTime;
    }

    private void schedule(Context context, Calendar calendar) {
        Log.i("Alarm", "schedule calendar " + calendar.getTime());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable(context.getString(R.string.arg_alarm_obj),this);
        intent.putExtra(context.getString(R.string.bundle_alarm_obj),bundle);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, FLAG_UPDATE_CURRENT);

        String toastText = null;
        try {
            toastText = String.format("One Time Alarm %s scheduled for %s at %02d:%02d", title, DayUtil.toDay(calendar.get(Calendar.DAY_OF_WEEK)), hour, minute);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                alarmPendingIntent
        );
        Log.i("Alarm", "schedule setExact " + calendar.getTimeInMillis());

        this.started = true;
    }

    public void schedule(Context context, int second) {
        Log.i("Alarm", "schedule second " + second);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, second);

        schedule(context, calendar);
    }

    public void schedule(Context context) {
        Log.i("Alarm", "schedule");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, getSecond());
        calendar.set(Calendar.MILLISECOND, 0);

        long deltaSecond = (calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000;
        Log.i("Alarm", "schedule deltaSecond " + deltaSecond);
        // if alarm time has already passed, increment day by 1
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            Log.i("Alarm", "schedule time has already passed.");
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
//            calendar.setTimeInMillis(System.currentTimeMillis());
//            calendar.add(Calendar.SECOND, 5);
        }

        setMillisecond(calendar.getTimeInMillis());
        Log.i("Alarm", "schedule put " + getMillisecond() + " Id " + alarmId);

        schedule(context, calendar);
    }

    public void scheduleRecurring(Context context, Application application) {
        Log.i("Alarm", "scheduleRecurring");

        schedule(context);

        AlarmRepository alarmRepository = new AlarmRepository(application);
        alarmRepository.update(this);
        Log.i("Alarm", "scheduleRecurring " + getMillisecond() + " Id " + getAlarmId());
    }

    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.started = false;
        String toastText = String.format("Alarm cancelled for %02d:%02d", hour, minute);
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        Log.i("cancel", toastText);
    }

    public String getRecurringDaysText() {
        if (!recurring) {
            return null;
        }

        String days = "";
        if (monday) {
            days += "Mo ";
        }
        if (tuesday) {
            days += "Tu ";
        }
        if (wednesday) {
            days += "We ";
        }
        if (thursday) {
            days += "Th ";
        }
        if (friday) {
            days += "Fr ";
        }
        if (saturday) {
            days += "Sa ";
        }
        if (sunday) {
            days += "Su ";
        }

        return days;
    }

    public String getTitle() {
        return title;
    }

    public int getNextAlarmSub() {
        // delta seconds
        int baseSecond = (int)(maxLockTime * 60);
        int minSecond = (int)(minLockTime * 60);
        int changeIntervalSecond = (int)(changeInterval * 60);
        int workSecond = (int)(workTime * 60);

        // debug data
        /*baseSecond = 8;
        minSecond = 3;
        changeInterval = 25;
        workSecond = 60;*/

        // test data
        /*baseSecond = 60;
        minSecond = 20;
        changeInterval = 20;
        workSecond = 3 * 60;*/

        long deltaSecond = (System.currentTimeMillis() - getMillisecond()) / 1000;
        Log.i("Alarm", "getNextAlarmSub " + System.currentTimeMillis() + ":" +  getMillisecond() + ":" + deltaSecond);
        if (deltaSecond >= workSecond)
            return -1;

        int deltaScale = Math.max(1, (int)(deltaSecond / changeIntervalSecond) + 1);
        int newSecond = (int)(baseSecond / deltaScale);

        return Math.max(minSecond, newSecond);
    }
}
