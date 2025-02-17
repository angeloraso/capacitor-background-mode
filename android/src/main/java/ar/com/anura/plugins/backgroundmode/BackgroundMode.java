package ar.com.anura.plugins.backgroundmode;

import static android.content.Context.POWER_SERVICE;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL;
import static android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;
import static android.view.WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

public class BackgroundMode {

    private final String TAG = "BackgroundMode";
    private final int FLAGS = FLAG_ALLOW_LOCK_WHILE_SCREEN_ON | FLAG_SHOW_WHEN_LOCKED | FLAG_TURN_SCREEN_ON | FLAG_DISMISS_KEYGUARD;
    private final Context mContext;
    private final AppCompatActivity mActivity;
    private final View mWebView;
    private BackgroundModeSettings mSettings;
    private BackgroundModeService foregroundService;
    private boolean mShouldUnbind = false;
    private PowerManager.WakeLock wakeLock;

    @Nullable
    private BackgroundModeEventListener backgroundModeEventListener;

    static final String EVENT_APP_IN_BACKGROUND = "appInBackground";
    static final String EVENT_APP_IN_FOREGROUND = "appInForeground";

    interface BackgroundModeEventListener {
        void onBackgroundModeEvent(String event);
    }

    // Flag indicates if the app is in background or foreground
    private boolean mInBackground = false;

    // Flag indicates if the plugin is enabled or disabled
    private boolean mIsDisabled = true;

    private Callback disableBatteryOptimizationCallback;
    private final ActivityResultLauncher<Intent> activityResultLauncher;

