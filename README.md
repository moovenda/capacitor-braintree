# @moovenda/capacitor-braintree

Capacitor v3+ Braintree Native SDK plugin

## Install

```bash
npm install @moovenda/capacitor-braintree
npx cap sync
```

## API

<docgen-index>

* [`setToken(...)`](#settoken)
* [`showDropIn(...)`](#showdropin)
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


### Interfaces


#### DropInToken

| Prop        | Type                |
| ----------- | ------------------- |
| **`token`** | <code>string</code> |


#### DropInOptions

| Prop           | Type                |
| -------------- | ------------------- |
| **`amount`**   | <code>string</code> |
| **`disabled`** | <code>{}</code>     |


#### DropInResult

| Prop                       | Type                                                                                                                                                                            |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`cancelled`**            | <code>boolean</code>                                                                                                                                                            |
| **`nonce`**                | <code>string</code>                                                                                                                                                             |
| **`type`**                 | <code>string</code>                                                                                                                                                             |
| **`localizedDescription`** | <code>string</code>                                                                                                                                                             |
| **`card`**                 | <code>{ lastTwo: string; network: string; }</code>                                                                                                                              |
| **`payPalAccount`**        | <code>{ email: string; firstName: string; lastName: string; phone: string; billingAddress: string; shippingAddress: string; clientMetadataId: string; payerId: string; }</code> |
| **`applePaycard`**         | <code>any</code>                                                                                                                                                                |
| **`threeDSecureCard`**     | <code>{ liabilityShifted: boolean; liabilityShiftPossible: boolean; }</code>                                                                                                    |
| **`venmoAccount`**         | <code>{ username: string; }</code>                                                                                                                                              |

</docgen-api>
