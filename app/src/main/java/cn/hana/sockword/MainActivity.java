package cn.hana.sockword;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.orm.query.Condition;
import com.orm.query.Select;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import cn.hana.sockword.model.ThesaurusGen;
import cn.hana.sockword.model.Wrongly;

public class MainActivity extends Activity implements SynthesizerListener {
    private int allNum,nowNum = 0,title_id ;
    private boolean isRight;
    private SpeechSynthesizer speechSynthesizer;
    private TextView time_text, date_text;
    private TextView word_text, english_text;
    private float x1,y1,x2,y2;
    private SharedPreferences sharedPreferences;
    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    private SharedPreferences.Editor editor;
    private RadioGroup choose_group;
    private int choose_id[] = {0, 1, 2, 3};
    private List<RadioButton> choose = new ArrayList<RadioButton>();


    private void init() {
        BaseApplication.addDestroyActiivty(this,"lockActivity");

        findViewById(R.id.play_vioce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = word_text.getText().toString();

                startSpeaking(text);
            }
        });

        time_text = findViewById(R.id.time_text);
        date_text = findViewById(R.id.date_text);

        word_text = findViewById(R.id.word_text);
        english_text = findViewById(R.id.english_text);

        choose_group = findViewById(R.id.choose_group);

        km = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);

        kl = km.newKeyguardLock("unlock");

        for (int i : choose_id) {
            try {
                choose_id[i] = R.id.class.getField("choose_" + i).getInt(null);
                choose.add((RadioButton) findViewById(choose_id[i]));

            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        setParam();


    }

    public void startSpeaking(String text)
    {
        speechSynthesizer.startSpeaking(text, this);
    }

    public void setParam() {
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5ba25989");
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(this,null);
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        speechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
        speechSynthesizer.setParameter(SpeechConstant.VOLUME, "50");
        speechSynthesizer.setParameter(SpeechConstant.PITCH, "50");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences =  getSharedPreferences("share",Context.MODE_PRIVATE);

        allNum = sharedPreferences.getInt("allNum", 2);

        editor = sharedPreferences.edit();


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        init();
        new Thread() {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat time = new SimpleDateFormat("HH:mm");
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.CHINA);
            @Override
            public void run() {
                while (true) {
                    Calendar calendar = Calendar.getInstance();
                    Date startDate = calendar.getTime();
                    time_text.setText(time.format(startDate));
                    date_text.setText(dateFormat.format(startDate));
                    try {
                        Thread.sleep(1000 * (60 - startDate.getSeconds()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        choose_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < 4; i++) {
                    if (choose_id[i] == checkedId) {
                        //Log.d("Debug", String.valueOf(choose.get(i).getText()));
                        if ((boolean) choose.get(i).getTag()) {
                            Wrongly wrongly = Select.from(Wrongly.class).where(Condition.prop("qid").eq(title_id)).first();
                            if(wrongly != null)
                            {
                                Wrongly.delete(wrongly);
                            }
                            nowNum++;
                            isRight = true;
                            word_text.setTextColor(Color.GREEN);
                            english_text.setTextColor(Color.GREEN);
                            choose.get(i).setTextColor(Color.GREEN);
                            int num = sharedPreferences.getInt("alreadyStudy",0) + 1;
                            editor.putInt("alreadyStudy",num);
                            editor.commit();

                        } else {
                            int num = sharedPreferences.getInt("wrong",0) + 1;

                            Wrongly wrongly = Select.from(Wrongly.class).where(Condition.prop("qid").eq(title_id)).first();
                            if(wrongly == null)
                            {
                                wrongly = new Wrongly(title_id);
                                Wrongly.save(wrongly);
                            }

                            editor.putInt("wrong",num);
                            editor.commit();

                            word_text.setTextColor(Color.RED);
                            english_text.setTextColor(Color.RED);
                            choose.get(i).setTextColor(Color.RED);
                        }
                    }
                }
            }
        });
        getNextData();
    }
    public void getNextData() {

        isRight = false;

        word_text.setTextColor(Color.WHITE);
        english_text.setTextColor(Color.WHITE);
        for (RadioButton radioButton : choose)
        {
            radioButton.setTextColor(Color.WHITE);
        }

        Random ra = new Random();
        int flag = ra.nextInt(20);
        int[] ints = spanNum(4, 20, flag);
        ThesaurusGen title = ThesaurusGen.findById(ThesaurusGen.class, flag);

        title_id = flag;

        word_text.setText(title.word);
        english_text.setText(title.english);
        flag = ra.nextInt(4);
        for (int i = 0; i < 4; i++) {
            String str = String.valueOf((char) ('A' + i)) + ":";
            if (i == flag) {
                RadioButton correct = choose.get(i);
                correct.setTag(true);
                correct.setText(str + title.china);
                Log.d("Debug", String.valueOf(correct.getText() + "Id:" + i));
            } else {
                ThesaurusGen temp = ThesaurusGen.findById(ThesaurusGen.class, ints[i]);
                RadioButton error = choose.get(i);
                error.setText(str + temp.china);
                error.setTag(false);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            x1 = event.getX();
            y1 = event.getY();

        }
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            x2 = event.getX();
            y2 = event.getY();

            if(y1 - y2 > 200) // 向上滑
            {
                int num = sharedPreferences.getInt("alreadyMastered",0) + 1;
                editor.putInt("alreadyMastered",num);
                editor.commit();

                Toast.makeText(this, "已掌握", Toast.LENGTH_SHORT).show();
                getNextData();

            }else if(y2 -y1> 200) //向下滑
            {
                Toast.makeText(this, "代加功能", Toast.LENGTH_SHORT).show();
            }
            else  if(x1 - x2 > 200)//向左划
            {
                getNextData();
            }
            else  if(x2 - x1 > 200)//向右划
            {
                if(nowNum >= allNum){
                    unlocked();
                }else {
                    Toast.makeText(this, "解锁需要 " + (allNum - nowNum) + "道" , Toast.LENGTH_SHORT).show();
                }

            }
        }
        return super.onTouchEvent(event);
    }

    private void unlocked() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        kl.disableKeyguard();
        finish();
    }

    public int[] spanNum(int length, int maxValue, int Num) {
        int[] temp = new int[length];
        for (int i = 0; i < temp.length; i++) {
            Random ra = new Random();
            if (Num != i) {
                temp[i] = ra.nextInt(maxValue);
                if (i == length) {
                    break;
                }
            }
        }
        return temp;
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {

    }

    @Override
    public void onCompleted(SpeechError speechError) {

    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }
}
