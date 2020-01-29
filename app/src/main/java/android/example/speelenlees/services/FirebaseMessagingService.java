package android.example.speelenlees.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.example.speelenlees.R;
import android.example.speelenlees.activities.MainActivity;
import android.example.speelenlees.activities.ui.homepages.HomeActivity;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    /*@Override // Deze methode wordt opgeroepen wanneer een bericht van Firebase ontvangen wordt
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        showNotificiation(remoteMessage.getData().get("message"));
    }

    private void showNotificiation(String message) {
        Intent intentToMainActivity = new Intent(this, HomeActivity.class);
        intentToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intentToMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("FCM Test")
                .setContentText(message)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }*/
}
