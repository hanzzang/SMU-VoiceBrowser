package app.mulit.smu.voice.voicewebbrowser;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager.LayoutParams;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.MotionEvent;
import android.widget.*;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import android.view.ViewGroup;


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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    public static Context mContext;
    Intent recognizer_intent;
    SpeechRecognizer mRecognizer;
    TextToSpeech mSpeckOut;

    Button btn_url;
    Button btn_home;
    String link;
    String nowlink;
    public static WebView webView;
    EditText et_url;
    ProgressBar progressBar;

    //
    FloatingActionButton fab;
    public static String mode;
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

    public void lowPitchTTS() {
        mSpeckOut.setPitch(0.8f);         // 음성 톤을 0.5배 설정
    }

    public void normalPitchTTS() {
        mSpeckOut.setPitch(1.0f);         // 음성 톤을 기본으로 설정
    }

    public void highPitchTTS() {
        mSpeckOut.setPitch(1.3f);         // 음성 톤을 2.0배 설정
    }

    public void lowRateTTS() {
        mSpeckOut.setSpeechRate(0.8f);    // 읽는 속도를 0.5빠르기로 설정
    }

    public void normalRateTTS() {
        mSpeckOut.setSpeechRate(1.0f);    // 읽는 속도를 기본으로 설정
    }

    public void highRateTTS() {
        mSpeckOut.setSpeechRate(1.5f);    // 읽는 속도를 2.0배 빠르기로 설정
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 툴바
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mode = "main";
        mContext = this;

        btn_url = (Button) findViewById(R.id.btn_url);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_home.setOnClickListener(this);
        webView = (WebView) findViewById(R.id.webView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        et_url = (EditText) findViewById(R.id.et_url);

        loadResource(webView, LOCAL_RESOURCE);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

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
                if(mode=="main") {
                    requestName = R.layout.menu_main;
                    dialog = new CustomDialog(this, requestName);

                    setDialogSize();

                    dialog.setDialogListener(new MyDialogListener() {  // MyDialogListener 를 구현
                        @Override
                        public void onMenuClicked(String modeset){
                            mode = modeset;
                        }
                    });
                    dialog.show();
                    break;
                } else if(mode=="choice") {
                    requestName = R.layout.menu_choice;
                    dialog = new CustomDialog(this, requestName);

                    setDialogSize();

                    dialog.setDialogListener(new MyDialogListener() {  // MyDialogListener 를 구현
                        @Override
                        public void onMenuClicked(String modeset){
                            mode = modeset;
                        }
                    });
                    dialog.show();
                    break;
                } else if(mode=="backZoom") {
                    requestName = R.layout.menu_choice;
                    dialog = new CustomDialog(this, requestName);

                    setDialogSize();

                    dialog.setDialogListener(new MyDialogListener() {  // MyDialogListener 를 구현
                        @Override
                        public void onMenuClicked(String modeset){
                            mode = modeset;
                        }
                    });
                    dialog.show();
                    break;
                } else if(mode=="setting") {
                    requestName = R.layout.menu_setting;
                    dialog = new CustomDialog(this, requestName);

                    setDialogSize();

                    dialog.setDialogListener(new MyDialogListener() {  // MyDialogListener 를 구현
                        @Override
                        public void onMenuClicked(String modeset){
                            mode = modeset;
                        }
                    });
                    dialog.show();
                    break;
                } else if(mode=="zoomtts") {
                    requestName = R.layout.menu_zoomtts;
                    dialog = new CustomDialog(this, requestName);

                    setDialogSize();

                    dialog.setDialogListener(new MyDialogListener() {  // MyDialogListener 를 구현
                        @Override
                        public void onMenuClicked(String modeset){
                            mode = modeset;
                        }
                    });
                    dialog.show();
                    break;
                } else if(mode=="tts") {
                    requestName = R.layout.menu_tts;
                    dialog = new CustomDialog(this, requestName);

                    setDialogSize();

                    dialog.setDialogListener(new MyDialogListener() {  // MyDialogListener 를 구현
                        @Override
                        public void onMenuClicked(String modeset){
                            mode = modeset;
                        }
                    });
                    dialog.show();
                    break;
                } else if(mode=="zoom") {
                    requestName = R.layout.menu_zoom;
                    dialog = new CustomDialog(this, requestName);

                    setDialogSize();

                    dialog.setDialogListener(new MyDialogListener() {  // MyDialogListener 를 구현
                        @Override
                        public void onMenuClicked(String modeset){
                            mode = modeset;
                        }
                    });
                    dialog.show();
                    break;
                }
            case R.id.btn_home:
                editHome();
                break;
        }
    }

    private void setDialogSize(){
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics(); //디바이스 화면크기를 구하기위해
        int width = dm.widthPixels; //디바이스 화면 너비
        int height = dm.heightPixels; //디바이스 화면 높이

        Log.v(TAG, "width: "+width+" height: "+height);

        WindowManager.LayoutParams wm = dialog.getWindow().getAttributes();  //다이얼로그의 높이 너비 설정하기위해
        wm.copyFrom(dialog.getWindow().getAttributes());  //여기서 설정한값을 그대로 다이얼로그에 넣겠다는의미
        wm.width = (int)(width * 0.8);  //화면 너비의 절반
        /*wm.height = (int)(height * 0.8);  //화면 높이의 절반*/
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

    @Override
    protected void onStop() {
        super.onStop();
        stopTTS();
        dialog.dismiss();
        mode = "main";
        JavaScriptInterface.clearWebPageDomObject();
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

        webView.loadUrl("file:///android_asset/zoom.html");
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

        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        String homeurl = sf.getString("homeurl","");

        if(homeurl != null && homeurl.equals("")){
            homeurl = "http://www.sookmyung.ac.kr";
        }

        webView.loadUrl(homeurl);
        //nowlink = webView.getUrl();
        //et_url.setText(nowlink);
    }


    public void editHome() {
        //SharedPreferences를 sFile이름, 기본모드로 설정
        SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);

        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String urlString = webView.getUrl().toString();
        String homeurl = urlString; // 사용자가 입력한 저장할 데이터
        editor.putString("homeurl",homeurl); // key, value를 이용하여 저장하는 형태

        //최종 커밋
        editor.commit();
    }

    public void reload(){
        webView.setWebViewClient(new SMUWebVieweClient(this));
        webView.addJavascriptInterface(new JavaScriptInterface(this), "SMUJSInterface");

        webView.reload();
    }

    public void previous(){
        if(webView.canGoBack()){
            webView.goBack();
        }
        //nowlink= webView.getUrl();
        //et_url.setText(nowlink);
    }

    public void next(){
        if(webView.canGoForward()){
            webView.goForward();
        }
        //nowlink = webView.getUrl();
        //et_url.setText(nowlink);
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ArrayList<String> arrayList = new ArrayList<String>();
    ArrayList<String> bookList = new ArrayList<String>();

    //bookmark 레이아웃 겹치기
    public void layer() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RelativeLayout Rl = (RelativeLayout) inflater.inflate(R.layout.menu_bookmark, null);
        /*RelativeLayout.LayoutParams paramRl = new RelativeLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, WRAP_CONTENT);*/
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        final RelativeLayout.LayoutParams paramRl = new RelativeLayout.LayoutParams(dm.widthPixels, dm.heightPixels);
        Rl.setBackgroundColor(Color.parseColor("#99000000"));
        addContentView(Rl, paramRl);

        final String current_url = webView.getUrl();
        Button btn_add = (Button) findViewById(R.id.btn_add);
        Button btn_out = (Button) findViewById(R.id.btn_out);

        final EditText editText = (EditText) findViewById(R.id.edit_input);
        //editText.setText(current_url);
        final ListView listView = (ListView) findViewById(R.id.lv_bookmark);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.menu_bookmark_row, arrayList);
        listView.setAdapter(arrayAdapter);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().length() > 0) {
                    String inputStr = editText.getText().toString();
                    arrayList.add(inputStr);
                    bookList.add(current_url);
                    arrayAdapter.notifyDataSetChanged();

                }

            }
        });


        //클릭
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int pos, long id) {
                //해당 주소로 이동
                String selected_item = bookList.get(pos);
                loadResource(webView, selected_item);
                //즐찾 닫기
                View viewToRemove = findViewById(R.id.bookmark_root);
                ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);
            }
        });

        //닫기
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View viewToRemove = findViewById(R.id.bookmark_root);
                ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);
            }
        });

        //롱터치 삭제 및 수정
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos_del = position;
                LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final RelativeLayout Rel = (RelativeLayout) inflater2.inflate(R.layout.longclick, null);
                RelativeLayout.LayoutParams paramRel = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                Rel.setBackgroundColor(Color.parseColor("#99000000"));
                Rel.setPadding(210, 400, 210, 300);
                addContentView(Rel, paramRel);

                Button btn_modify = (Button) findViewById(R.id.btn_modify);
                Button btn_delete = (Button) findViewById(R.id.btn_delete);

                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View viewToRemove = findViewById(R.id.Long);
                        ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);
                        arrayList.remove(pos_del);
                        arrayAdapter.notifyDataSetChanged();

                    }
                });
                btn_modify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View viewToRemove = findViewById(R.id.Long);
                        ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);
                        LayoutInflater inflater3 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final RelativeLayout Rel_mod = (RelativeLayout) inflater3.inflate(R.layout.modify, null);
                        RelativeLayout.LayoutParams paramRel_mod = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                        Rel_mod.setBackgroundColor(Color.parseColor("#99000000"));
                        Rel_mod.setPadding(150, 300, 150, 300);
                        addContentView(Rel_mod, paramRel_mod);
                        Button btn_check = (Button) findViewById(R.id.btn_check);

                        final EditText editText = (EditText) findViewById(R.id.edit_mod);

                        btn_check.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String inputmod = editText.getText().toString();
                                View viewToRemove = findViewById(R.id.bookmark_mod);
                                ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);
                                int count, checked;
                                count = arrayAdapter.getCount();
                                if (count > 0) {
                                    // 현재 선택된 아이템의 position 획득.
                                    checked = pos_del;
                                    if (checked > -1 && checked < count) {
                                        // 아이템 수정
                                        arrayList.set(checked, inputmod);
                                        // listview 갱신
                                        arrayAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                        });
                    }
                });
                return true;
            }
        });
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
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

    // TTS 정지
    public void stopTTS() {
        // 음성합성 객체가 남아있다면 실행을 중지하고 메모리에서 제거
        if(mSpeckOut != null){
            mSpeckOut.stop();
        }
    }
}
