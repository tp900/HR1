package com.qr.hr.servers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.qr.hr.MainActivity;
import com.qr.hr.R;
import com.qr.hr.modles.Messages;
import com.qr.hr.modles.Notif;
import com.qr.hr.utils.HttpUtil;
import com.qr.hr.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotifService extends Service {
    public NotifService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("2", "onStartCommand: ");
        GetNotif();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int hours = 60000;//每分钟
        long triggerAtTime = SystemClock.elapsedRealtime()+hours;
        Intent intent1 = new Intent(this,NotifService.class);
        PendingIntent pendingIntent= PendingIntent.getService(this,0,intent1,0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }
    private void GetNotif(){
        String url = getResources().getString(R.string.url)+"GetNotification";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String empno = preferences.getString("emp_no","");
        if(empno ==""){
            return;
        }
        RequestBody body = new FormBody.Builder()
                .add("empno",empno).build();
        HttpUtil.SendOkHttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String sResponse = response.body().string();
                try{
                    JSONArray jsonArray = new JSONArray(sResponse);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String mContent = jsonObject.toString();
                    Messages messages = new Gson().fromJson(mContent,Messages.class);
                    if(messages.status ==1){
                        JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                        if(jsonArray1!=null&&jsonArray1.length()>0){
                            for(int i = 0 ;i<jsonArray1.length();i++){
                                JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                String nContent = jsonObject1.toString();
                                Notif notif = new Gson().fromJson(nContent,Notif.class);
                                if(null!=notif&&notif.content!=""){
                                    ShowNotification(notif.content);
                                }
                            }
                        }
                    }
                }catch (Exception ex){
                }
            }
        });
    }
    private void ShowNotification(String content){
        //Log.d("ss", "ShowNotification: ");
        String cId = "cId";
        String cName = "cName";
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,intent,0);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(cId,cName,NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
            notification = new Notification.Builder(getBaseContext(),cId)
                    .setChannelId(cId)
                    .setContentTitle(content)
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo48)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo96))
                    .setContentIntent(pendingIntent)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .build();
        }else{
            notification = new NotificationCompat.Builder(this,cId)
                    .setContentTitle(content)
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo48)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo96))
                    .setContentIntent(pendingIntent)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .build();
        }
        notificationManager.notify(1,notification);
    }

}
