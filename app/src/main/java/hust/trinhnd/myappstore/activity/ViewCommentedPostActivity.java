package hust.trinhnd.myappstore.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

import hust.trinhnd.myappstore.R;
import hust.trinhnd.myappstore.adapter.CommentedPostAdapter;
import hust.trinhnd.myappstore.base.BaseActivity;
import hust.trinhnd.myappstore.listener.GetDataListener;
import hust.trinhnd.myappstore.model.Comment;
import hust.trinhnd.myappstore.model.User;
import hust.trinhnd.myappstore.presentation.PresentationComment;
import hust.trinhnd.myappstore.presentation.PresentationUser;

public class ViewCommentedPostActivity extends BaseActivity {

    private RecyclerView rcvList;
    private User user;
    private ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_commented_post);
        init();
    }

    private void init() {
        btnBack= findViewById(R.id.btn_back_cmted);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rcvList= findViewById(R.id.rcv_commented_post);
        rcvList.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);
        rcvList.setLayoutManager(layoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        rcvList.addItemDecoration(decoration);
        showCommentedPost();
    }

    private void showCommentedPost() {
        showProgressDialog("Đang tải...");
        String uid= getIntent().getStringExtra(getString(R.string.userId));
        PresentationUser presentationUser= new PresentationUser(this);
        presentationUser.getUser(uid, new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                user= (User) object;
                PresentationComment presentationComment= new PresentationComment(ViewCommentedPostActivity.this);
                presentationComment.getCommentByUser(user.getUid(), new GetDataListener() {
                    @Override
                    public void getDataSuccess(Object object) {
                        ArrayList<Comment> comments= (ArrayList<Comment>) object;
                        CommentedPostAdapter adapter= new CommentedPostAdapter(ViewCommentedPostActivity.this,
                                comments, user);
                        adapter.notifyDataSetChanged();
                        rcvList.setAdapter(adapter);
                        hideProgressDialog();
                    }

                    @Override
                    public void getDataFailure(String error) {
                        hideProgressDialog();
                    }
                });
            }

            @Override
            public void getDataFailure(String error) {
                hideProgressDialog();
            }
        });
    }

}
