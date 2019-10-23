package cn.hana.sockword.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;


public class WisdomEntity extends SugarRecord {
    @Unique
    public int id;
    public String english;
    public String china;

    public WisdomEntity(){

    }


}
