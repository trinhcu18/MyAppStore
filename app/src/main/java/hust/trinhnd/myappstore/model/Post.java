package hust.trinhnd.myappstore.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Trinh on 08/12/2017.
 */

@IgnoreExtraProperties
public class Post implements Serializable {

    private String postId;
    private String uid;
    private String title;
    private String content;
    private String image;
    private String link;
    private String fileName;
    private String courseId;
    private long dateCreated;

    public Post(){

    }

    public Post(String postId, String uid, String title, String content, String image, String link,String fileName, String courseId) {
        this.postId= postId;
        this.uid = uid;
        this.title = title;
        this.content = content;
        this.image = image;
        this.link = link;
        this.fileName= fileName;
        this.courseId = courseId;
        this.dateCreated = new Date().getTime();
    }

    public Post(String uid, String title, String desc, String courseId) {
        this.uid= uid;
        this.title= title;
        this.content= desc;
        this.courseId= courseId;
        this.dateCreated= new Date().getTime();
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public long getDateCreated() {
            return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