    BackgroundMode(final AppCompatActivity activity, final Context context, final View webView) {
        mActivity = activity;
        mContext = context;
        mWebView = webView;
        mSettings = new BackgroundModeSettings();

      activityResultLauncher = activity.registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> disableBatteryOptimizationCallback.execute(isIgnoringBatteryOptimizations()));
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            foregroundService = ((BackgroundModeService.LocalBinder) iBinder).getService();
            foregroundService.updateNotification(mSettings);
        }

        public void onServiceDisconnected(ComponentName className) {
            foregroundService = null;
        }
    };

    public void onStop() {
        mInBackground = true;
        if (!isEnabled() || !isIgnoringBatteryOptimizations()) {
            return;
        }

        try {
            startService();
        } finally {
            clearKeyguardFlags();
        }

        /**
         * When activity loses focus, if disableWebViewOptimization setting is true, tell the android.webkit.WebView that it is still visible.
         * */
        if (mSettings.isDisableWebViewOptimization()) {
            // Wake up the app one second after the WebView has put it to sleep
            Thread thread = new Thread(
                () -> {
                    try {
                        Thread.sleep(1000);
                        mWebView.dispatchWindowVisibilityChanged(View.VISIBLE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            );

            thread.start();
        }
        assert backgroundModeEventListener != null;
        backgroundModeEventListener.onBackgroundModeEvent(EVENT_APP_IN_BACKGROUND);
    }

    public boolean areNotificationsEnabled() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        return notificationManager.areNotificationsEnabled();
    }

    public void onResume() {
        mInBackground = false;
        if (!isEnabled()) {
            return;
        }

        stopService();
        assert backgroundModeEventListener != null;
        backgroundModeEventListener.onBackgroundModeEvent(EVENT_APP_IN_FOREGROUND);
    }

    public void onDestroy() {
        stopService();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void clearKeyguardFlags() {
        mActivity.runOnUiThread(() -> mActivity.getWindow().clearFlags(FLAG_DISMISS_KEYGUARD));
    }

    public void enable() {
        mIsDisabled = false;

        if (mInBackground  && isIgnoringBatteryOptimizations()) {
            startService();
        }
    }

    public void disable() {
        stopService();
        mIsDisabled = true;
    }

    private void startService() {
        if (mIsDisabled || mShouldUnbind) {
            return;
        }

        Intent intent = new Intent(mContext, BackgroundModeService.class);

        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          new Handler(Looper.getMainLooper()).post(() -> {
            mContext.startForegroundService(intent);
            mShouldUnbind = true;
          });
        } else {
          mContext.startService(intent);
          mShouldUnbind = true;
        }
    }

    private void stopService() {
        if (!mShouldUnbind) {
            return;
        }

        Intent intent = new Intent(mContext, BackgroundModeService.class);
        mContext.unbindService(mConnection);
        mContext.stopService(intent);
        mShouldUnbind = false;
    }

    public BackgroundModeSettings getSettings() {
        return mSettings;
    }

    public void setSettings(BackgroundModeSettings settings) {
        mSettings = settings;
        if (mInBackground && mShouldUnbind && foregroundService != null) {
            foregroundService.updateNotification(settings);
        }
    }

    public boolean isIgnoringBatteryOptimizations() {
        String pkgName = mActivity.getPackageName();
        PowerManager pm = (PowerManager) mActivity.getSystemService(POWER_SERVICE);
        return pm.isIgnoringBatteryOptimizations(pkgName);
    }

    @SuppressLint("BatteryLife")
    public void requestDisableBatteryOptimizations(Callback callback) {
        if (isIgnoringBatteryOptimizations()) {
            callback.execute(true);
        }

        disableBatteryOptimizationCallback = callback;
        String pkgName = mActivity.getPackageName();
        Intent intent = new Intent();
        intent.setAction(ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + pkgName));

        activityResultLauncher.launch(intent);
    }

    public void enableWebViewOptimizations() {
        mSettings.setDisableWebViewOptimization(false);
    }

    public void disableWebViewOptimizations() {
        mSettings.setDisableWebViewOptimization(true);
        // Immediately dispatch visibility changed in case the app
        // has started in the background and is not visible
        mWebView.dispatchWindowVisibilityChanged(View.VISIBLE);
    }

    public boolean checkForegroundPermission() {
        return Settings.canDrawOverlays(mActivity);
    }

    public void requestForegroundPermission() {
        String pkgName = mActivity.getPackageName();
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + pkgName));
        mActivity.startActivity(intent);
    }

    public void moveToBackground() {
        if (mInBackground) {
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            mActivity.startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "moveToBackground error" + e.getMessage());
        }
    }

    public void moveToForeground() {
        if (!mInBackground) {
            return;
        }

        try {
            Intent launchIntent = mActivity.getPackageManager().getLaunchIntentForPackage(mActivity.getPackageName());
            assert launchIntent != null;
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            clearScreenAndKeyguardFlags();
            mActivity.startActivity(launchIntent);
        } catch (Exception e) {
            Log.d(TAG, "moveToForeground error" + e.getMessage());
        }
    }

    public boolean isScreenOff() {
        PowerManager pm = (PowerManager) mActivity.getSystemService(POWER_SERVICE);
        return !pm.isInteractive();
    }

    public boolean isEnabled() {
        return !mIsDisabled;
    }

    public boolean isActive() {
        return mInBackground;
    }

    private void clearScreenAndKeyguardFlags() {
        mActivity.runOnUiThread(
            () -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    mActivity.setShowWhenLocked(false);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    mActivity.setTurnScreenOn(false);
                }
                mActivity.getWindow().clearFlags(FLAGS);
            }
        );
    }

    public void wakeUp() {
        try {
            acquireWakeLock();
        } catch (Exception e) {
            releaseWakeLock();
        }
    }

    @SuppressWarnings("deprecation")
    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) mActivity.getSystemService(POWER_SERVICE);
        releaseWakeLock();

        if (!isScreenOff()) {
            return;
        }

        int level = PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
        wakeLock = pm.newWakeLock(level, TAG + ":wakelock");
        wakeLock.setReferenceCounted(false);
        wakeLock.acquire(1000);
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    public void unlock() {
        wakeUp();

        addScreenAndKeyguardFlags();
        openApp();
    }

    private void addScreenAndKeyguardFlags() {
        mActivity.runOnUiThread(
            () -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    mActivity.setShowWhenLocked(true);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    mActivity.setTurnScreenOn(true);
                }
                mActivity.getWindow().addFlags(FLAGS);
            }
        );
    }

    public void setBackgroundModeEventListener(@Nullable BackgroundModeEventListener backgroundModeEventListener) {
        this.backgroundModeEventListener = backgroundModeEventListener;
    }

    private void openApp() {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        mActivity.startActivity(intent);
    }
}
