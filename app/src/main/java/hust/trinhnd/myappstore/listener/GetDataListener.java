package hust.trinhnd.myappstore.listener;

/**
 * Created by Trinh on 12/12/2017.
 */

public interface GetDataListener {
    void getDataSuccess(Object object);
    void getDataFailure(String error);
}
