package com.mvcoelacanth.hntfapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mvcoelacanth.hntfapp.MainActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public abstract class DownloadFile extends AsyncTask<Void, Integer, String> {
    private NotificationCompat.Builder mBuilder;
    private String urlstr, fileName;
    private Context context;
    private String fl_name;
    private File myDir;

    private NotificationManager mNotifyManager;
    public int Cprogress = 0;


    public DownloadFile(Context context, String url, String fileName, File myDir) {
        this.urlstr = url;
        this.fileName = fileName;

        this.context = context;
        this.myDir = myDir;

        myDir.mkdir();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        onStart();
        initNotification();
        setStartedNotification();
        Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected String doInBackground(Void... Url) {
        try {

            URL url = new URL(urlstr);
            URLConnection connection = url.openConnection();
            connection.connect();
            int fileLength = connection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream());
            File file = new File(myDir, fileName);
            fl_name = fileName;
            if (file.exists()) file.delete();
            OutputStream output = new FileOutputStream(file);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            return "f";
        }
        return "";
    }

    private void initNotification() {
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
            mNotifyManager.createNotificationChannel(notificationChannel);

            mBuilder = new NotificationCompat.Builder(context, notificationChannel.getId());
            mBuilder.setOnlyAlertOnce(true);
        } else {
            mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setSound(null);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        int incr = progress[0].intValue();
        Cprogress = incr;
        if (incr == 0)
            setProgressNotification();
        updateProgressNotification(incr);
    }

    @Override
    protected void onPostExecute(String str) {
        super.onPostExecute(str);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (str.equals("f")) {
            final Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            mBuilder.setSmallIcon(R.drawable.hntflogo).setContentTitle(fileName).setContentText("Failed to download");
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            mBuilder.setAutoCancel(true);
            mBuilder.setWhen(0);
            mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
            mNotifyManager.notify(10, mBuilder.build());
            onDownloadFailed();

        } else {
            onDownloadSuccess();
            setCompletedNotification();

        }

        onFinish();
    }

    protected abstract void onFinish();
    protected abstract void onDownloadSuccess();
    protected abstract void onDownloadFailed();
    protected abstract void onStart();

    private void setCompletedNotification() {
        mBuilder.setSmallIcon(R.drawable.hntflogo).setContentTitle(fileName).setContentText("Completed");

        Intent target = new Intent(Intent.ACTION_QUICK_VIEW);
        String inte = "";

        File file = new File(myDir, fl_name);
        target.setDataAndType(Uri.fromFile(file), inte);
        Intent intent = Intent.createChooser(target, "Open File");


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        Intent intent2 = new Intent(context, MainActivity.class);
        intent2.setAction(Intent.ACTION_MAIN);
        intent2.addCategory(Intent.CATEGORY_LAUNCHER);
        stackBuilder.addNextIntent(intent2);
        stackBuilder.addNextIntent(intent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);

        mBuilder.setWhen(0);
        mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        mNotifyManager.notify(10, mBuilder.build());
    }

    private void setProgressNotification() {
        mBuilder.setContentTitle(fileName).setContentText("Downloading...").setSmallIcon(R.drawable.hntflogo);
    }

    private void setStartedNotification() {
        mBuilder.setSmallIcon(R.drawable.hntflogo).setContentTitle(fileName).setContentText("Connecting....");
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        mBuilder.setWhen(0);
        mBuilder.build();
        mNotifyManager.notify(10, mBuilder.build());
    }


    private void updateProgressNotification(int incr) {
        mBuilder.setProgress(100, incr, false);
        mBuilder.setWhen(0);

        Cprogress = incr;
        mNotifyManager.notify(10, mBuilder.build());

    }

}
