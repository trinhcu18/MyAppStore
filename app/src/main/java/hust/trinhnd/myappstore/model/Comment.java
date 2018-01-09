package hust.trinhnd.myappstore.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Trinh on 08/12/2017.
 */

public class Comment implements Serializable {
    private String commentId;
    private String userId;
    private String content;
    private String postId;
    private long dateCreated;

    public Comment() {
    }

    public Comment(String userId, String postId, String content) {
        this.userId = userId;
        this.content = content;
        this.postId = postId;
        this.dateCreated = new Date().getTime();
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
