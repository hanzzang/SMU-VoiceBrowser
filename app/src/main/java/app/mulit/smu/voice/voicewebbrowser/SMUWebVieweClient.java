package app.mulit.smu.voice.voicewebbrowser;

import android.content.Context;
import android.graphics.Bitmap;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

class SMUWebVieweClient extends WebViewClient {
    private static int refreshCount;
    private Context m_context;
    private long m_start;

    SMUWebVieweClient(Context context) {
        m_context = context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        m_start = System.currentTimeMillis();
        refreshCount++;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        long interval = System.currentTimeMillis() - m_start;
        Toast.makeText(m_context, "Loaded this webpage [" + refreshCount + "] " +
                "times in [" + interval + "] ms - HTML 분석 준비 완료", Toast.LENGTH_SHORT).show();

        ((MainActivity) m_context).mSpeckOut.speak("웹 페이지 로딩 완료", TextToSpeech.QUEUE_FLUSH, null);

        injectScriptFile(view, "js/script.js");
    }

    // 로딩되는 HTML 문서에 앱에서 정의한 자바 스크립트 코드를 추가 삽입하는 함수
    private void injectScriptFile(WebView view, String scriptFile) {
        InputStream input;
        try {
            input = m_context.getAssets().open(scriptFile);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();

            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
