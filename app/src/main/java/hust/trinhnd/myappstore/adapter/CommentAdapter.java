package hust.trinhnd.myappstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import hust.trinhnd.myappstore.R;
import hust.trinhnd.myappstore.activity.MainActivity;
import hust.trinhnd.myappstore.activity.UserActivity;
import hust.trinhnd.myappstore.listener.GetDataListener;
import hust.trinhnd.myappstore.model.Comment;
import hust.trinhnd.myappstore.model.User;
import hust.trinhnd.myappstore.presentation.PresentationPost;
import hust.trinhnd.myappstore.presentation.PresentationUser;
import hust.trinhnd.myappstore.utils.SharedPreferencesUtils;

/**
 * Created by Trinh on 17/12/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private List<Comment> lstComment;
    private Context context;
    private LayoutInflater inflater;

    public CommentAdapter(List<Comment> lstComment, Context context) {
        this.lstComment = lstComment;
        this.context = context;
        inflater= LayoutInflater.from(context);
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.item_comment, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentHolder holder, int position) {
        Comment comment= lstComment.get(position);
        PresentationUser presentationUser= new PresentationUser(context);
        presentationUser.getUser(comment.getUserId(), new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                final User user= (User) object;
                holder.tvName.setText(user.getName());
                holder.tvName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewUserHomeIntent = new Intent(context, UserActivity.class);
                        viewUserHomeIntent.putExtra("uid", user.getUid());
                        context.startActivity(viewUserHomeIntent);
                    }
                });
            }

            @Override
            public void getDataFailure(String error) {
                holder.tvName.setText("#unknown");
                Log.d("CommentAdapter: ", error);
            }
        });
        SimpleDateFormat formatter= new SimpleDateFormat(context.getString(R.string.date_format));
        holder.tvDate.setText(formatter.format(comment.getDateCreated()));
        holder.tvContent.setText(comment.getContent());

    }

    @Override
    public int getItemCount() {
        return lstComment==null?0:lstComment.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView ivAvatar;
        TextView tvName;
        TextView tvContent;
        TextView tvDate;
        public CommentHolder(View itemView) {
            super(itemView);
            mView= itemView;
            ivAvatar= itemView.findViewById(R.id.iv_cmt_ava);
            tvName= itemView.findViewById(R.id.tv_cmt_user);
            tvDate= itemView.findViewById(R.id.tv_cmt_date);
            tvContent= itemView.findViewById(R.id.tv_cmt);
        }
    }
}
