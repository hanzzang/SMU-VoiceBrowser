package app.mulit.smu.voice.voicewebbrowser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

class SMUChromeViewClient extends WebChromeClient {
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        AlertDialog dialog = new AlertDialog.Builder(view.getContext()).
                setMessage(message).
                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        dialog.show();
        result.confirm();
        return true;
    }
}