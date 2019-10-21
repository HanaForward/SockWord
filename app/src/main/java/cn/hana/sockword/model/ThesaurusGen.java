package cn.hana.sockword.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class ThesaurusGen extends SugarRecord {
    @Unique
    private int _id;

    private String WORD;
    private String ENGLISH;
    private String CHINA;
    private String SIGN;

    public ThesaurusGen(){

    }



}
