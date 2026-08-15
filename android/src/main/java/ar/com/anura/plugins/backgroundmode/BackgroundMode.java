package ar.com.anura.plugins.backgroundmode;

import static android.content.Context.POWER_SERVICE;
import static android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;
import static android.view.WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class BackgroundMode {

    private final String TAG = "BackgroundMode";
    private final int FLAGS = FLAG_ALLOW_LOCK_WHILE_SCREEN_ON | FLAG_SHOW_WHEN_LOCKED | FLAG_TURN_SCREEN_ON | FLAG_DISMISS_KEYGUARD;
    private final Context mContext;
    private final AppCompatActivity mActivity;
    private final View mWebView;
    private BackgroundModeSettings mSettings;
    private boolean mServiceStarted = false;
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
    // The app tried to start the service but there was some obstacle and it should try to start it the next time the app comes to the foreground
    private boolean mScheduleStartService = false;

    // Flag indicates if the plugin is enabled or disabled
    private boolean mIsDisabled = true;

    private Callback disableBatteryOptimizationCallback;
    private final ActivityResultLauncher<Intent> activityResultLauncher;

    BackgroundMode(final AppCompatActivity activity, final Context context, final View webView) {
        mActivity = activity;
        mContext = context;
        mWebView = webView;

        mSettings = new BackgroundModeSettings.Builder().build();
        mServiceStarted = BackgroundModeService.isRunning();
        mIsDisabled = !mServiceStarted;

        activityResultLauncher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (result) -> {
            if (disableBatteryOptimizationCallback != null) {
                disableBatteryOptimizationCallback.execute(isIgnoringBatteryOptimizations());
                disableBatteryOptimizationCallback = null;
            }
        });
    }

    public void onStop() {
        mInBackground = true;
        if (backgroundModeEventListener != null) {
            backgroundModeEventListener.onBackgroundModeEvent(EVENT_APP_IN_BACKGROUND);
        }

        if (!isEnabled() || !isIgnoringBatteryOptimizations()) {
            return;
        }

        /**
         * When activity loses focus, if disableWebViewOptimization setting is true, tell the android.webkit.WebView that it is still visible.
         * */
        if (mSettings.isDisableWebViewOptimization()) {
            // Wake up the app one second after the WebView has put it to sleep
            new Handler(Looper.getMainLooper()).postDelayed(
                () -> mWebView.dispatchWindowVisibilityChanged(View.VISIBLE),
                1000
            );
        }
    }

    public boolean areNotificationsEnabled() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        return notificationManager.areNotificationsEnabled();
    }

    public boolean isMicrophonePermissionGranted() {
        boolean recordAudioGranted = ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        boolean foregroundServiceMicrophoneGranted = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            foregroundServiceMicrophoneGranted = ContextCompat.checkSelfPermission(mContext, Manifest.permission.FOREGROUND_SERVICE_MICROPHONE) == PackageManager.PERMISSION_GRANTED;
        }

        return recordAudioGranted && foregroundServiceMicrophoneGranted;
    }

    public boolean isMicrophoneMuted() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return audioManager != null && audioManager.isMicrophoneMute();
    }

    public boolean isMicrophoneEnabled() {
        return isMicrophonePermissionGranted();
    }

    public void onResume() {
        mInBackground = false;
        if (backgroundModeEventListener != null) {
            backgroundModeEventListener.onBackgroundModeEvent(EVENT_APP_IN_FOREGROUND);
        }

        if (mScheduleStartService) {
            startService(mSettings);
        }
    }

    public void onDestroy() {
        releaseWakeLock();
        backgroundModeEventListener = null;
    }

    public void enable(final BackgroundModeSettings settings) {
        mIsDisabled = false;
        mSettings = mSettings.merge(settings);
        startService(mSettings);
    }

    public void disable() {
        stopService();
        mIsDisabled = true;
    }

    private void startService(final BackgroundModeSettings settings) {
        if (settings == null) {
            return;
        }

        if (mInBackground) {
            mScheduleStartService = true;
            return;
        }

        if (
            mIsDisabled ||
            BackgroundModeService.isRunning() ||
            (settings.isAllowMicrophoneInBackground() && !isMicrophoneEnabled()) ||
            !areNotificationsEnabled()
        ) {
            return;
        }

        Intent intent = new Intent(mContext, BackgroundModeService.class);
        intent.putExtra("settings", settings);

        mContext.startForegroundService(intent);
        mServiceStarted = true;
        mScheduleStartService = false;
    }

    private void stopService() {
        if (!mServiceStarted && !BackgroundModeService.isRunning()) {
            return;
        }

        Intent intent = new Intent(mContext, BackgroundModeService.class);
        mContext.stopService(intent);
        mServiceStarted = false;
        mSettings = new BackgroundModeSettings.Builder().build();
    }

    public void updateNotification(BackgroundModeSettings settings) {
        mSettings = mSettings.merge(settings);
        BackgroundModeService.updateRunningNotification(mSettings);
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
            return;
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
            if (launchIntent == null) {
                return;
            }
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
        return BackgroundModeService.isRunning();
    }

    private void clearScreenAndKeyguardFlags() {
        mActivity.runOnUiThread(() -> {
            mActivity.setShowWhenLocked(false);
            mActivity.setTurnScreenOn(false);
            mActivity.getWindow().clearFlags(FLAGS);
        });
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
        mActivity.runOnUiThread(() -> {
            mActivity.setShowWhenLocked(true);
            mActivity.setTurnScreenOn(true);
            mActivity.getWindow().addFlags(FLAGS);
        });
    }

    public void setBackgroundModeEventListener(@Nullable BackgroundModeEventListener backgroundModeEventListener) {
        this.backgroundModeEventListener = backgroundModeEventListener;
    }

    private void openApp() {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        if (intent != null) {
            mActivity.startActivity(intent);
        }
    }
}
