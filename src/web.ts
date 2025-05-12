import { WebPlugin } from '@capacitor/core';

import type { BackgroundModePlugin, ISettings, MicrophonePermissionStatus, NotificationPermissionStatus } from './definitions';

export class BackgroundModeWeb
  extends WebPlugin
  implements BackgroundModePlugin {
    async enable(): Promise<void>{
      throw this.unimplemented('Not implemented on web.');
    }
    async disable(): Promise<void>{
      throw this.unimplemented('Not implemented on web.');
    }
    async getSettings(): Promise<{settings: ISettings}>{
      throw this.unimplemented('Not implemented on web.');
    }
    async updateNotification(): Promise<void>{
      throw this.unimplemented('Not implemented on web.');
    }
    async checkNotificationsPermission(): Promise<NotificationPermissionStatus> {
      throw this.unimplemented('Not implemented on web.');
    }
    async requestNotificationsPermission(): Promise<NotificationPermissionStatus> {
      throw this.unimplemented('Not implemented on web.');
    }
    async checkMicrophonePermission(): Promise<MicrophonePermissionStatus> {
      throw this.unimplemented('Not implemented on web.');
    }
    async requestMicrophonePermission(): Promise<MicrophonePermissionStatus> {
      throw this.unimplemented('Not implemented on web.');
    }
    async checkBatteryOptimizations(): Promise<{enabled: boolean}>{
      throw this.unimplemented('Not implemented on web.');
    }
    async requestDisableBatteryOptimizations(): Promise<{enabled: boolean}>{
      throw this.unimplemented('Not implemented on web.');
    }
    async enableWebViewOptimizations(): Promise<void>{
      throw this.unimplemented('Not implemented on web.');
    }
    async disableWebViewOptimizations(): Promise<void>{
      throw this.unimplemented('Not implemented on web.');
    }
    async moveToBackground(): Promise<void>{
      throw this.unimplemented('Not implemented on web.');
    }
    async moveToForeground(): Promise<void>{
      throw this.unimplemented('Not implemented on web.');
    }
    async isScreenOff(): Promise<{isScreenOff: boolean}>{
      throw this.unimplemented('Not implemented on web.');
    }
    async isEnabled(): Promise<{enabled: boolean}>{
      throw this.unimplemented('Not implemented on web.');
    }
    async isActive(): Promise<{activated: boolean}>{
      throw this.unimplemented('Not implemented on web.');
    }
    async wakeUp(): Promise<void>{
      throw this.unimplemented('Not implemented on web.');
    }
    async unlock(): Promise<void>{
      throw this.unimplemented('Not implemented on web.');
    }
}
