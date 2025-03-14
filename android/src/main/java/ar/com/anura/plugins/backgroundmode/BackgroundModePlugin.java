package ar.com.anura.plugins.backgroundmode;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

@CapacitorPlugin(
    name = "BackgroundMode",
    permissions = {
        @Permission(alias = BackgroundModePlugin.BACKGROUND_MODE_NOTIFICATIONS_PERMISSION, strings = { Manifest.permission.POST_NOTIFICATIONS }),
        @Permission(alias = BackgroundModePlugin.BACKGROUND_MODE_MICROPHONE_PERMISSION, strings = { Manifest.permission.FOREGROUND_SERVICE_MICROPHONE, Manifest.permission.RECORD_AUDIO })
    }
)
public class BackgroundModePlugin extends Plugin {

    private BackgroundMode backgroundMode;

    static final String BACKGROUND_MODE_NOTIFICATIONS_PERMISSION = "notifications";
    static final String BACKGROUND_MODE_MICROPHONE_PERMISSION = "microphone";

    public void load() {
        AppCompatActivity activity = getActivity();
        Context context = getContext();
        View webView = bridge.getWebView();
        backgroundMode = new BackgroundMode(activity, context, webView);
        backgroundMode.setBackgroundModeEventListener(this::onBackgroundModeEvent);
    }

    void onBackgroundModeEvent(String event) {
        JSObject jsObject = new JSObject();
        switch (event) {
            case BackgroundMode.EVENT_APP_IN_BACKGROUND:
            case BackgroundMode.EVENT_APP_IN_FOREGROUND:
                bridge.triggerWindowJSEvent(event);
                notifyListeners(event, jsObject);
                break;
        }
    }

    @PluginMethod
    public void enable(PluginCall call) {
        if (getActivity().isFinishing()) {
            String appFinishingMsg = getActivity().getString(R.string.app_finishing);
            call.reject(appFinishingMsg);
            return;
        }

        backgroundMode.enable();

        call.resolve();
    }

    /**
     * Called when the activity is no longer visible to the user.
     */
    @Override
    public void handleOnStop() {
        backgroundMode.onStop();
    }

    /**
     * Called when the activity will start interacting with the user.
     */
    @Override
    public void handleOnResume() {
        backgroundMode.onResume();
    }

    /**
     * Called when the activity will be destroyed.
     */
    @Override
    public void handleOnDestroy() {
        backgroundMode.onDestroy();
    }

    @PluginMethod
    public void disable(PluginCall call) {
        if (getActivity().isFinishing()) {
            String appFinishingMsg = getActivity().getString(R.string.app_finishing);
            call.reject(appFinishingMsg);
            return;
        }

        backgroundMode.disable();
        call.resolve();
    }

    @PluginMethod
    public void getSettings(PluginCall call) {
        BackgroundModeSettings settings = backgroundMode.getSettings();
        JSObject res = new JSObject();
        res.put("title", settings.getTitle());
        res.put("text", settings.getText());
        res.put("subText", settings.getSubText());
        res.put("bigText", settings.getBigText());
        res.put("resume", settings.getResume());
        res.put("silent", settings.getSilent());
        res.put("hidden", settings.getHidden());
        res.put("color", settings.getColor());
        res.put("icon", settings.getIcon());
        res.put("channelName", settings.getChannelName());
        res.put("channelDescription", settings.getChannelDescription());
        res.put("allowClose", settings.getAllowClose());
        res.put("closeIcon", settings.getCloseIcon());
        res.put("closeTitle", settings.getCloseTitle());
        res.put("showWhen", settings.getShowWhen());
        res.put("visibility", settings.getVisibility());
        res.put("disableWebViewOptimization", settings.isDisableWebViewOptimization());
        call.resolve(res);
    }

    @PluginMethod
    public void setSettings(PluginCall call) {
        BackgroundModeSettings currentSettings = backgroundMode.getSettings();
        BackgroundModeSettings settings = buildSettings(currentSettings, call);
        backgroundMode.setSettings(settings);
        call.resolve();
    }

    @PluginMethod
    public void moveToBackground(PluginCall call) {
        backgroundMode.moveToBackground();
        call.resolve();
    }

    @PluginMethod
    public void moveToForeground(PluginCall call) {
        backgroundMode.moveToForeground();
        call.resolve();
    }

    @PluginMethod
    public void isScreenOff(PluginCall call) {
        boolean isScreenOff = backgroundMode.isScreenOff();
        JSObject res = new JSObject();
        res.put("isScreenOff", isScreenOff);
        call.resolve(res);
    }

    @PluginMethod
    public void isEnabled(PluginCall call) {
        boolean isEnabled = backgroundMode.isEnabled();
        JSObject res = new JSObject();
        res.put("enabled", isEnabled);
        call.resolve(res);
    }

    @PluginMethod
    public void wakeUp(PluginCall call) {
        backgroundMode.wakeUp();
        call.resolve();
    }

    @PluginMethod
    public void unlock(PluginCall call) {
        backgroundMode.unlock();
        call.resolve();
    }

