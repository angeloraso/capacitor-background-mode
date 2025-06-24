import { WebPlugin } from '@capacitor/core';
export class BackgroundModeWeb extends WebPlugin {
    async enable() {
        throw this.unimplemented('Not implemented on web.');
    }
    async disable() {
        throw this.unimplemented('Not implemented on web.');
    }
    async getSettings() {
        throw this.unimplemented('Not implemented on web.');
    }
<<<<<<< HEAD
<<<<<<< HEAD
    async updateNotification() {
=======
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    async setSettings(_settings) {
>>>>>>> 0123837 (Start service only on foreground)
=======
    async updateNotification() {
>>>>>>> 3141302 (Fix notification updating)
        throw this.unimplemented('Not implemented on web.');
    }
    async checkNotificationsPermission() {
        throw this.unimplemented('Not implemented on web.');
    }
    async requestNotificationsPermission() {
        throw this.unimplemented('Not implemented on web.');
    }
    async checkMicrophonePermission() {
        throw this.unimplemented('Not implemented on web.');
    }
    async requestMicrophonePermission() {
        throw this.unimplemented('Not implemented on web.');
    }
    async checkBatteryOptimizations() {
        throw this.unimplemented('Not implemented on web.');
    }
    async requestDisableBatteryOptimizations() {
        throw this.unimplemented('Not implemented on web.');
    }
    async enableWebViewOptimizations() {
        throw this.unimplemented('Not implemented on web.');
    }
    async disableWebViewOptimizations() {
        throw this.unimplemented('Not implemented on web.');
    }
    async moveToBackground() {
        throw this.unimplemented('Not implemented on web.');
    }
    async moveToForeground() {
        throw this.unimplemented('Not implemented on web.');
    }
    async isScreenOff() {
        throw this.unimplemented('Not implemented on web.');
    }
    async isEnabled() {
        throw this.unimplemented('Not implemented on web.');
    }
    async isActive() {
        throw this.unimplemented('Not implemented on web.');
    }
    async wakeUp() {
        throw this.unimplemented('Not implemented on web.');
    }
    async unlock() {
        throw this.unimplemented('Not implemented on web.');
    }
}
//# sourceMappingURL=web.js.map