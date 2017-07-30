package id.unware.poken.httpConnection;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import id.unware.poken.domain.PokenApiBase;
import id.unware.poken.tools.Constants;
import id.unware.poken.tools.MyLog;
import id.unware.poken.tools.Utils;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Poken req callback <br/>
 * Handle SERVER Response.
 */
public abstract class MyCallback implements retrofit2.Callback {

    private static final String TAG = "MyCallback";

    @Override
    public void onResponse(Call call, Response response) {
        Utils.Log("MyCallback", "Network response code: " + response.code());
        Utils.Log("MyCallback", "Network response body: " + response.body());


        // HEADERS CONTENT
//        int headerSize = response.headers().size();
//        for (int i = 0; i < headerSize; i++) {
//            Utils.Logs('v', TAG, i + " - "
//                    .concat(response.headers().name(i))
//                    .concat(" : ")
//                    .concat(response.headers().value(i))
//            );
//        }

        if (response.isSuccessful()) {
            onSuccess(response);
        } else {
            try {
                String strErrorResponse = response.errorBody().string();
                Utils.Log("MyCallback", "Network response error body: " + strErrorResponse);
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                PokenApiBase apiBase = gson.fromJson(strErrorResponse, PokenApiBase.class);

                if (apiBase != null) {
                    String msg = apiBase.detail != null
                            ? apiBase.detail
                            : apiBase.non_field_errors.length != 0
                            ? String.valueOf(apiBase.non_field_errors[0])
                            : "Request jaringan bermasalah.";

                    this.onMessage(msg, Constants.NETWORK_CALLBACK_FAILURE);
                } else {
                    Utils.Logs('e', "MyCallback", "no poken api base");
                    this.onMessage(response.code() + ": " + response.message(), Constants.NETWORK_CALLBACK_FAILURE);
                }
            } catch (IOException | com.google.gson.JsonSyntaxException e) {
                e.printStackTrace();
            }

            Utils.Logs('w', TAG, "Response errorBody: " + String.valueOf(response.errorBody()));
            Utils.Logs('w', TAG, "Response body: " + String.valueOf(response.raw().toString()));
            Utils.Logs('w', TAG, "Raw response body: " + String.valueOf(response));

            MyLog.FabricLog(Log.ERROR, "Response failed. Body: " + String.valueOf(response.errorBody().toString()));
        }

        onFinish();
    }

    @Override
    public void onFailure(Call call, Throwable t) {

        Utils.Log(TAG, "response failed. Cause: " + String.valueOf(t.getMessage()));
        Utils.Log(TAG, "response failed. Cause: " + String.valueOf(call));
        onMessage(t.getMessage(), Constants.NETWORK_CALLBACK_FAILURE);
        onFinish();

        t.printStackTrace();
    }

    public abstract void onSuccess(Response response);

    public abstract void onMessage(String msg, int status);

    public abstract void onFinish();
}
