package cn.hana.sockword;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView time_text,date_text;
    private TextView word_text,english_text;
    private RadioGroup choose_group;
    private int choose_id[] = { 0 , 1 ,2 ,3};
    private  List<RadioButton> choose  = new ArrayList<RadioButton>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        time_text = findViewById(R.id.time_text);
        date_text = findViewById(R.id.date_text);

        word_text = findViewById(R.id.word_text);
        english_text = findViewById(R.id.english_text);

        choose_group = findViewById(R.id.choose_group);

        for (int i : choose_id)
        {
            try {
                choose_id[i] =  R.id.class.getField("choose_" + i).getInt(null);
                choose.add((RadioButton)findViewById(choose_id[i]));

            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        new Thread(){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat time = new SimpleDateFormat("HH:mm");
            DateFormat dateFormat=DateFormat.getDateInstance(DateFormat.FULL,Locale.CHINA);

            @Override
            public void run() {
                while (true)
                {
                    Calendar calendar =  Calendar.getInstance();
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
                for (int i = 0 ;  i < 4 ; i ++)
                {
                    if(choose_id[i] == checkedId)
                    {
                        word_text.setTextColor(Color.GREEN);
                        english_text.setTextColor(Color.GREEN);
                        choose.get(i).setTextColor(Color.GREEN);
                    }
                    else
                    {
                        word_text.setTextColor(Color.RED);
                        english_text.setTextColor(Color.RED);
                        choose.get(i).setTextColor(Color.RED);

                    }
                }
            }
        });
    }
}
