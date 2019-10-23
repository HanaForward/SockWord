package cn.hana.sockword.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import java.util.Random;
import cn.hana.sockword.R;
import cn.hana.sockword.model.WisdomEntity;

public class StudyFragment extends Fragment {
    private TextView difficultyTv,wisdomEnglish,wisdomChina,alreadyStudyText, alreadyMasteredText, wrongText;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.study_fragment_layhout, null);
        sharedPreferences = getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);
        difficultyTv = (TextView) view.findViewById(R.id.difficulty_text);
        wisdomEnglish = (TextView) view.findViewById(R.id.wisdom_english);
        wisdomChina = (TextView) view.findViewById(R.id.wisdom_china);
        alreadyMasteredText = (TextView) view.findViewById(R.id.already_mastered);
        alreadyStudyText = (TextView) view.findViewById(R.id.already_study);
        wrongText = (TextView) view.findViewById(R.id.wrong_text);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        difficultyTv.setText(sharedPreferences.getString("difficulty", "四级") + "英语");
        Random random = new Random();
        int i = random.nextInt(10);
        WisdomEntity wisdom_entities = WisdomEntity.findById(WisdomEntity.class,i);
        wisdomEnglish.setText(wisdom_entities.english);
        wisdomChina.setText(wisdom_entities.china);
        setText();
    }

    private void setText() {
        alreadyMasteredText.setText(sharedPreferences.getInt("alreadyMastered",0)+"");
        alreadyStudyText.setText(sharedPreferences.getInt("alreadyStudy",0)+"");
        wrongText.setText(sharedPreferences.getInt("wrong",0)+"");
    }
}
