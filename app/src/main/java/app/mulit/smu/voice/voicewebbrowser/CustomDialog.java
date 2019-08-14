package app.mulit.smu.voice.voicewebbrowser;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import static app.mulit.smu.voice.voicewebbrowser.MainActivity.webView;

public class CustomDialog extends Dialog implements View.OnClickListener{
    private MyDialogListener dialogListener;

    FloatingActionButton fab_choice, fab_setting, fab_bookmark, fab_transform, fab_previous, fab_home, fab_next ;

    CheckBox cb_zoom, cb_tts;
    FloatingActionButton fab_choice_ok, fab_choice_cancel;

    RadioButton rb_low, rb_old, rb_basic;
    RadioGroup radioGroup;
    LinearLayout ll_dialog;

    ListView lv_bookmark;

    FloatingActionButton fab_mode_back, fab_mode_cancel;
    FloatingActionButton fab_zoomin, fab_zoomout, fab_reversal;
    FloatingActionButton fab_play, fab_pause, fab_stop;
    EditText et_repeat, et_speed;
    Button btn_pitch_low, btn_pitch_normal, btn_pitch_high, btn_rate_low, btn_rate_normal, btn_rate_high;

    int layoutName;


    public CustomDialog(Context context, int requestName) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   //다이얼로그의 타이틀바를 없애주는 옵션입니다.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //다이얼로그의 배경을 투명으로 만듭니다.
        layoutName = requestName;
        setContentView(layoutName);     //다이얼로그에서 사용할 레이아웃입니다.



/*        fab_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"fab_choice 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                dialogListener.onMenuClicked(1);
                dismiss();   //다이얼로그를 닫는 메소드
            }
        });*/
    }

    public void setDialogListener(MyDialogListener dialogListener){
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fab_choice = (FloatingActionButton) findViewById(R.id.fab_choice);
        fab_setting = (FloatingActionButton) findViewById(R.id.fab_setting);
        fab_bookmark = (FloatingActionButton) findViewById(R.id.fab_bookmark);
        fab_transform = (FloatingActionButton) findViewById(R.id.fab_transform);
        fab_previous = (FloatingActionButton) findViewById(R.id.fab_previous);
        fab_home = (FloatingActionButton) findViewById(R.id.fab_home);
        fab_next = (FloatingActionButton) findViewById(R.id.fab_next);

        // 선택메뉴
        cb_zoom = (CheckBox) findViewById(R.id.cb_zoom);
        cb_tts = (CheckBox) findViewById(R.id.cb_tts);
        fab_choice_ok = (FloatingActionButton) findViewById(R.id.fab_choice_ok);
        fab_choice_cancel = (FloatingActionButton) findViewById(R.id.fab_choice_cancel);

        // 사용자 설정 메뉴
        rb_low = (RadioButton) findViewById(R.id.rb_low);
        rb_old = (RadioButton) findViewById(R.id.rb_old);
        rb_basic = (RadioButton) findViewById(R.id.rb_basic);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        ll_dialog = (LinearLayout)findViewById(R.id.ll_dialog);

        // 즐겨찾기
        lv_bookmark = (ListView) findViewById(R.id.lv_bookmark);

        // 모드
        fab_mode_back = (FloatingActionButton) findViewById(R.id.fab_mode_back);
        fab_mode_cancel = (FloatingActionButton) findViewById(R.id.fab_mode_cancel);

        // 확대모드
        fab_zoomin = (FloatingActionButton) findViewById(R.id.fab_zoomin);
        fab_zoomout = (FloatingActionButton) findViewById(R.id.fab_zoomout);
        fab_reversal = (FloatingActionButton) findViewById(R.id.fab_reversal);

        // 음성모드
        et_repeat = (EditText) findViewById(R.id.et_repeat);
        et_speed = (EditText) findViewById(R.id.et_speed);

        btn_pitch_low = (Button) findViewById(R.id.btn_pitch_low);
        btn_pitch_normal = (Button) findViewById(R.id.btn_pitch_normal);
        btn_pitch_high = (Button) findViewById(R.id.btn_pitch_high);

        btn_rate_low = (Button) findViewById(R.id.btn_rate_low);
        btn_rate_normal = (Button) findViewById(R.id.btn_rate_normal);
        btn_rate_high = (Button) findViewById(R.id.btn_rate_high);

        fab_play = (FloatingActionButton) findViewById(R.id.fab_play);
        fab_pause = (FloatingActionButton) findViewById(R.id.fab_pause);
        fab_stop = (FloatingActionButton) findViewById(R.id.fab_stop);


        if(layoutName==R.layout.menu_main) {
            fab_choice.setOnClickListener(this);
            fab_setting.setOnClickListener(this);
            fab_bookmark.setOnClickListener(this);
            fab_transform.setOnClickListener(this);
            fab_previous.setOnClickListener(this);
            fab_home.setOnClickListener(this);
            fab_next.setOnClickListener(this);
        } else if(layoutName==R.layout.menu_choice) {
            fab_choice_ok.setOnClickListener(this);
            fab_choice_cancel.setOnClickListener(this);
        } else if(layoutName==R.layout.menu_setting) {
            /*rb_low.setOnClickListener(radioButtonClickListener);
            rb_old.setOnClickListener(radioButtonClickListener);
            rb_basic.setOnClickListener(radioButtonClickListener);
            radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);*/
            if(rb_low.isChecked()) {
                ll_dialog.setBackgroundColor(Color.rgb(255, 0, 0));
                rb_low.setTextColor(Color.rgb(0, 0, 255));
                rb_low.setTextSize(36);
                rb_old.setTextSize(36);
                rb_basic.setTextSize(36);
            } else if(rb_old.isChecked()) {
                ll_dialog.setBackgroundColor(Color.rgb(0, 255, 0));
                rb_low.setTextColor(Color.rgb(255, 0, 0));
                rb_low.setTextSize(36);
                rb_old.setTextSize(36);
                rb_basic.setTextSize(36);
            } else if(rb_basic.isChecked()) {
                ll_dialog.setBackgroundColor(Color.rgb(0, 0, 255));
                rb_low.setTextColor(Color.rgb(0, 255, 0));
                rb_low.setTextSize(20);
                rb_old.setTextSize(20);
                rb_basic.setTextSize(20);
                dialogListener.onMenuClicked("main");
            }
            radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);
        } else if(layoutName==R.layout.menu_bookmark) {

        } else if(layoutName==R.layout.menu_zoom) {
            fab_zoomin.setOnClickListener(this);
            fab_zoomout.setOnClickListener(this);
            fab_reversal.setOnClickListener(this);
            fab_mode_back.setOnClickListener(this);
            fab_mode_cancel.setOnClickListener(this);
        } else if(layoutName==R.layout.menu_tts) {
            btn_pitch_low.setOnClickListener(this);
            btn_pitch_normal.setOnClickListener(this);
            btn_pitch_high.setOnClickListener(this);
            btn_rate_low.setOnClickListener(this);
            btn_rate_normal.setOnClickListener(this);
            btn_rate_high.setOnClickListener(this);
            fab_play.setOnClickListener(this);
            fab_pause.setOnClickListener(this);
            fab_stop.setOnClickListener(this);
            fab_mode_back.setOnClickListener(this);
            fab_mode_cancel.setOnClickListener(this);
        } else if(layoutName==R.layout.menu_zoomtts) {
            btn_pitch_low.setOnClickListener(this);
            btn_pitch_normal.setOnClickListener(this);
            btn_pitch_high.setOnClickListener(this);
            btn_rate_low.setOnClickListener(this);
            btn_rate_normal.setOnClickListener(this);
            btn_rate_high.setOnClickListener(this);
            fab_zoomin.setOnClickListener(this);
            fab_zoomout.setOnClickListener(this);
            fab_reversal.setOnClickListener(this);
            fab_play.setOnClickListener(this);
            fab_pause.setOnClickListener(this);
            fab_stop.setOnClickListener(this);
            fab_mode_back.setOnClickListener(this);
            fab_mode_cancel.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_choice:
                Toast.makeText(getContext(),"fab_choice 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                dialogListener.onMenuClicked("choice");
                webView.loadUrl("javascript:initChoice();");
                dismiss();
                break;
            case R.id.fab_setting:
                Toast.makeText(getContext(),"fab_setting 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                dialogListener.onMenuClicked("setting");
                dismiss();
                break;
            case R.id.fab_bookmark:
                Toast.makeText(getContext(),"fab_bookmark 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                dialogListener.onMenuClicked("main");
                dismiss();
                break;
            case R.id.fab_transform:
                Toast.makeText(getContext(),"fab_transform 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                dialogListener.onMenuClicked("main");
                dismiss();
                break;
            case R.id.fab_previous:
                Toast.makeText(getContext(),"fab_previous 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                ((MainActivity)MainActivity.mContext).previous();
                dialogListener.onMenuClicked("main");
                dismiss();
                break;
            case R.id.fab_home:
                Toast.makeText(getContext(),"fab_home 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                ((MainActivity)MainActivity.mContext).home();
                dialogListener.onMenuClicked("main");
                dismiss();
                break;
            case R.id.fab_next:
                Toast.makeText(getContext(),"fab_next 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                ((MainActivity)MainActivity.mContext).next();
                dialogListener.onMenuClicked("main");
                dismiss();
                break;
            case R.id.fab_choice_ok:
                if(cb_zoom.isChecked() && cb_tts.isChecked()){ // 확대음성
                    Toast.makeText(getContext(),"확대 음성 모드입니다.", Toast.LENGTH_LONG).show();
                    dialogListener.onMenuClicked("zoomtts");
                    webView.loadUrl("javascript:startTTS();");
                    webView.loadUrl("javascript:stopChoice();");
                    dismiss();
                } else if(cb_tts.isChecked() && !(cb_zoom.isChecked())){ // 음성
                    Toast.makeText(getContext(),"음성 모드입니다.", Toast.LENGTH_LONG).show();
                    dialogListener.onMenuClicked("tts");
                    webView.loadUrl("javascript:startTTS();");
                    webView.loadUrl("javascript:stopChoice();");
                    dismiss();
                } else if(!(cb_tts.isChecked()) && cb_zoom.isChecked()){ //확대
                    Toast.makeText(getContext(),"확대 모드입니다.", Toast.LENGTH_LONG).show();
                    dialogListener.onMenuClicked("zoom");
                    webView.loadUrl("javascript:stopChoice();");
                    dismiss();
                } else{
                    Toast.makeText(getContext(),"모드를 체크하세요.", Toast.LENGTH_LONG).show();
                    dialogListener.onMenuClicked("choice");
                    dismiss();
                }
                break;
            case R.id.fab_choice_cancel:
                Toast.makeText(getContext(),"fab_choice_cancel 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                dialogListener.onMenuClicked("main");
                webView.loadUrl("javascript:finalizeChoice();");
                dismiss();
                break;
            case R.id.fab_mode_back:
                Toast.makeText(getContext(),"fab_mode_back 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                dialogListener.onMenuClicked("choice");
                webView.loadUrl("javascript:initChoice();");
                dismiss();
                break;
            case R.id.fab_mode_cancel:
                Toast.makeText(getContext(),"fab_mode_cancel 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                dialogListener.onMenuClicked("main");
                webView.loadUrl("javascript:finalizeChoice();");
                dismiss();
                break;


            case R.id.btn_pitch_low:
                Toast.makeText(getContext(),"btn_pitch_low 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                ((MainActivity)MainActivity.mContext).lowPitchTTS();
                dismiss();
                break;
            case R.id.btn_pitch_normal:
                Toast.makeText(getContext(),"btn_pitch_normal 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                ((MainActivity)MainActivity.mContext).normalPitchTTS();
                dismiss();
                break;
            case R.id.btn_pitch_high:
                Toast.makeText(getContext(),"btn_pitch_high 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                ((MainActivity)MainActivity.mContext).highPitchTTS();
                dismiss();
                break;
            case R.id.btn_rate_low:
                Toast.makeText(getContext(),"btn_rate_low 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                ((MainActivity)MainActivity.mContext).lowRateTTS();
                dismiss();
                break;
            case R.id.btn_rate_normal:
                Toast.makeText(getContext(),"btn_rate_normal 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                ((MainActivity)MainActivity.mContext).normalRateTTS();
                dismiss();
                break;
            case R.id.btn_rate_high:
                Toast.makeText(getContext(),"btn_rate_high 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                ((MainActivity)MainActivity.mContext).highRateTTS();
                dismiss();
                break;

            case R.id.fab_zoomin:
                Toast.makeText(getContext(),"fab_zoomin 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                dismiss();
                break;
            case R.id.fab_zoomout:
                Toast.makeText(getContext(),"fab_zoomout 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                dismiss();
                break;
            case R.id.fab_reversal:
                Toast.makeText(getContext(),"fab_reversal 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                dismiss();
                break;
            case R.id.fab_play:
                Toast.makeText(getContext(),"fab_play 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                webView.loadUrl("javascript:startTTS();");
                dismiss();
                break;
            case R.id.fab_pause:
                Toast.makeText(getContext(),"fab_pause 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                dismiss();
                break;
            case R.id.fab_stop:
                Toast.makeText(getContext(),"fab_stop 버튼을 눌렀습니다.", Toast.LENGTH_LONG).show();
                dismiss();
                break;
        }
    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rb_low:
                    ll_dialog.setBackgroundColor(Color.rgb(255, 0, 0));
                    rb_low.setTextColor(Color.rgb(0, 0, 255));
                    rb_old.setTextColor(Color.rgb(0, 0, 255));
                    rb_basic.setTextColor(Color.rgb(0, 0, 255));
                    rb_low.setTextSize(36);
                    rb_old.setTextSize(36);
                    rb_basic.setTextSize(36);
                    dialogListener.onMenuClicked("main");
                    break;
                case R.id.rb_old:
                    ll_dialog.setBackgroundColor(Color.rgb(0, 255, 0));
                    rb_low.setTextColor(Color.rgb(255, 0, 0));
                    rb_old.setTextColor(Color.rgb(255, 0, 0));
                    rb_basic.setTextColor(Color.rgb(255, 0, 0));
                    rb_low.setTextSize(36);
                    rb_old.setTextSize(36);
                    rb_basic.setTextSize(36);
                    dialogListener.onMenuClicked("main");
                    break;
                case R.id.rb_basic:
                    ll_dialog.setBackgroundColor(Color.rgb(0, 0, 255));
                    rb_low.setTextColor(Color.rgb(0, 255, 0));
                    rb_old.setTextColor(Color.rgb(0, 255, 0));
                    rb_basic.setTextColor(Color.rgb(0, 255, 0));
                    rb_low.setTextSize(20);
                    rb_old.setTextSize(20);
                    rb_basic.setTextSize(20);
                    dialogListener.onMenuClicked("main");
                    break;
            }
        }
    };
}