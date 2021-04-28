# @moovenda/capacitor-braintree

Capacitor v3+ Braintree Native SDK plugin for 3D Secure-enabled payments

## Install

1. `npm install @moovenda/capacitor-braintree`
2. `npx cap sync`

## iOS Setup
### Register a URL type

1. In Xcode, click on your project in the Project Navigator and navigate to **App Target > Info > URL Types**
2. Click **[+]** to add a new URL type
3. Under **URL Schemes**, enter your app switch return URL scheme. This scheme must start with your app's Bundle ID and be dedicated to Braintree app switch returns. For example, if the app bundle ID is `com.your-company.your-app`, then your URL scheme could be `com.your-company.your-app.payments`.

For further informations please refer to the [official docs](https://developers.braintreepayments.com/guides/client-sdk/setup/ios/v4).

### Add listener in your AppDelegate.swift

1. Open your `ios/App/App/AppDelegate.swift` file
2. Import the braintree SDK with `import Braintree`
3. Search for the function `func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey: Any] = [:]) -> Bool`
4. Append the following snippet above the `return` line

```swift
if url.scheme?.localizedCaseInsensitiveCompare("com.your-company.your-app.payments") == .orderedSame {
  BTAppSwitch.handleOpen(url, options: options)
}
```

## Android Setup

### Browser switch setup

1. Edit your `android/app/src/main/AndroidManifest.xml` file
2. Add this snippet between within the `<application>` tag:

```
<activity android:name="com.braintreepayments.api.BraintreeBrowserSwitchActivity"
    android:launchMode="singleTask">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="${applicationId}.braintree" />
    </intent-filter>
</activity>
```
3. Require cardinalcommerce editing the application's `build.gradle` adding the following repository:
```
 allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url "https://cardinalcommerce.bintray.com/android"
            credentials {
                username 'braintree-team-sdk@cardinalcommerce'
                password '220cc9476025679c4e5c843666c27d97cfb0f951'
            }
        }
    }
}
```

## Usage example

```javascript
import { Braintree } from '@moovenda/capacitor-braintree';

let payment;

try {
  await Braintree.setToken({
    token: CLIENT_TOKEN_FROM_SERVER,
  });

  payment = await Braintree.showDropIn({
    amount: '0.00',
    disabled: ['venmo', 'applePay', 'googlePay'],
    givenName: customerDetails.firstName,
    surname: customerDetails.lastName,
    email: customerDetails.email,
    phoneNumber: customerDetails.phone,
    streetAddress: customerDetails.streetAddress,
    postalCode: customerDetails.zipcode,
    locality: customerDetails.city,
    countryCodeAlpha2: customerDetails.CountryAlphaCode,
  });
} catch(e) {
  console.error(e);
}

if (!payment.cancelled || !payment.nonce) {
  try {
    const deviceData = await Braintree.getDeviceData({
      merchantId: BRAINTREE_MERCHANT_ID,
    });
    await yourApiCall(`order/${orderId}/pay`, {
      nonce: payment.nonce,
      deviceData: deviceData,
    });
  } catch (error) {
    console.error(error);
  }
}
```

## API

<docgen-index>

* [`setToken(...)`](#settoken)
* [`showDropIn(...)`](#showdropin)
* [`getDeviceData(...)`](#getdevicedata)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### setToken(...)

```typescript
setToken(options: DropInToken) => any
```

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code><a href="#dropintoken">DropInToken</a></code> |

**Returns:** <code>any</code>

--------------------


### showDropIn(...)

```typescript
showDropIn(options: DropInOptions) => any
```

| Param         | Type                                                    |
| ------------- | ------------------------------------------------------- |
| **`options`** | <code><a href="#dropinoptions">DropInOptions</a></code> |

**Returns:** <code>any</code>

--------------------


### getDeviceData(...)

```typescript
getDeviceData(options: DataCollectorOptions) => any
```

| Param         | Type                                                                  |
| ------------- | --------------------------------------------------------------------- |
| **`options`** | <code><a href="#datacollectoroptions">DataCollectorOptions</a></code> |

**Returns:** <code>any</code>

--------------------


### Interfaces


#### DropInToken

| Prop        | Type                |
| ----------- | ------------------- |
| **`token`** | <code>string</code> |


#### DropInOptions

| Prop                    | Type                |
| ----------------------- | ------------------- |
| **`amount`**            | <code>string</code> |
| **`disabled`**          | <code>{}</code>     |
| **`givenName`**         | <code>string</code> |
| **`surname`**           | <code>string</code> |
| **`email`**             | <code>string</code> |
| **`phoneNumber`**       | <code>string</code> |
| **`streetAddress`**     | <code>string</code> |
| **`postalCode`**        | <code>string</code> |
| **`locality`**          | <code>string</code> |
| **`countryCodeAlpha2`** | <code>string</code> |


#### DropInResult

| Prop                       | Type                                                                                                                                                                            |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`cancelled`**            | <code>boolean</code>                                                                                                                                                            |
| **`nonce`**                | <code>string</code>                                                                                                                                                             |
| **`type`**                 | <code>string</code>                                                                                                                                                             |
| **`localizedDescription`** | <code>string</code>                                                                                                                                                             |
| **`deviceData`**           | <code>string</code>                                                                                                                                                             |
| **`card`**                 | <code>{ lastTwo: string; network: string; }</code>                                                                                                                              |
| **`payPalAccount`**        | <code>{ email: string; firstName: string; lastName: string; phone: string; billingAddress: string; shippingAddress: string; clientMetadataId: string; payerId: string; }</code> |
| **`applePaycard`**         | <code>any</code>                                                                                                                                                                |
| **`threeDSecureCard`**     | <code>{ liabilityShifted: boolean; liabilityShiftPossible: boolean; }</code>                                                                                                    |
| **`venmoAccount`**         | <code>{ username: string; }</code>                                                                                                                                              |


#### DataCollectorOptions

| Prop             | Type                |
| ---------------- | ------------------- |
| **`merchantId`** | <code>string</code> |

</docgen-api>

