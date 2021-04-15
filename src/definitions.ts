export interface DropInToken {
  token: string;
}

export interface DropInOptions {
  amount: string;
  disabled?: string[];
  givenName: string | undefined;
  surname: string | undefined;
  email: string | undefined;
  phoneNumber: string | undefined;
  streetAddress: string | undefined;
  postalCode: string | undefined;
  locality: string | undefined;
  countryCodeAlpha2: string | undefined;
}

export interface DataCollectorOptions {
  merchantId: string;
}

export interface DropInResult {
  cancelled: boolean;
  nonce: string;
  type: string;
  localizedDescription: string;
  deviceData: string;
  card: {
    lastTwo: string;
    network: string;
  };
  payPalAccount: {
    email: string;
    firstName: string;
    lastName: string;
    phone: string;
    billingAddress: string;
    shippingAddress: string;
    clientMetadataId: string;
    payerId: string;
  };
  applePaycard: any;
  threeDSecureCard: {
    liabilityShifted: boolean;
    liabilityShiftPossible: boolean;
  };
  venmoAccount: {
    username: string;
  };
}

export interface BraintreePlugin {
  setToken(options: DropInToken): Promise<any>;

  showDropIn(options: DropInOptions): Promise<DropInResult>;

  getDeviceData(options: DataCollectorOptions): Promise<any>;
}