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
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cn.hana.sockword.model.ThesaurusGen;

public class MainActivity extends Activity {
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


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences =  getSharedPreferences("share",Context.MODE_PRIVATE);
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
                            word_text.setTextColor(Color.GREEN);
                            english_text.setTextColor(Color.GREEN);
                            choose.get(i).setTextColor(Color.GREEN);
                            int num = sharedPreferences.getInt("alreadyStudy",0) + 1;
                            editor.putInt("alreadyStudy",num);
                            editor.commit();

                        } else {
                            int num = sharedPreferences.getInt("wrong",0) + 1;
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
                unlocked();
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

}
