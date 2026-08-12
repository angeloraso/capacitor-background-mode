package ar.com.anura.plugins.backgroundmode;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE;
import static android.os.PowerManager.PARTIAL_WAKE_LOCK;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import java.lang.ref.WeakReference;

/**
 * Puts the service in a foreground state, where the system considers it to be
 * something the user is actively aware of and thus not a candidate for killing
 * when low on memory.
 */
public class BackgroundModeService extends Service {

    private final String TAG = "BackgroundModeService";

    // Fixed ID for the 'foreground' notification
    public static final int NOTIFICATION_ID = -574543954;

    private static final String CHANNEL_ID = "anuradev-capacitor-background-mode-id-v3";
    private static final String SILENT_CHANNEL_ID = CHANNEL_ID + "-silent";
    private static volatile WeakReference<BackgroundModeService> runningService = new WeakReference<>(null);

    // Partial wake lock to prevent the app from going to sleep when locked
    private PowerManager.WakeLock mWakeLock;

    public BackgroundModeService() {}

    /**
     * Allow clients to call on to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Put the service in a foreground state to prevent app from being killed
     * by the OS.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        runningService = new WeakReference<>(this);
    }

    /**
     * No need to run headless on destroy.
     */
    @Override
    public void onDestroy() {
        sleepWell();
        runningService.clear();
        super.onDestroy();
    }

