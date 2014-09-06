package com.hansihe.texdroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

class TexRenderServiceResultReceiver extends ResultReceiver {

    ITexRenderResponseHandler responseHandler = null;

    public TexRenderServiceResultReceiver(Handler handler) {
        super(handler);
    }

    public void setListener(ITexRenderResponseHandler listener) {
        responseHandler = listener;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (responseHandler != null) {
            String errorMessage = resultData.getString("errorMessage");
            TexEquationRendered result = resultData.getParcelable("result");

            if (errorMessage != null) {
                responseHandler.error(errorMessage);
            }
            responseHandler.result(result);
        }
    }
}