package id.kakzaki.midpay;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.UIKitCustomSetting;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
//import io.flutter.plugin.common.PluginRegistry;
//import io.flutter.plugin.common.PluginRegistry.Registrar;
//import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener;


/**
 * MidpayPlugin
 */
public class MidpayPlugin implements FlutterPlugin,ActivityAware, MethodCallHandler,  TransactionFinishedCallback {
    static final String TAG = "MidpayPlugin";
    private MethodChannel channel;
    private Context context;
    private FlutterPluginBinding pluginBinding;
    private ActivityPluginBinding activityBinding;
    private Object initializationLock = new Object();
    private Application application;
    private Activity activity;

    /**
     * Plugin registration.
     */
//    public static void registerWith(Registrar registrar) {
//        final MidpayPlugin instance = new MidpayPlugin();
//        //registrar.addRequestPermissionsResultListener(instance);
//        //final MethodChannel channel = new MethodChannel(registrar.messenger(), "midpay")
//        //channel.setMethodCallHandler(new MidpayPlugin(registrar, channel));
//        Activity activity = registrar.activity();
//        Application application = null;
//        instance.setup(registrar.messenger(), application, activity, registrar, null);
//
//    }


    public MidpayPlugin() {
//        this.registrar = registrar;
//        this.channel = channel;
//        this.context = registrar.activeContext();
    }

    private void setup(
            final BinaryMessenger messenger,
            final Application application,
            final Activity activity,
//            final PluginRegistry.Registrar registrar,
            final ActivityPluginBinding activityBinding) {
        synchronized (initializationLock) {
            Log.i(TAG, "setup");
            this.activity = activity;
            this.application = application;
            this.context = application;
            channel = new MethodChannel(messenger,   "midpay");
            channel.setMethodCallHandler(this);
//            if (registrar != null) {
//                // V1 embedding setup for activity listeners.
//                registrar.addRequestPermissionsResultListener(this);
//            } else {
//                // V2 embedding setup for activity listeners.
  //              activityBinding.addRequestPermissionsResultListener(this);
//            }
        }
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        pluginBinding = flutterPluginBinding;
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        pluginBinding = null;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
        activityBinding = activityPluginBinding;
        setup(
                pluginBinding.getBinaryMessenger(),
                (Application) pluginBinding.getApplicationContext(),
                activityBinding.getActivity(),
//                null,
                activityBinding);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {
        onAttachedToActivity(activityPluginBinding);
    }

    @Override
    public void onDetachedFromActivity() {
        detach();
    }

    private void detach() {
        Log.i(TAG, "detach");
        context = null;
//        activityBinding.removeRequestPermissionsResultListener((PluginRegistry.RequestPermissionsResultListener) this);
        activityBinding = null;
        channel.setMethodCallHandler(null);
        channel = null;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "init":
                initMidtransSdk((String) call.argument("client_key").toString(), call.argument("base_url").toString());
                break;
            case "payment": {
                String str = call.arguments();
                payment(str);
                break;
            }
            case "paymentToken": {
                String str = call.arguments();
                paymentToken(str);
                break;
            }
            default:
                result.notImplemented();
                break;
        }
    }

    private void initMidtransSdk(String client_key, String base_url) {
        SdkUIFlowBuilder.init()
                .setClientKey(client_key) // client_key is mandatory
                .setContext(context)// context is mandatory
                .setTransactionFinishedCallback(this) // set transaction finish callback (sdk callback)
                .setMerchantBaseUrl(base_url) //set merchant url
                .enableLog(true) // enable sdk log
                //.setColorTheme(new CustomColorTheme("#4CAF50", "#009688", "#CDDC39")) // will replace theme on snap theme on MAP
                .buildSDK();
    }

    void payment(String str) {
        try {
            Log.d(TAG, str);
            JSONObject json = new JSONObject(str);
            JSONObject cJson = json.getJSONObject("customer");
            TransactionRequest transactionRequest = new
                    TransactionRequest(System.currentTimeMillis() + "", json.getInt("total"));
            ArrayList<ItemDetails> itemList = new ArrayList<>();
            JSONArray arr = json.getJSONArray("items");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                ItemDetails item = new ItemDetails(obj.getString("id"), obj.getInt("price"), obj.getInt("quantity"), obj.getString("name"));
                itemList.add(item);
            }
            CustomerDetails cus = new CustomerDetails();
            cus.setFirstName(cJson.getString("first_name"));
            cus.setLastName(cJson.getString("last_name"));
            cus.setEmail(cJson.getString("email"));
            cus.setPhone(cJson.getString("phone"));
            transactionRequest.setCustomerDetails(cus);
            if (json.has("custom_field_1"))
                transactionRequest.setCustomField1(json.getString("custom_field_1"));
            if (json.has("custom_field_2"))
                transactionRequest.setCustomField2(json.getString("custom_field_2"));
            if (json.has("custom_field_3"))
                transactionRequest.setCustomField3(json.getString("custom_field_3"));
            transactionRequest.setItemDetails(itemList);
            UIKitCustomSetting setting = MidtransSDK.getInstance().getUIKitCustomSetting();
            if (json.has("skip_customer"))
                setting.setSkipCustomerDetailsPages(json.getBoolean("skip_customer"));
            MidtransSDK.getInstance().setUIKitCustomSetting(setting);
            MidtransSDK.getInstance().setTransactionRequest(transactionRequest);
            MidtransSDK.getInstance().startPaymentUiFlow(context);
        } catch (Exception e) {
            Log.d(TAG, "ERROR " + e.getMessage());
        }
    }

    void paymentToken(String token) {
        try {
            Log.d(TAG, token);
            UIKitCustomSetting setting = MidtransSDK.getInstance().getUIKitCustomSetting();
            setting.setSkipCustomerDetailsPages(true);
            MidtransSDK.getInstance().setUIKitCustomSetting(setting);
            MidtransSDK.getInstance().startPaymentUiFlow(context,token);
        } catch (Exception e) {
            Log.d(TAG, "ERROR " + e.getMessage());
        }
    }

    @Override
    public void onTransactionFinished(TransactionResult transactionResult) {
        Map<String, Object> content = new HashMap<>();
        content.put("transactionCanceled", transactionResult.isTransactionCanceled());
        content.put("status", transactionResult.getStatus());
        content.put("source", transactionResult.getSource());
        content.put("statusMessage", transactionResult.getStatusMessage());
        if (transactionResult.getResponse() != null)
            content.put("response", transactionResult.getResponse().toString());
        else
            content.put("response", "null");
        channel.invokeMethod("onTransactionFinished", content);
    }

//    @Override
//    public boolean onRequestPermissionsResult(int i, String[] strings, int[] ints) {
//        return false;
//    }
}