    /**
     * Redeliver the settings if Android has to recreate the service after killing its process.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BackgroundModeSettings settings = intent == null ? null : (BackgroundModeSettings) intent.getSerializableExtra("settings");

        if (settings == null) {
            settings = new BackgroundModeSettings.Builder().build();
        }

        keepAwake(settings);

        return START_REDELIVER_INTENT;
    }

    /**
     * Put the service in a foreground state to prevent app from being killed
     * by the OS.
     */
    @SuppressLint("WakelockTimeout")
    private void keepAwake(final BackgroundModeSettings settings) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                createNotification(settings),
                FOREGROUND_SERVICE_TYPE_SPECIAL_USE | FOREGROUND_SERVICE_TYPE_MICROPHONE
            );
        } else {
            startForeground(NOTIFICATION_ID, createNotification(settings));
        }

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (mWakeLock == null || !mWakeLock.isHeld()) {
            mWakeLock = pm.newWakeLock(PARTIAL_WAKE_LOCK, TAG + ":wakelock");
            mWakeLock.setReferenceCounted(false);
            mWakeLock.acquire();
        }
    }

    /**
     * Stop background mode.
     */
    private void sleepWell() {
        stopForeground(true);
        getNotificationManager().cancel(NOTIFICATION_ID);

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mWakeLock = null;
    }

    /**
     * Create a notification as the visible part to be able to put the service
     * in a foreground state.
     *
     * @param settings The config settings
     */
    private Notification createNotification(BackgroundModeSettings settings) {
        // use channelId for Oreo and higher
        String OLD_CHANNEL_ID = "anuradev-capacitor-background-mode-id";
        String channelId = settings.getSilent() ? SILENT_CHANNEL_ID : CHANNEL_ID;
        // The user-visible name of the channel.
        NotificationChannel mChannel = getNotificationChannel(settings, channelId);

        NotificationManager notificationManager = getNotificationManager();
        notificationManager.deleteNotificationChannel(OLD_CHANNEL_ID);
        notificationManager.createNotificationChannel(mChannel);
        String title = settings.getTitle();
        String text = settings.getText();
        boolean bigText = settings.getBigText();
        String subText = settings.getSubText();
        boolean showWhen = settings.getShowWhen();
        Visibility visibility = settings.getVisibility();

        Context context = getApplicationContext();
        String pkgName = context.getPackageName();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);

        String iconName = settings.getIcon();
        int smallIcon = getIconResId(iconName);
        if (smallIcon == 0) {
            // If no icon at all was found, fall back to the app's icon
            smallIcon = context.getApplicationInfo().icon;
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setOngoing(true)
            .setSmallIcon(smallIcon)
            .setShowWhen(showWhen)
            .setSilent(settings.getSilent());

        if (!subText.isEmpty()) {
            notification.setSubText(subText);
        }

        boolean allowClose = settings.getAllowClose();
        if (allowClose) {
            final Intent closeAppIntent = new Intent(context, BackgroundModeCloseReceiver.class);
            final PendingIntent closeIntent = PendingIntent.getBroadcast(context, 1337, closeAppIntent, PendingIntent.FLAG_IMMUTABLE);
            final String closeIconName = settings.getCloseIcon();
            final String closeTitle = settings.getCloseTitle();
            NotificationCompat.Action.Builder closeAction = new NotificationCompat.Action.Builder(
                getIconResId(closeIconName),
                closeTitle,
                closeIntent
            );
            notification.addAction(closeAction.build());
        }

        boolean hidden = settings.getHidden();
        if (hidden) {
            notification.setPriority(NotificationCompat.PRIORITY_MIN);
        }

        if (bigText || text.contains("\n")) {
            notification.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
        }

        notification.setVisibility(getVisibility(visibility));

        String hexColor = settings.getColor();
        setColor(notification, hexColor);

        boolean resume = settings.getResume();
        if (intent != null && resume) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_IMMUTABLE);

            notification.setContentIntent(contentIntent);
        }

        // Android 12
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notification.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);
        }

        return notification.build();
    }

    @NonNull
    private static NotificationChannel getNotificationChannel(BackgroundModeSettings settings, String CHANNEL_ID) {
        CharSequence name = settings.getChannelName();
        // The user-visible description of the channel.
        String description = settings.getChannelDescription();

        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = null;
        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        // Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.setShowBadge(false);
        if (settings.getSilent()) {
            mChannel.setSound(null, null);
            mChannel.enableVibration(false);
        }
        return mChannel;
    }

    /**
     * Update the notification.
     *
     * @param settings The config settings
     */
    private void updateNotification(BackgroundModeSettings settings) {
        getNotificationManager().notify(NOTIFICATION_ID, createNotification(settings));
    }

    static boolean isRunning() {
        return runningService.get() != null;
    }

    static boolean updateRunningNotification(BackgroundModeSettings settings) {
        BackgroundModeService service = runningService.get();
        if (service == null) {
            return false;
        }
        service.updateNotification(settings);
        return true;
    }

    /**
     * Retrieves the resource ID of the sent icon name
     *
     * @param name Name of the resource to return
     */
    private int getIconResId(String name) {
        int resId = getIconResId(name, "mipmap");

        if (resId == 0) {
            resId = getIconResId(name, "drawable");
        }

        if (resId == 0) {
            resId = getIconResId("icon", "mipmap");
        }

        if (resId == 0) {
            resId = getIconResId("icon", "drawable");
        }

        return resId;
    }

    /**
     * Retrieve resource id of the specified icon.
     *
     * @param icon The name of the icon.
     * @param type The resource type where to look for.
     *
     * @return The resource id or 0 if not found.
     */
    private int getIconResId(String icon, String type) {
        Resources res = getResources();
        String pkgName = getPackageName();

        return res.getIdentifier(icon, type, pkgName);
    }

    /**
     * Get the visibility constant from a string.
     *
     * @param visibility one of 'public', 'private', 'secret'
     *
     * @return The visibility constant if a match is found, 'private' otherwise
     */
    private int getVisibility(Visibility visibility) {
        if (visibility == Visibility.PUBLIC) {
            return Notification.VISIBILITY_PUBLIC;
        } else if (visibility == Visibility.SECRET) {
            return Notification.VISIBILITY_SECRET;
        } else {
            return Notification.VISIBILITY_PRIVATE;
        }
    }

    /**
     * Set notification color if its supported by the SDK.
     *
     * @param notification A Notification.Builder instance
     * @param color An hex color (red: FF0000)
     */
    private void setColor(NotificationCompat.Builder notification, String color) {
        if (color == null) {
            return;
        }

        try {
            int aRGB = Integer.parseInt(color, 16) + 0xFF000000;
            notification.setColor(aRGB);
        } catch (Exception e) {
            Log.d(TAG, "setColor error" + e.getMessage());
        }
    }

    /**
     * Returns the shared notification service manager.
     */
    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
}
