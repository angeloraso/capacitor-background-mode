import { WebPlugin } from '@capacitor/core';
import type { BackgroundModePlugin, ISettings, PermissionStatus } from './definitions';
export declare class BackgroundModeWeb extends WebPlugin implements BackgroundModePlugin {
    enable(): Promise<void>;
    disable(): Promise<void>;
    getSettings(): Promise<{
        settings: ISettings;
    }>;
    setSettings(_settings: Partial<ISettings>): Promise<void>;
    checkNotificationsPermission(): Promise<PermissionStatus>;
    requestNotificationsPermission(): Promise<PermissionStatus>;
    checkMicrophonePermission(): Promise<PermissionStatus>;
    requestMicrophonePermission(): Promise<PermissionStatus>;
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
