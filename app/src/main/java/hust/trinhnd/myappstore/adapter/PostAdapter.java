package hust.trinhnd.myappstore.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import hust.trinhnd.myappstore.R;
import hust.trinhnd.myappstore.activity.PostActivity;
import hust.trinhnd.myappstore.listener.GetDataListener;
import hust.trinhnd.myappstore.model.Comment;
import hust.trinhnd.myappstore.model.Course;
import hust.trinhnd.myappstore.model.Post;
import hust.trinhnd.myappstore.model.User;
import hust.trinhnd.myappstore.presentation.PresentationComment;
import hust.trinhnd.myappstore.presentation.PresentationCourse;
import hust.trinhnd.myappstore.presentation.PresentationUser;

/**
 * Created by Trinh on 08/12/2017.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    private List<Post> lstPost;
    private Activity activity;

    public PostAdapter(List<Post> lstPost, Activity activity) {
        this.lstPost = lstPost;
        this.activity = activity;
    }

    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostHolder holder, int position) {
        final Post post = lstPost.get(position);
        holder.tvTitle.setText(post.getTitle());
        holder.tvUser.setText(post.getUid());
        SimpleDateFormat formatter = new SimpleDateFormat(activity.getResources().getString(R.string.date_format));
        holder.tvDate.setText(activity.getResources().getString(R.string.up_post_date) + formatter.format(post.getDateCreated()));
        Glide.with(activity)
                .load(post.getImage())
                .into(holder.ivPost);
        PresentationUser presentationUser = new PresentationUser(activity);
        presentationUser.getUser(post.getUid(), new GetDataListener() {
            @Override
            public void getDataSuccess(Object ob) {
                User user = (User) ob;
                holder.tvUser.setText(user.getName());
//                Glide.with(MyApplication.getInstance())
//                        .load()
//                        .into(holder.ivAvatar);
            }
            @Override
            public void getDataFailure(String error) {
                holder.tvUser.setText("#unknown");
            }
        });
        PresentationCourse presentationCourse= new PresentationCourse(activity);
        presentationCourse.getCourseById(post.getCourseId(), new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                Course course= (Course)object;
                holder.tvCourse.setText("Học phần: "+course.getName()+"\nMã học phần: "+ course.getId());
            }

            @Override
            public void getDataFailure(String error) {
                holder.tvCourse.setText("Không tìm thấy tên học phần!");
            }
        });

        PresentationComment presentationComment= new PresentationComment(activity);
        presentationComment.getCommentByPost(post.getPostId(), new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                ArrayList<Comment> lstCommnet= (ArrayList<Comment>) object;
                holder.tvNumberCmt.setText(String.valueOf(lstCommnet.size()));
            }

            @Override
            public void getDataFailure(String error) {
                Log.d("PostAdapter - count cmt", error);
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewPostIntent = new Intent(activity, PostActivity.class);
                viewPostIntent.putExtra("post", post);
                activity.startActivity(viewPostIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return lstPost == null ? 0 : lstPost.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView ivAvatar;
        ImageView ivPost;
        TextView tvUser;
        TextView tvTitle;
        TextView tvNumberCmt;
        TextView tvDate;
        TextView tvCourse;

        PostHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_post_avatar);
            ivPost = (ImageView) itemView.findViewById(R.id.iv_post);
            tvUser = (TextView) itemView.findViewById(R.id.tv_user);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvNumberCmt = (TextView) itemView.findViewById(R.id.number_comment_product_type_1);
            tvDate = (TextView) itemView.findViewById(R.id.tv_item_post_date);
            tvCourse= itemView.findViewById(R.id.tv_course_1);
        }
    }
}
