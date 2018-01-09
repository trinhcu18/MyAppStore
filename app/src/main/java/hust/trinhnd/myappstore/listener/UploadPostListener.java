package hust.trinhnd.myappstore.listener;

import hust.trinhnd.myappstore.model.Post;

/**
 * Created by Trinh on 11/12/2017.
 */

public interface UploadPostListener {
    void uploadPostSuccess(Post post);
    void uploadPostFailure(String error);
}