    @PluginMethod
    public void checkBatteryOptimizations(PluginCall call) {
        boolean isIgnoring = backgroundMode.isIgnoringBatteryOptimizations();
        JSObject res = new JSObject();
        res.put("enabled", !isIgnoring);
        call.resolve(res);
    }

    @PluginMethod
    public void requestDisableBatteryOptimizations(PluginCall call) {
        backgroundMode.requestDisableBatteryOptimizations((boolean isIgnoring) -> {
            JSObject res = new JSObject();
            res.put("enabled", !isIgnoring);
            call.resolve(res);
        });
    }

    @PluginMethod
    public void enableWebViewOptimizations(PluginCall call) {
        backgroundMode.enableWebViewOptimizations();
        call.resolve();
    }

    @PluginMethod
    public void disableWebViewOptimizations(PluginCall call) {
        backgroundMode.disableWebViewOptimizations();
        call.resolve();
    }

    @PluginMethod
    public void checkNotificationsPermission(PluginCall call) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || getPermissionState(BACKGROUND_MODE_NOTIFICATIONS_PERMISSION) == PermissionState.GRANTED) {
            notificationPermissionCallback(call);
        } else {
            super.checkPermissions(call);
        }
    }

    @PluginMethod
    public void requestNotificationsPermission(PluginCall call) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || getPermissionState(BACKGROUND_MODE_NOTIFICATIONS_PERMISSION) == PermissionState.GRANTED) {
            notificationPermissionCallback(call);
        } else {
            requestPermissionForAlias(BACKGROUND_MODE_NOTIFICATIONS_PERMISSION, call, "notificationPermissionCallback");
        }
    }

    @PermissionCallback
    private void notificationPermissionCallback(PluginCall call) {
        JSObject permissionsResultJSON = new JSObject();
        permissionsResultJSON.put(BACKGROUND_MODE_NOTIFICATIONS_PERMISSION, getPermissionText(backgroundMode.areNotificationsEnabled()));
        call.resolve(permissionsResultJSON);
    }

    @PluginMethod
    public void checkMicrophonePermissions(PluginCall call) {
        microphonePermissionCallback(call);
    }

    @PluginMethod
    public void requestMicrophonePermissions(PluginCall call) {
        if (getPermissionState(BACKGROUND_MODE_MICROPHONE_PERMISSION) == PermissionState.GRANTED) {
            microphonePermissionCallback(call);
        } else {
            requestPermissionForAlias(BACKGROUND_MODE_MICROPHONE_PERMISSION, call, "microphonePermissionCallback");
        }
    }

    @PermissionCallback
    private void microphonePermissionCallback(PluginCall call) {
        JSObject permissionResultJSON = new JSObject();
        permissionResultJSON.put(BACKGROUND_MODE_MICROPHONE_PERMISSION, getPermissionText(backgroundMode.isMicrophoneEnabled()));
        call.resolve(permissionResultJSON);
    }

    private String getPermissionText(boolean enabled) {
        if (enabled) {
            return "granted";
        } else {
            return "denied";
        }
    }

    private BackgroundModeSettings buildSettings(BackgroundModeSettings settings, PluginCall call) {
        String title = call.getString("title");
        if (title != null) {
            settings.setTitle(title);
        }

        String text = call.getString("text");
        if (text != null) {
            settings.setText(text);
        }

        String subText = call.getString("subText");
        if (subText != null) {
            settings.setSubText(subText);
        }

        Boolean bigText = call.getBoolean("bigText");
        if (bigText != null) {
            settings.setBigText(bigText);
        }

        Boolean resume = call.getBoolean("resume");
        if (resume != null) {
            settings.setResume(resume);
        }

        Boolean silent = call.getBoolean("silent");
        if (silent != null) {
            settings.setSilent(silent);
        }

        Boolean hidden = call.getBoolean("hidden");
        if (hidden != null) {
            settings.setHidden(hidden);
        }

        String color = call.getString("color");
        if (color != null) {
            settings.setColor(color);
        }

        String icon = call.getString("icon");
        if (icon != null) {
            settings.setIcon(icon);
        }

        String channelName = call.getString("channelName");
        if (channelName != null) {
            settings.setChannelName(channelName);
        }

        String channelDescription = call.getString("channelDescription");
        if (channelDescription != null) {
            settings.setChannelDescription(channelDescription);
        }

        Boolean allowClose = call.getBoolean("allowClose");
        if (allowClose != null) {
            settings.setAllowClose(allowClose);
        }

        String closeIcon = call.getString("closeIcon");
        if (closeIcon != null) {
            settings.setCloseIcon(closeIcon);
        }

        String closeTitle = call.getString("closeTitle");
        if (closeTitle != null) {
            settings.setCloseTitle(closeTitle);
        }

        Boolean showWhen = call.getBoolean("showWhen");
        if (showWhen != null) {
            settings.setShowWhen(showWhen);
        }

        String visibility = call.getString("visibility");
        if (visibility != null) {
            settings.setVisibility(visibility);
        }

        Boolean disableWebViewOptimization = call.getBoolean("disableWebViewOptimization");
        if (disableWebViewOptimization != null) {
            settings.setDisableWebViewOptimization(disableWebViewOptimization);
        }

        return settings;
    }
}
