import { WebPlugin } from '@capacitor/core';

import type { BackgroundModePlugin } from './definitions';

export class BackgroundModeWeb
  extends WebPlugin
  implements BackgroundModePlugin
{
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
