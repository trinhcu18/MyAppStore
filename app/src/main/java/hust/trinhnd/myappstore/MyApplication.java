package hust.trinhnd.myappstore;

import android.app.Application;

/**
 * Created by Trinh on 08/12/2017.
 */

public class MyApplication extends Application{
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
