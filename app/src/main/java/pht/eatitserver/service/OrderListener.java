package pht.eatitserver.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import pht.eatitserver.OrderList;
import pht.eatitserver.R;
import pht.eatitserver.model.Request;

public class OrderListener extends Service implements ChildEventListener {

    FirebaseDatabase database;
    DatabaseReference request;

    public OrderListener() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        database = FirebaseDatabase.getInstance();
        request = database.getReference("Request");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        request.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Request child = dataSnapshot.getValue(Request.class);

        if(child.getStatus().equals("0")){
            showNotification(dataSnapshot.getKey(), child);
        }
    }

    private void showNotification(String key, Request child) {
        Intent intent = new Intent(getBaseContext(), OrderList.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker("Hoàng Tâm")
                .setContentInfo("New Order")
                .setContentText("You have new order " + key)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // To show many notification, make ID for each notification
        int randomID = new Random().nextInt(9999-1) + 1;
        manager.notify(randomID, builder.build());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
