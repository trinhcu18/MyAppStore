package hust.trinhnd.myappstore.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import hust.trinhnd.myappstore.R;
import hust.trinhnd.myappstore.adapter.CommentAdapter;
import hust.trinhnd.myappstore.base.BaseActivity;
import hust.trinhnd.myappstore.fragment.FragmentDialogError;
import hust.trinhnd.myappstore.listener.GetDataListener;
import hust.trinhnd.myappstore.listener.TaskUploadListener;
import hust.trinhnd.myappstore.model.Comment;
import hust.trinhnd.myappstore.model.Post;
import hust.trinhnd.myappstore.model.User;
import hust.trinhnd.myappstore.presentation.PresentationComment;
import hust.trinhnd.myappstore.presentation.PresentationPost;
import hust.trinhnd.myappstore.utils.SharedPreferencesUtils;
import hust.trinhnd.myappstore.utils.Utils;

public class ViewCommentActivity extends BaseActivity {

    private String postId;
    private Post post;
    private User postUser;
    private RecyclerView rcvComment;
    private TextView tvNoCmt;
    private Button btnCmt;
    private EditText edtCmt;
    private PresentationComment mPresentationComment;
    private ImageView ivMainUser;
    private TextView tvMainUser;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comment);
        init();
        showCommentList();
        addControl();
    }

    private void addControl() {
        btnCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isEmpty(edtCmt)) {
                    addComment(SharedPreferencesUtils.getCurrentUid(ViewCommentActivity.this),
                            postId, edtCmt.getText().toString().trim());
                }
            }
        });
    }


    private void init() {
        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra(getString(R.string.post));
        postId = post.getPostId();
        postUser = (User) intent.getSerializableExtra(getString(R.string.user));
        rcvComment = findViewById(R.id.rcv_comment);
        tvNoCmt = findViewById(R.id.tv_no_cmt);
        btnCmt = findViewById(R.id.btn_cmt);
        edtCmt = findViewById(R.id.edt_your_cmt);
        ivMainUser= findViewById(R.id.iv_post_avatar);
        tvMainUser= findViewById(R.id.tv_main_user);
        tvTitle= findViewById(R.id.tv_main_title);
        mPresentationComment = new PresentationComment(this);

        tvMainUser.setText(postUser.getName());
        tvTitle.setText(post.getTitle());

        configRecyclerView();
    }

    private void showCommentList() {
        tvNoCmt.setVisibility(View.INVISIBLE);
        showProgressDialog("Đang tải bình luận...");
        mPresentationComment.getCommentByPost(postId, new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                ArrayList<Comment> lstComment = (ArrayList<Comment>) object;
                if (lstComment.isEmpty()) {
                    tvNoCmt.setVisibility(View.VISIBLE);
                } else {
                    CommentAdapter adapter = new CommentAdapter(lstComment, ViewCommentActivity.this);
                    adapter.notifyDataSetChanged();
                    rcvComment.setAdapter(adapter);
                }
                hideProgressDialog();
            }

            @Override
            public void getDataFailure(String error) {
                tvNoCmt.setVisibility(View.VISIBLE);
                hideProgressDialog();
                Toast.makeText(ViewCommentActivity.this, error, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void configRecyclerView() {
        rcvComment.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rcvComment.setLayoutManager(layoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        rcvComment.addItemDecoration(decoration);
    }

    private void addComment(String currentUid, String postId, String text) {
        showProgressDialog("Đang gửi...");
        mPresentationComment.addComment(currentUid, postId, text, new TaskUploadListener() {
            @Override
            public void taskSuccess() {
                hideProgressDialog();
                FragmentManager fm = getFragmentManager();
                FragmentDialogError fragmentDialogError = FragmentDialogError.newInstance("Bình luận thành công !!!");
                fragmentDialogError.setOnDismissListener(new FragmentDialogError.OnDismissListener() {
                    @Override
                    public void handleDialogClose() {
                        edtCmt.setText("");
                        showCommentList();
                    }
                });
                fragmentDialogError.show(fm, "Upload comment success");
            }

            @Override
            public void taskFailure(final String error) {
                hideProgressDialog();
                FragmentManager fm = getFragmentManager();
                FragmentDialogError fragmentDialogError = FragmentDialogError.newInstance("Lỗi !!!");
                fragmentDialogError.setOnDismissListener(new FragmentDialogError.OnDismissListener() {
                    @Override
                    public void handleDialogClose() {
                        Toast.makeText(ViewCommentActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
                fragmentDialogError.show(fm, "Upload comment fail");
            }
        });
    }
}
