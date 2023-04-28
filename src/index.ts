import { registerPlugin } from '@capacitor/core';

import type { BackgroundModePlugin } from './definitions';

const BackgroundMode = registerPlugin<BackgroundModePlugin>('BackgroundMode', {
  web: () => import('./web').then(m => new m.BackgroundModeWeb()),
});

export * from './definitions';
export { BackgroundMode };
