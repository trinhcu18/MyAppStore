package hust.trinhnd.myappstore.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hust.trinhnd.myappstore.R;
import hust.trinhnd.myappstore.activity.PostActivity;
import hust.trinhnd.myappstore.activity.UserActivity;
import hust.trinhnd.myappstore.activity.ViewCommentedPostActivity;
import hust.trinhnd.myappstore.base.BaseActivity;
import hust.trinhnd.myappstore.listener.DeleteDataListener;
import hust.trinhnd.myappstore.listener.GetDataListener;
import hust.trinhnd.myappstore.model.Comment;
import hust.trinhnd.myappstore.model.Post;
import hust.trinhnd.myappstore.model.User;
import hust.trinhnd.myappstore.presentation.PresentationComment;
import hust.trinhnd.myappstore.presentation.PresentationPost;
import hust.trinhnd.myappstore.presentation.PresentationUser;
import hust.trinhnd.myappstore.utils.SharedPreferencesUtils;

/**
 * Created by Trinh on 17/12/2017.
 */

public class CommentedPostAdapter extends RecyclerView.Adapter<CommentedPostAdapter.CommentedHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Comment> comments;
    private User user;

    public CommentedPostAdapter(Context context, ArrayList<Comment> comments, User user) {
        this.context = context;
        this.comments = comments;
        this.user = user;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public CommentedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_commented_post, parent, false);
        return new CommentedHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentedHolder holder, int position) {
        final Comment cmt = comments.get(position);
        SimpleDateFormat formatter = new SimpleDateFormat(context.getResources().getString(R.string.date_format));
        holder.tvDate.setText(context.getResources().getString(R.string.up_post_date) + formatter.format(cmt.getDateCreated()));
        holder.tvUsername.setText(user.getName());
        holder.tvContent.setText(cmt.getContent());
        final PresentationPost presentationPost= new PresentationPost(context);
        presentationPost.getPostById(cmt.getPostId(), new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                final Post post= (Post) object;
                holder.tvTitle.setText(post.getTitle());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewPostIntent = new Intent(context, PostActivity.class);
                        viewPostIntent.putExtra(context.getString(R.string.post), post);
                        context.startActivity(viewPostIntent);
                    }
                });
                PresentationUser presentationUser= new PresentationUser(context);
                presentationUser.getUser(post.getUid(), new GetDataListener() {
                    @Override
                    public void getDataSuccess(Object object) {
                        User postUser= (User) object;
                        holder.tvPostUser.setText(postUser.getName());
                    }

                    @Override
                    public void getDataFailure(String error) {

                    }
                });
            }

            @Override
            public void getDataFailure(String error) {
            }
        });
        holder.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu(context, v, cmt);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    class CommentedHolder extends RecyclerView.ViewHolder {
        View mView;
        ;
        TextView tvUsername;
        TextView tvPostUser;
        TextView tvContent;
        TextView tvTitle;
        TextView tvDate;
        CircleImageView ivUseravatar;
        CircleImageView ivPostavatar;
        ImageButton btnSettings;

        public CommentedHolder(View itemView) {
            super(itemView);
            mView = itemView;
            tvUsername = itemView.findViewById(R.id.tv_user);
            tvPostUser = itemView.findViewById(R.id.tv_post_user);
            tvContent = itemView.findViewById(R.id.tv_cmt);
            tvTitle = itemView.findViewById(R.id.tv_post_title);
            tvDate = itemView.findViewById(R.id.tv_cmt_date);
            ivPostavatar = itemView.findViewById(R.id.iv_post_user);
            ivUseravatar = itemView.findViewById(R.id.iv_cmt_ava);
            btnSettings= itemView.findViewById(R.id.btn_setting);
        }
    }

    private void popupMenu(final Context context, View btnSetting, final Comment cmt) {
        PopupMenu popup = new PopupMenu(context, btnSetting);
        popup.getMenuInflater().inflate(R.menu.popup_cmt, popup.getMenu());
        Menu popupMenu = popup.getMenu();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_cmt_item:
                        String uid= SharedPreferencesUtils.getCurrentUid(context);
                        if (!(cmt.getUserId().trim().equals(uid))) {
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                            builder.setTitle("Thông báo");
                            builder.setMessage(R.string.not_allow);
                            builder.setIcon(R.drawable.astralis_logo);
                            builder.create().show();
                        } else {
                            new AlertDialog.Builder(context)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Xác nhận đăng")
                                    .setMessage("Bạn có chắc chắn muốn xoá bình luận này?")
                                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteCmt(cmt.getCommentId());
                                        }
                                    })
                                    .setNegativeButton("Không", null)
                                    .show();
                        }
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private void deleteCmt(String commentId) {
        ((BaseActivity) context).showProgressDialog("Đang xử lý ...");
        PresentationComment presentationComment = new PresentationComment(context);
        presentationComment.deleteCmt(commentId, new DeleteDataListener() {
            @Override
            public void deleteSuccess() {
                ((BaseActivity) context).hideProgressDialog();
                Toast.makeText(context, "Đã xóa bình luận", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deleteFailure(String error) {
                ((BaseActivity) context).hideProgressDialog();
                Toast.makeText(context, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
