import { registerPlugin } from '@capacitor/core';

import type { BraintreePlugin } from './definitions';

const Braintree = registerPlugin<BraintreePlugin>('Braintree', {
  web: () => import('./web').then(m => new m.BraintreeWeb()),
});

export * from './definitions';
export { Braintree };
