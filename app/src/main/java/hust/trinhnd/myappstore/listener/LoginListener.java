package hust.trinhnd.myappstore.listener;

/**
 * Created by Trinh on 18/11/2017.
 */

public interface LoginListener {
    void loginSuccess();
    void loginFailure(String message);
}
