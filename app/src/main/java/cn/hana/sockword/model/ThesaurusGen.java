package cn.hana.sockword.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class ThesaurusGen extends SugarRecord {
    @Unique
    public int ID;
    public String word;
    public String english;
    public String china;
    public String sign;

    public ThesaurusGen(){

    }



}
