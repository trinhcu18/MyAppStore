package hust.trinhnd.myappstore.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hust.trinhnd.myappstore.R;
import hust.trinhnd.myappstore.adapter.CourseSelectAdapter;
import hust.trinhnd.myappstore.listener.CourseListener;
import hust.trinhnd.myappstore.model.Course;
import hust.trinhnd.myappstore.presentation.PresentationCourse;

public class SelectCourseActivity extends AppCompatActivity {

    @BindView(R.id.btn_back_sl_course)
    ImageButton btnBack;
    @BindView(R.id.recyclerview_course)
    RecyclerView rcvCourse;
    private PresentationCourse presentationCourse;
    private CourseSelectAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course);
        ButterKnife.bind(this);

        initView();
        getAllCourse();
    }

    private void getAllCourse() {
        presentationCourse.getAllCourse(new CourseListener() {
            @Override
            public void getAllCourseSuccess(ArrayList<Course> list) {
                adapter = new CourseSelectAdapter(SelectCourseActivity.this, list);
                rcvCourse.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void getAllCourseFailure(String error) {
                Toast.makeText(SelectCourseActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        presentationCourse= new PresentationCourse(this);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcvCourse.setLayoutManager(layoutManager);
    }


}
