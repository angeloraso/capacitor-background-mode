# @anuradev/capacitor-background-mode

Capacitor plugin to enable background mode

This plugin replace to [capacitor-plugin-background-mode](https://github.com/angeloraso/capacitor-plugin-background-mode)

## Install

```bash
npm install @anuradev/capacitor-background-mode
npx cap sync
```

## API

<docgen-index>

* [`enable()`](#enable)
* [`disable()`](#disable)
* [`getSettings()`](#getsettings)
* [`setSettings(...)`](#setsettings)
* [`checkForegroundPermission()`](#checkforegroundpermission)
* [`requestForegroundPermission()`](#requestforegroundpermission)
* [`checkNotificationsPermission()`](#checknotificationspermission)
* [`requestNotificationsPermission()`](#requestnotificationspermission)
* [`checkBatteryOptimizations()`](#checkbatteryoptimizations)
* [`requestDisableBatteryOptimizations()`](#requestdisablebatteryoptimizations)
* [`enableWebViewOptimizations()`](#enablewebviewoptimizations)
* [`disableWebViewOptimizations()`](#disablewebviewoptimizations)
* [`moveToBackground()`](#movetobackground)
* [`moveToForeground()`](#movetoforeground)
* [`isScreenOff()`](#isscreenoff)
* [`isEnabled()`](#isenabled)
* [`isActive()`](#isactive)
* [`wakeUp()`](#wakeup)
* [`unlock()`](#unlock)
* [`addListener('appInBackground', ...)`](#addlistenerappinbackground)
* [`addListener('appInForeground', ...)`](#addlistenerappinforeground)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### enable()

```typescript
enable() => Promise<void>
```

--------------------


### disable()

```typescript
disable() => Promise<void>
```

--------------------


### getSettings()

```typescript
getSettings() => Promise<{ settings: ISettings; }>
```

**Returns:** <code>Promise&lt;{ settings: <a href="#isettings">ISettings</a>; }&gt;</code>

--------------------


### setSettings(...)

```typescript
setSettings(settings: Partial<ISettings>) => Promise<void>
```

| Param          | Type                                                                                  |
| -------------- | ------------------------------------------------------------------------------------- |
| **`settings`** | <code><a href="#partial">Partial</a>&lt;<a href="#isettings">ISettings</a>&gt;</code> |

--------------------


### checkForegroundPermission()

```typescript
checkForegroundPermission() => Promise<PermissionStatus>
```

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### requestForegroundPermission()

```typescript
requestForegroundPermission() => Promise<PermissionStatus>
```

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### checkNotificationsPermission()

```typescript
checkNotificationsPermission() => Promise<PermissionStatus>
```

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### requestNotificationsPermission()

```typescript
requestNotificationsPermission() => Promise<PermissionStatus>
```

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### checkBatteryOptimizations()

```typescript
checkBatteryOptimizations() => Promise<{ disabled: boolean; }>
```

**Returns:** <code>Promise&lt;{ disabled: boolean; }&gt;</code>

--------------------


### requestDisableBatteryOptimizations()

```typescript
requestDisableBatteryOptimizations() => Promise<{ disabled: boolean; }>
```

**Returns:** <code>Promise&lt;{ disabled: boolean; }&gt;</code>

--------------------


### enableWebViewOptimizations()

```typescript
enableWebViewOptimizations() => Promise<void>
```

--------------------


### disableWebViewOptimizations()

```typescript
disableWebViewOptimizations() => Promise<void>
```

--------------------


### moveToBackground()

```typescript
moveToBackground() => Promise<void>
```

--------------------


### moveToForeground()

```typescript
moveToForeground() => Promise<void>
```

--------------------


### isScreenOff()

```typescript
isScreenOff() => Promise<{ isScreenOff: boolean; }>
```

**Returns:** <code>Promise&lt;{ isScreenOff: boolean; }&gt;</code>

--------------------


### isEnabled()

```typescript
isEnabled() => Promise<{ enabled: boolean; }>
```

**Returns:** <code>Promise&lt;{ enabled: boolean; }&gt;</code>

--------------------


### isActive()

```typescript
isActive() => Promise<{ activated: boolean; }>
```

**Returns:** <code>Promise&lt;{ activated: boolean; }&gt;</code>

--------------------


### wakeUp()

```typescript
wakeUp() => Promise<void>
```

--------------------


### unlock()

```typescript
unlock() => Promise<void>
```

--------------------


### addListener('appInBackground', ...)

```typescript
addListener(eventName: 'appInBackground', listenerFunc: () => void) => Promise<PluginListenerHandle>
```

| Param              | Type                           |
| ------------------ | ------------------------------ |
| **`eventName`**    | <code>'appInBackground'</code> |
| **`listenerFunc`** | <code>() =&gt; void</code>     |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('appInForeground', ...)

```typescript
addListener(eventName: 'appInForeground', listenerFunc: () => void) => Promise<PluginListenerHandle>
```

| Param              | Type                           |
| ------------------ | ------------------------------ |
| **`eventName`**    | <code>'appInForeground'</code> |
| **`listenerFunc`** | <code>() =&gt; void</code>     |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

--------------------


### Interfaces


#### ISettings

| Prop                             | Type                                           |
| -------------------------------- | ---------------------------------------------- |
| **`title`**                      | <code>string</code>                            |
| **`text`**                       | <code>string</code>                            |
| **`subText`**                    | <code>string</code>                            |
| **`bigText`**                    | <code>boolean</code>                           |
| **`resume`**                     | <code>boolean</code>                           |
| **`silent`**                     | <code>boolean</code>                           |
| **`hidden`**                     | <code>boolean</code>                           |
| **`color`**                      | <code>string</code>                            |
| **`icon`**                       | <code>string</code>                            |
| **`channelName`**                | <code>string</code>                            |
| **`channelDescription`**         | <code>string</code>                            |
| **`allowClose`**                 | <code>boolean</code>                           |
| **`closeIcon`**                  | <code>string</code>                            |
| **`closeTitle`**                 | <code>string</code>                            |
| **`showWhen`**                   | <code>boolean</code>                           |
| **`disableWebViewOptimization`** | <code>boolean</code>                           |
| **`visibility`**                 | <code>'public' \| 'private' \| 'secret'</code> |


#### PermissionStatus

| Prop          | Type                                                        |
| ------------- | ----------------------------------------------------------- |
| **`display`** | <code><a href="#permissionstate">PermissionState</a></code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


### Type Aliases


#### Partial

Make all properties in T optional

<code>{ [P in keyof T]?: T[P]; }</code>


#### PermissionState

<code>'prompt' | 'prompt-with-rationale' | 'granted' | 'denied'</code>

</docgen-api>
