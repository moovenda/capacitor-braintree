import { WebPlugin } from '@capacitor/core';

import type { BraintreePlugin } from './definitions';

export class BraintreeWeb extends WebPlugin implements BraintreePlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
