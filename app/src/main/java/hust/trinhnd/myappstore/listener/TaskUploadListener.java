package hust.trinhnd.myappstore.listener;

/**
 * Created by Trinh on 11/12/2017.
 */

public interface TaskUploadListener {
    void taskSuccess();
    void taskFailure( String error);
}
