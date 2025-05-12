import { WebPlugin } from '@capacitor/core';
import type { BackgroundModePlugin, ISettings, MicrophonePermissionStatus, NotificationPermissionStatus } from './definitions';
export declare class BackgroundModeWeb extends WebPlugin implements BackgroundModePlugin {
    enable(): Promise<void>;
    disable(): Promise<void>;
    getSettings(): Promise<{
        settings: ISettings;
    }>;
<<<<<<< HEAD
<<<<<<< HEAD
    updateNotification(): Promise<void>;
=======
    setSettings(_settings: Partial<ISettings>): Promise<void>;
>>>>>>> 0123837 (Start service only on foreground)
=======
    updateNotification(): Promise<void>;
>>>>>>> 3141302 (Fix notification updating)
    checkNotificationsPermission(): Promise<NotificationPermissionStatus>;
    requestNotificationsPermission(): Promise<NotificationPermissionStatus>;
    checkMicrophonePermission(): Promise<MicrophonePermissionStatus>;
    requestMicrophonePermission(): Promise<MicrophonePermissionStatus>;
    checkBatteryOptimizations(): Promise<{
        enabled: boolean;
    }>;
    requestDisableBatteryOptimizations(): Promise<{
        enabled: boolean;
    }>;
    enableWebViewOptimizations(): Promise<void>;
    disableWebViewOptimizations(): Promise<void>;
    moveToBackground(): Promise<void>;
    moveToForeground(): Promise<void>;
    isScreenOff(): Promise<{
        isScreenOff: boolean;
    }>;
    isEnabled(): Promise<{
        enabled: boolean;
    }>;
    isActive(): Promise<{
        activated: boolean;
    }>;
    wakeUp(): Promise<void>;
    unlock(): Promise<void>;
}
