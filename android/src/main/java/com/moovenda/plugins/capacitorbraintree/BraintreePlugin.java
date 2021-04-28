package com.moovenda.plugins.capacitorbraintree;

import android.content.Intent;
import android.app.Activity;
import android.os.Parcelable;
import android.util.Log;

import androidx.activity.result.ActivityResult;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.ThreeDSecureInfo;
import com.braintreepayments.api.models.ThreeDSecurePostalAddress;
import com.braintreepayments.api.models.ThreeDSecureRequest;
import com.braintreepayments.api.models.ThreeDSecureAdditionalInformation;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.braintreepayments.cardform.view.CardForm;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.BridgeFragment;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;

import com.braintreepayments.api.models.PaymentMethodNonce;

@CapacitorPlugin(
        name = "Braintree"
        //requestCodes = {
        //        BraintreePlugin.DROP_IN_REQUEST
        //}
)
public class BraintreePlugin extends Plugin {

   private String clientToken;
   private BraintreeFragment mBraintreeFragment;

    /**
     * Logger tag
     */
    private static final String PLUGIN_TAG = "Braintree";

    static final String EXTRA_PAYMENT_RESULT = "payment_result";
    static final String EXTRA_DEVICE_DATA = "device_data";
    //static final String EXTRA_COLLECT_DEVICE_DATA = "collect_device_data";
    private String deviceData = "";

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
    public void getDeviceData(PluginCall call) {
        String merchantId = call.getString("merchantId");

        if (merchantId == null) {
            call.reject("A Merchant ID is required.");
            return;
        }
        try {
           JSObject deviceDataMap = new JSObject(this.deviceData);
            call.resolve(deviceDataMap);
        } catch (JSONException e) {
            call.reject("Cannot get device data");
        }
    }

    @PluginMethod()
    public void setToken(PluginCall call) throws InvalidArgumentException {
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
        ThreeDSecurePostalAddress address = new ThreeDSecurePostalAddress()
            .givenName(call.getString("givenName")) // ASCII-printable characters required, else will throw a validation error
            .surname(call.getString("surname")) // ASCII-printable characters required, else will throw a validation error
            .phoneNumber(call.getString("phoneNumber"))
            .streetAddress(call.getString("streetAddress"))
            .locality(call.getString("locality"))
            .postalCode(call.getString("postalCode"))
            .countryCodeAlpha2(call.getString("countryCodeAlpha2"));
        ThreeDSecureAdditionalInformation additionalInformation = new ThreeDSecureAdditionalInformation()
           .shippingAddress(address);
        ThreeDSecureRequest threeDSecureRequest = new ThreeDSecureRequest()
            .amount(call.getString("amount"))
            .email(call.getString("email"))
            .billingAddress(address)
            .versionRequested(ThreeDSecureRequest.VERSION_2)
            .additionalInformation(additionalInformation);
        DropInRequest dropInRequest = new DropInRequest()
            .clientToken(this.clientToken)
            .cardholderNameStatus(CardForm.FIELD_REQUIRED)
            .requestThreeDSecureVerification(true)
            .collectDeviceData(true)
            .threeDSecureRequest(threeDSecureRequest);
        Intent intent = dropInRequest.getIntent(getContext());

        Log.d(PLUGIN_TAG, "showDropIn started");

        startActivityForResult(call, intent, "dropinCallback");
    }

    @ActivityCallback
    protected void dropinCallback(PluginCall call, ActivityResult activityResult) {
        Intent data = activityResult.getData();

        Log.d(PLUGIN_TAG, "dropinCallback. Result code: "+activityResult.getResultCode()+", intent: "+data);

        if (call == null) {
            return;
        }

        if (activityResult.getResultCode() == Activity.RESULT_CANCELED) {
            DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
            call.resolve(handleCanceled(result.getDeviceData()));
        }


        if (activityResult.getResultCode() == Activity.RESULT_OK) {
            DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
            call.resolve(handleNonce(result.getPaymentMethodNonce(), result.getDeviceData()));
        } else {
            Exception ex = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            String msg = ex.getMessage();
            Log.e(PLUGIN_TAG, "Error: "+msg);
            call.reject(msg, ex);
        }
    }

    /**
     *
     * @param deviceData device info (not used in context)
     */
    private JSObject handleCanceled(String deviceData) {
        Log.d(PLUGIN_TAG, "handleNonce");

        JSObject resultMap = new JSObject();
        resultMap.put("cancelled", true);
        resultMap.put("deviceData", deviceData);
        return resultMap;
    }

    /**
     * Helper used to return a dictionary of values from the given payment method nonce.
     * Handles several different types of nonces (eg for cards, PayPal, etc).
     *
     * @param paymentMethodNonce The nonce used to build a dictionary of data from.
     * @param deviceData Device info
     */
    private JSObject handleNonce(PaymentMethodNonce paymentMethodNonce, String deviceData) {
        Log.d(PLUGIN_TAG, "handleNonce");

        JSObject resultMap = new JSObject();

        resultMap.put("nonce", paymentMethodNonce.getNonce());
        resultMap.put("type", paymentMethodNonce.getTypeLabel());
        resultMap.put("localizedDescription", paymentMethodNonce.getDescription());
        this.deviceData = deviceData;
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

        return resultMap;
    }
}
