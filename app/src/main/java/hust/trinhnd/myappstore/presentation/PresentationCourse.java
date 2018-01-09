package hust.trinhnd.myappstore.presentation;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hust.trinhnd.myappstore.listener.CourseListener;
import hust.trinhnd.myappstore.listener.GetDataListener;
import hust.trinhnd.myappstore.model.Course;
import hust.trinhnd.myappstore.model.Post;
import hust.trinhnd.myappstore.utils.Constants;

/**
 * Created by Trinh on 09/12/2017.
 */

public class PresentationCourse {
    private Context context;
    private DatabaseReference mDatabaseReference;

    public PresentationCourse(Context context) {
        this.context = context;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void getAllCourse(final CourseListener listener) {
        final ArrayList<Course> list = new ArrayList<>();
        mDatabaseReference.child(Constants.COURSES)
                .orderByChild("id")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Course course = ds.getValue(Course.class);
                            list.add(course);
                        }
                        listener.getAllCourseSuccess(list);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.getAllCourseFailure(databaseError.getMessage());
                    }
                });
    }

    public void getCourseById(String courseId, final GetDataListener listener) {
        mDatabaseReference.child(Constants.COURSES)
                .orderByChild("id")
                .equalTo(courseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Course course = ds.getValue(Course.class);
                            listener.getDataSuccess(course);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.getDataFailure(databaseError.getMessage());
                    }
                });
    }

    public void getCourseByKey(final String key, final GetDataListener getDataListener) {
        final ArrayList<Course> lstCourse = new ArrayList<>();
        DatabaseReference courseRef = mDatabaseReference.child(Constants.COURSES);
        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Course course = ds.getValue(Course.class);
                            String cName = course.getName().trim().toLowerCase();
                            String cId = course.getId().trim().toLowerCase();
                            String s3 = key.toLowerCase();
                            if (cId.contains(s3) || cName.contains(s3)) {
                                lstCourse.add(course);
                            }
                        }

                        getDataListener.getDataSuccess(lstCourse);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        getDataListener.getDataFailure(databaseError.getMessage());
                    }
                });

    }
}
