package com.hansihe.texdroid;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;


public class TexRenderService extends IntentService {

    public static Intent createTexRenderIntent(Context ctx, TexEquation tex, ITexRenderResponseHandler responseHandler) {
        Intent i = new Intent(ctx, TexRenderService.class);
        TexRenderServiceResultReceiver resultReceiver = new TexRenderServiceResultReceiver(new Handler());
        resultReceiver.setListener(responseHandler);
        i.putExtra("responseHandler", resultReceiver);
        i.putExtra("input", tex);
        return i;
    }


    private WebView executor = null;
    private boolean jsInitialized = false;

    private final Object webViewLock = new Object();
    private boolean returned = false;

    private TexEquationRendered result = null;
    private String errorMessage = null;

    private Handler mainThreadHandler = null;


    public TexRenderService() {
        super("TexRenderService");
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    public void onCreate() {
        super.onCreate();

        mainThreadHandler = new Handler(getMainLooper());

        executor = new WebView(getApplicationContext());
        executor.layout(0, 0, 0, 0);

        WebSettings settings = executor.getSettings();
        settings.setJavaScriptEnabled(true);

        executor.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void returnResult(String svg, int width, int height) {
                TexRenderService.this.result = new TexEquationRendered(svg, width, height);
                returned = true;
                synchronized (webViewLock) {
                    webViewLock.notifyAll();
                }
            }

            @JavascriptInterface
            public void returnError(String message) {
                TexRenderService.this.errorMessage = message;
                returned = true;
                synchronized (webViewLock) {
                    webViewLock.notifyAll();
                }
            }

            @JavascriptInterface
            public void initialize() {
                jsInitialized = true;
                synchronized (webViewLock) {
                    webViewLock.notifyAll();
                }
            }
        }, "AndroidServiceInterface");

        executor.loadUrl("file:///android_asset/tex_render/main.html");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        reset();

        // If the WebView is not done loading, wait for it.
        synchronized (webViewLock) {
            if (!jsInitialized) {
                try {
                    webViewLock.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }

        ResultReceiver receiver = intent.getParcelableExtra("responseHandler");
        TexEquation input = intent.getParcelableExtra("input");

        // FIXME: Find and potentially remove the place where one backslash disappears.
        String inputMarkup = input.getMarkup().replaceAll("\\\\", "\\\\\\\\");
        final String safeInput = Uri.encode(inputMarkup);

        // Start processing in JavaScript
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                executor.loadUrl("javascript:processTex(\"" + safeInput + "\");");
            }
        });

        // Wait until we have a result from the other side
        synchronized (webViewLock) {
            while (!returned) {
                try {
                    webViewLock.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }

        // Return result
        if (errorMessage != null) {
            Bundle response = new Bundle();
            response.putString("errorMessage", "Error in Javascript: " + errorMessage);
            receiver.send(0, response);

            return;
        }

        Bundle response = new Bundle();
        response.putParcelable("result", result);
        receiver.send(1, response);
    }

    private void reset() {
        result = null;
        errorMessage = null;
        returned = false;
    }

}
