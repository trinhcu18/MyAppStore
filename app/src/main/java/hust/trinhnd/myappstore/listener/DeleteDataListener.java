package hust.trinhnd.myappstore.listener;

/**
 * Created by Trinh on 16/12/2017.
 */

public interface DeleteDataListener {
    void deleteSuccess();
    void deleteFailure(String error);
}
