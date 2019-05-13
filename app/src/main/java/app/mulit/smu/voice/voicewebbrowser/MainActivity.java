package app.mulit.smu.voice.voicewebbrowser;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

// Import Library for Speech Recognition
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

// Import Library for Speech To Text
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import android.util.Log;

import static android.speech.tts.TextToSpeech.ERROR;
import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static Context mContext;
    Intent recognizer_intent;
    SpeechRecognizer mRecognizer;
    TextToSpeech mSpeckOut;

    Button btn_url;
    String link;
    WebView webView;
    EditText et_url;
    ProgressBar progressBar;

    //
    FloatingActionButton fab;
    //CustomDialog cd;
    int mode;
    private int requestName;
    CustomDialog dialog;

    private final int SMU_PERMISSIONS_RECORD_AUDIO = 1;

    private static final String LOCAL_RESOURCE = "file:///android_asset/local.html";

    private void initSTT() {
        // 음성 입력을 위한 앱 권한 획득
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, SMU_PERMISSIONS_RECORD_AUDIO
                );
            }
        }
        String mQuestion = "질문?";
        recognizer_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizer_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizer_intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        recognizer_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        recognizer_intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        recognizer_intent.putExtra(RecognizerIntent.EXTRA_PROMPT, mQuestion);

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(recognitionListener);
    }

    private void initTTS() {
        // TTS 엔진을 생성
        mSpeckOut = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 음성 합성 언어 선택한다.
                    mSpeckOut.setLanguage(Locale.KOREAN);

                    mSpeckOut.setPitch(1.0f);         // 음성 톤은 기본 설정 샘플
                    mSpeckOut.setSpeechRate(1.0f);    // 음성 읽기 속도는 2배 샘플
                    mSpeckOut.speak("시각장애인용 보이스 브라우져를 실행합니다.",TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        mSpeckOut.setEngineByPackageName("com.google.android.tts");
    }

    // STT 결과 Listener
    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            // 입력 준비 완료
            Log.d(TAG, "음성 입력 준비 완료");
        }

        @Override
        public void onBeginningOfSpeech() {
            // 입력 시작
            Log.d(TAG, "음성 시작");
        }

        @Override
        public void onRmsChanged(float v) {
            Log.d(TAG, "RMS 변경");
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            // 입력 오디오 버퍼 수신
            Log.d(TAG, "음성 버퍼 수신");
        }

        @Override
        public void onEndOfSpeech() {
            // 입력 완료
            Log.d(TAG, "음성 입력 완료");
        }

        @Override
        public void onError(int i) {
            Log.d(TAG, getErrorText(i));
        }

        @Override
        public void onResults(Bundle bundle) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = bundle.getStringArrayList(key);

            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);

            // 음성 인식 결과
            //recogText.setText(rs[0]);
            mSpeckOut.speak(rs[0].toString(), TextToSpeech.QUEUE_FLUSH, null);
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }

        public String getErrorText(int errorCode) {
            String message;
            switch (errorCode) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 녹음 오류";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 오류";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "불충분한 퍼미션";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 오류";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트워크 시간 초과";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "맞는 단어 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "음성 인식 서비스 이용 중";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버 오류";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "음성 입력 시간 초과";
                    break;
                default:
                    message = "Didn't understand, please try again.";
                    break;
            }
            return message;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 툴바
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // 전역변수
        mode = 0;
        mContext = this;

        btn_url = (Button) findViewById(R.id.btn_url);
        webView = (WebView) findViewById(R.id.webView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        et_url = (EditText) findViewById(R.id.et_url);

        loadResource(webView, LOCAL_RESOURCE);

/*        FloatingActionButton fab0 = (FloatingActionButton) findViewById(R.id.fab0);
        fab0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl("javascript:window.SMUJSInterface.getWebPageBodyText(document.getElementsByTagName('body')[0].innerText);");
            }
        });*/

/*        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 변경할 마크업 테그
                webView.loadUrl("javascript:replaceMathMLTagHtml();");
            }
        });*/

/*        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 변경할 마크업 태그
                webView.loadUrl("javascript:replaceTableTagHtml();");
            }
        });*/

        //
/*        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics(); //디바이스 화면크기를 구하기위해
        int width = dm.widthPixels; //디바이스 화면 너비
        int height = dm.heightPixels; //디바이스 화면 높이*/
        //

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        //
        //cd = new CustomDialog(this);


/*        WindowManager.LayoutParams wm = cd.getWindow().getAttributes();  //다이얼로그의 높이 너비 설정하기위해
        wm.copyFrom(cd.getWindow().getAttributes());  //여기서 설정한값을 그대로 다이얼로그에 넣겠다는의미
        wm.width = width / 2;  //화면 너비의 절반
        wm.height = height / 2;  //화면 높이의 절반*/
        //

/*        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode==0) { // 기본메뉴
                    Toast.makeText(getApplicationContext(), "fab 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                    cd.show();  //다이얼로그
                }
            }
        });*/

        initSTT();
        initTTS();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE))
            { webView.setWebContentsDebuggingEnabled(true); }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                if(mode==0) {
                    requestName = R.layout.menu_main;
                    dialog = new CustomDialog(this, requestName);
                    dialog.setDialogListener(new MyDialogListener() {  // MyDialogListener 를 구현
                        @Override
                        public void onMenuClicked(int menu){
                            mode = menu;
                        }
                    });
                    Toast.makeText(getApplicationContext(), "기본 메뉴 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                    dialog.show();
                    break;
                } else if(mode==1) {
                    requestName = R.layout.menu_choice;
                    dialog = new CustomDialog(this, requestName);
                    dialog.setDialogListener(new MyDialogListener() {  // MyDialogListener 를 구현
                        @Override
                        public void onMenuClicked(int menu){
                            mode = menu;
                        }
                    });
                    Toast.makeText(getApplicationContext(), "선택 메뉴 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                    dialog.show();
                    break;
                } else if(mode==2) {
                    requestName = R.layout.menu_setting;
                    dialog = new CustomDialog(this, requestName);
                    dialog.setDialogListener(new MyDialogListener() {  // MyDialogListener 를 구현
                        @Override
                        public void onMenuClicked(int menu){
                            mode = menu;
                        }
                    });
                    Toast.makeText(getApplicationContext(), "사용자 설정 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                    dialog.show();
                    break;
                } else if(mode==3) {
                    requestName = R.layout.menu_bookmark;
                    dialog = new CustomDialog(this, requestName);
                    dialog.setDialogListener(new MyDialogListener() {  // MyDialogListener 를 구현
                        @Override
                        public void onMenuClicked(int menu){
                            mode = menu;
                        }
                    });
                    Toast.makeText(getApplicationContext(), "즐겨찾기 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                    dialog.show();
                    break;
                } else if(mode==4) {
                    requestName = R.layout.menu_zoomtts;
                    dialog = new CustomDialog(this, requestName);
                    dialog.setDialogListener(new MyDialogListener() {  // MyDialogListener 를 구현
                        @Override
                        public void onMenuClicked(int menu){
                            mode = menu;
                        }
                    });
                    Toast.makeText(getApplicationContext(), "확대 음성 메뉴 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                    dialog.show();
                    break;
                } else if(mode==5) {
                    requestName = R.layout.menu_tts;
                    dialog = new CustomDialog(this, requestName);
                    dialog.setDialogListener(new MyDialogListener() {  // MyDialogListener 를 구현
                        @Override
                        public void onMenuClicked(int menu){
                            mode = menu;
                        }
                    });
                    Toast.makeText(getApplicationContext(), "음성 메뉴 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                    dialog.show();
                    break;
                } else if(mode==6) {
                    requestName = R.layout.menu_zoom;
                    dialog = new CustomDialog(this, requestName);
                    dialog.setDialogListener(new MyDialogListener() {  // MyDialogListener 를 구현
                        @Override
                        public void onMenuClicked(int menu){
                            mode = menu;
                        }
                    });
                    Toast.makeText(getApplicationContext(), "확대 메뉴 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                    dialog.show();
                    break;
                }
        }
    }

    private void loadResource(WebView wv, String resource) {
        wv.loadUrl(resource);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebChromeClient(new SMUChromeViewClient());
        wv.setWebViewClient(new SMUWebVieweClient(this));
        wv.addJavascriptInterface(new JavaScriptInterface(this), "SMUJSInterface");
    }

    @Override
    protected void onStart() {
        super.onStart();
        home();
    }

    public void click(View view) {
        link = "http://" + et_url.getText().toString();
        loadLink();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_file) {
            loadLocalPage();
        }
        if (id == R.id.action_Home) {
            home();
        }
        if (id == R.id.action_refresh) {
            refresh();
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadLink() {
        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webView.setWebChromeClient(new SMUChromeViewClient());
        webView.setWebViewClient(new SMUWebVieweClient(this));
        webSetting.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavaScriptInterface(this), "SMUJSInterface");
        webView.loadUrl(link);
    }

    // ASSET에 포함되어 있는 HTML 문서 로딩
    public void loadLocalPage() {
        webView.setWebViewClient(new SMUWebVieweClient(this));
        webView.addJavascriptInterface(new JavaScriptInterface(this), "SMUJSInterface");

        webView.loadUrl("file:///android_asset/local.html");
    }

    public void refresh() {
        //loadLink();
        webView.setWebViewClient(new SMUWebVieweClient(this));
        webView.addJavascriptInterface(new JavaScriptInterface(this), "SMUJSInterface");

        webView.loadUrl("file:///android_asset/localnomj.html");
    }

    public void home() {
        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new SMUChromeViewClient());
        webView.setWebViewClient(new SMUWebVieweClient(this));
        webView.addJavascriptInterface(new JavaScriptInterface(this), "SMUJSInterface");

        webView.loadUrl("http://www.sookmyung.ac.kr");
    }

    public void previous(){
        webView.goBack();
    }

    public void next(){
        webView.goForward();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class WebViewClient extends android.webkit.WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 음성합성 객체가 남아있다면 실행을 중지하고 메모리에서 제거
        if(mSpeckOut != null){
            mSpeckOut.stop();
            mSpeckOut.shutdown();
            mSpeckOut = null;
        }

        // 음성인식 객체가 남아 있다면 입력을 중지하고 메모리에서 제거
        if(mRecognizer != null) {
            mRecognizer.stopListening();
            mRecognizer.destroy();
            mRecognizer = null;
        }
    }
}
