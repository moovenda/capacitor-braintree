package com.moovenda.plugins.capacitorbraintree;

import android.content.Intent;
import android.app.Activity;
import android.os.Parcelable;
import android.util.Log;

import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.ThreeDSecureInfo;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;

import com.braintreepayments.api.models.PaymentMethodNonce;

// force firing "handleOnActivityResult" after Braintree activity ends
@NativePlugin(requestCodes={BraintreePlugin.DROP_IN_REQUEST})
@CapacitorPlugin(name = "Braintree")
public class BraintreePlugin extends Plugin {

   private String clientToken;

    /**
     * Logger tag
     */
    private static final String PLUGIN_TAG = "Braintree";

    static final String EXTRA_PAYMENT_RESULT = "payment_result";
    static final String EXTRA_DEVICE_DATA = "device_data";
    // static final String EXTRA_COLLECT_DEVICE_DATA = "collect_device_data";

    /**
     * In this version (simplified) using only "dropin" with nonce processed on server-side
     */
    static final int DROP_IN_REQUEST = 1;
    // private static final int GOOGLE_PAYMENT_REQUEST = 2;
    // private static final int CARDS_REQUEST = 3;
    // private static final int PAYPAL_REQUEST = 4;
    // private static final int VENMO_REQUEST = 5;
    // private static final int VISA_CHECKOUT_REQUEST = 6;
    // private static final int LOCAL_PAYMENTS_REQUEST = 7;
    // private static final int PREFERRED_PAYMENT_METHODS_REQUEST = 8;

    @PluginMethod()
    public void setToken(PluginCall call) {
        String token = call.getString("token");

        if (!call.getData().has("token")){
            call.reject("A token is required.");
            return;
        }
        this.clientToken = token;
        call.resolve();
    }

    @PluginMethod()
    public void showDropIn(PluginCall call) {
        saveCall(call);
        DropInRequest dropInRequest = new DropInRequest().clientToken(this.clientToken);
        Intent intent = dropInRequest.getIntent(getContext());
        Log.d(PLUGIN_TAG, "showDropIn started");
        startActivityForResult(call, intent, DROP_IN_REQUEST);
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);

        Log.d(PLUGIN_TAG, "handleOnActivityResult. Result code: "+resultCode+", intent: "+data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == DROP_IN_REQUEST) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                handleNonce(result.getPaymentMethodNonce(), result.getDeviceData());
            } else {
                // -- this is not used now, but implementation can be done, ex:
                //[..]
                //    Intent intent = new Intent(this, PayPalActivity.class).putExtra(EXTRA_COLLECT_DEVICE_DATA, Settings.shouldCollectDeviceData(this));
                //    startActivityForResult(intent, PAYPAL_REQUEST);
                //[..]
                Parcelable returnedData = data.getParcelableExtra(EXTRA_PAYMENT_RESULT);
                String deviceData = data.getStringExtra(EXTRA_DEVICE_DATA);
                if (returnedData instanceof PaymentMethodNonce) {
                    handleNonce((PaymentMethodNonce) returnedData, deviceData);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
            handleCanceled(result.getDeviceData());
        } else {
            Exception ex = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            String msg = ex.getMessage();
            Log.e(PLUGIN_TAG, "Error: "+msg);
            handleError(msg, ex);
        }
    }

    /**
     *
     * @param deviceData device info (not used in context)
     */
    @SuppressWarnings("unused")
    private void handleCanceled(String deviceData) {
        PluginCall call = getSavedCall();
        if (call == null) return;

        Log.d(PLUGIN_TAG, "handleNonce");

        JSObject resultMap = new JSObject();
        resultMap.put("cancelled", true);
        //resultMap.put("deviceData", deviceData);
        call.resolve(resultMap); // call.resolve(resultMap)
    }

    /**
     *
     * @param msg error message
     * @param ex Exception
     */
    private void handleError(String msg, Exception ex) {

        PluginCall call = getSavedCall();
        if (call == null) return;

        Log.d(PLUGIN_TAG, "handleError");
        call.reject(msg, ex);
    }

    /**
     * Helper used to return a dictionary of values from the given payment method nonce.
     * Handles several different types of nonces (eg for cards, PayPal, etc).
     *
     * @param paymentMethodNonce The nonce used to build a dictionary of data from.
     * @param deviceData Device info
     */
    @SuppressWarnings("unused")
    private void handleNonce(PaymentMethodNonce paymentMethodNonce, String deviceData) {

        PluginCall call = getSavedCall();
        if (call == null) return;

        Log.d(PLUGIN_TAG, "handleNonce");

        JSObject resultMap = new JSObject();

        resultMap.put("nonce", paymentMethodNonce.getNonce());
        resultMap.put("type", paymentMethodNonce.getTypeLabel());
        resultMap.put("localizedDescription", paymentMethodNonce.getDescription());
        // resultMap.put("deviceData", deviceData);

        // Card
        if (paymentMethodNonce instanceof CardNonce) {
            CardNonce cardNonce = (CardNonce)paymentMethodNonce;

            JSObject innerMap = new JSObject();
            innerMap.put("lastTwo", cardNonce.getLastTwo());
            innerMap.put("network", cardNonce.getCardType());

            resultMap.put("card", innerMap);
        }

        // PayPal
        if (paymentMethodNonce instanceof PayPalAccountNonce) {
            PayPalAccountNonce payPalAccountNonce = (PayPalAccountNonce)paymentMethodNonce;

            JSObject innerMap = new JSObject();
            resultMap.put("email", payPalAccountNonce.getEmail());
            resultMap.put("firstName", payPalAccountNonce.getFirstName());
            resultMap.put("lastName", payPalAccountNonce.getLastName());
            resultMap.put("phone", payPalAccountNonce.getPhone());
            //resultMap.put("billingAddress", payPalAccountNonce.getBillingAddress()); //TODO
            //resultMap.put("shippingAddress", payPalAccountNonce.getShippingAddress()); //TODO
            resultMap.put("clientMetadataId", payPalAccountNonce.getClientMetadataId());
            resultMap.put("payerId", payPalAccountNonce.getPayerId());

            resultMap.put("payPalAccount", innerMap);
        }

        // 3D Secure
        if (paymentMethodNonce instanceof CardNonce) {
            CardNonce cardNonce = (CardNonce) paymentMethodNonce;
            ThreeDSecureInfo threeDSecureInfo = cardNonce.getThreeDSecureInfo();

            if (threeDSecureInfo != null) {
                JSObject innerMap = new JSObject();
                innerMap.put("liabilityShifted", threeDSecureInfo.isLiabilityShifted());
                innerMap.put("liabilityShiftPossible", threeDSecureInfo.isLiabilityShiftPossible());

                resultMap.put("threeDSecureCard", innerMap);
            }
        }

        // Venmo
        if (paymentMethodNonce instanceof VenmoAccountNonce) {
            VenmoAccountNonce venmoAccountNonce = (VenmoAccountNonce) paymentMethodNonce;

            JSObject innerMap = new JSObject();
            innerMap.put("username", venmoAccountNonce.getUsername());

            resultMap.put("venmoAccount", innerMap);
        }

        call.resolve(resultMap);
    }
}
