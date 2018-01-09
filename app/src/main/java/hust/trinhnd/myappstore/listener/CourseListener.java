package hust.trinhnd.myappstore.listener;

import java.util.ArrayList;

import hust.trinhnd.myappstore.model.Course;

/**
 * Created by Trinh on 11/12/2017.
 */

public interface CourseListener {
    void getAllCourseSuccess(ArrayList<Course> list);
    void getAllCourseFailure(String error);
}
