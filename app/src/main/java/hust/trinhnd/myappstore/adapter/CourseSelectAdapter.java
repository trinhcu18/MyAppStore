package hust.trinhnd.myappstore.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hust.trinhnd.myappstore.R;
import hust.trinhnd.myappstore.model.Course;

import java.util.List;


public class CourseSelectAdapter extends RecyclerView.Adapter<CourseSelectAdapter.ViewHolder> {

    private Context context;
    private final List<Course> lstCourse;

    public CourseSelectAdapter(Context context, List<Course> items) {
        this.context = context;
        lstCourse = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_course_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = lstCourse.get(position);
        holder.mIdView.setText(lstCourse.get(position).getId());
        holder.mContentView.setText(lstCourse.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.putExtra("course", holder.mItem);
                ((Activity)context).setResult(Activity.RESULT_OK, intent);
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (lstCourse == null) {
            return 0;
        }
        return lstCourse.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Course mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
