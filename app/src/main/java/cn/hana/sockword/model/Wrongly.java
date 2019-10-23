package cn.hana.sockword.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.sql.Timestamp;

public class Wrongly extends SugarRecord {
    @Unique
    private int id;
    private int qid;
    private Timestamp create_at;

    public Wrongly()
    {



    }
}
