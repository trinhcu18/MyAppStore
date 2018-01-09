package hust.trinhnd.myappstore.model;

import java.io.Serializable;

/**
 * Created by Trinh on 09/12/2017.
 */

public class Course implements Serializable {
    private String id;
    private String name;

    public Course() {
    }

    public Course(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
