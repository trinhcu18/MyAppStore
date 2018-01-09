package hust.trinhnd.myappstore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Trinh on 09/12/2017.
 */

public class SharedPreferencesUtils {
    public static String getCurrentUid(Context context){
        SharedPreferences pref= context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        return pref.getString(Constants.UID_KEY,"");
    }

    public static void setCurrentUid(Context context, String uid){
        SharedPreferences pref= context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor =  pref.edit();
        editor.putString(Constants.UID_KEY, uid);
        editor.commit();
    }

    public static void removeCurrentUid(Context context){
        SharedPreferences pref= context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor= pref.edit();
        editor.remove(Constants.UID_KEY);
        editor.apply();
    }
}
