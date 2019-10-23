package cn.hana.sockword;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.hana.sockword.Fragment.SetFragment;
import cn.hana.sockword.Fragment.StudyFragment;
import cn.hana.sockword.model.WisdomEntity;

public class HomeActivity extends Activity  implements View.OnClickListener {
    private ScreenListener screenListener;
    private StudyFragment studyFragment;
    private SetFragment setFragment;
    private SharedPreferences sharedPreferences;
    private FragmentTransaction transaction;
    private Button wrongBtn;
    private SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home_activity);
        sharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        try {
            File file = this.getDataDir();
            assets2SDCard(this, "dictionary.db", file.getPath() + "/databases/dictionary.db");
        } catch (IOException e) {
            e.printStackTrace();
        }


        wrongBtn = (Button) findViewById(R.id.wrong_btn);
        wrongBtn.setOnClickListener(this);

        screenListener = new ScreenListener(this);

        screenListener.begin(new ScreenListener.ScreenStateListener() {

            @Override
            public void onScreenOn() {
                if (sharedPreferences.getBoolean("btnTf",false)){
                    if (sharedPreferences.getBoolean("tf", false)) {
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
            @Override
            public void onScreenOff() {
                editor.putBoolean("tf", true);
                editor.commit();
                BaseApplication.destroyActivity("lockActivity");

            }

            @Override
            public void onUserPresent() {
                editor.putBoolean("tf", false);
                editor.commit();
            }
        });






    }
    public void study(View v) {
        if (studyFragment == null) {
            studyFragment = new StudyFragment();
        }
        setFragment(studyFragment);
    }
    public void set(View v) {
        if (setFragment == null) {
            setFragment = new SetFragment();
        }
        setFragment(setFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        screenListener.unregisterListener();
    }




    public void setFragment(Fragment fragment) {
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.wrong_btn:
                Intent i = new Intent(this, WrongActivity.class);
                startActivity(i);
                break;
        }
    }

    public static void assets2SDCard(Context context, String assetsFileName, String destFilePath) throws IOException {
        File trainFile = new File(destFilePath);
        if (!trainFile.exists()) {
            trainFile.createNewFile();
            FileOutputStream fos = null;
            InputStream is = null;
            try {
                is = context.getAssets().open(assetsFileName);
                byte[] bytes = new byte[1024];
                int length = 0;
                fos = new FileOutputStream(trainFile);
                while ((length = is.read(bytes)) != -1) {

                    fos.write(bytes, 0, length);

                }
                fos.flush();
            } catch (IOException e) {
                throw e;
            } finally {
                try {
                    if (is != null)
                        is.close();
                    if (fos != null) {

                        fos.close();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
