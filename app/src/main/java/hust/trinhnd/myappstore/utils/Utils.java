package hust.trinhnd.myappstore.utils;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Trinh on 28/10/2017.
 */

public class Utils {
    public static  boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            etText.requestFocus();
            etText.setError("Vui lòng điền thông tin!");
            return true;
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = true;
        String expression = "[a-zA-Z0-9._-]+@[a-z]+(\\.+[a-z]+)+";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = false;
        }
        return isValid;
    }
}