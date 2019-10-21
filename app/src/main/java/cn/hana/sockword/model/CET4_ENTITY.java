package cn.hana.sockword.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.io.Serializable;

public class CET4_ENTITY extends SugarRecord implements Serializable {
    @Unique
    private int _id;
    private String WORD;
    private String ENGLISH;
    private String CHINA;
    private String SIGN;

    public CET4_ENTITY(){

    }



}
