package cn.hana.sockword;

import android.app.Activity;
import android.app.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseApplication extends Application {

    //创建一个Map的集合用来把activity加到这个map里面
    private static Map<String, Activity> destroyMap = new HashMap<>();

    public static void addDestroyActiivty(Activity activity, String activityName) {
        destroyMap.put(activityName, activity);
    }

    public static void destroyActivity(String activityName) {
        Set<String> keySet = destroyMap.keySet();
        for (String key : keySet) {
            destroyMap.get(key).finish();
        }
    }
}
