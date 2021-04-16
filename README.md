# WORK IN PROGRESS

Android still not implemented.

# @moovenda/capacitor-braintree

Capacitor v3+ Braintree Native SDK plugin for 3D Secure-enabled payments

## Install

```bash
npm install @moovenda/capacitor-braintree
npx cap sync
```

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
if url.scheme?.localizedCaseInsensitiveCompare("com.moovenda.cliente.payments") == .orderedSame {
  BTAppSwitch.handleOpen(url, options: options)
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
