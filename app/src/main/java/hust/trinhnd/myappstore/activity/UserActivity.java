package hust.trinhnd.myappstore.activity;

import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import hust.trinhnd.myappstore.R;
import hust.trinhnd.myappstore.adapter.PostAdapter;
import hust.trinhnd.myappstore.base.BaseActivity;
import hust.trinhnd.myappstore.listener.GetDataListener;
import hust.trinhnd.myappstore.model.Post;
import hust.trinhnd.myappstore.model.User;
import hust.trinhnd.myappstore.presentation.PresentationPost;
import hust.trinhnd.myappstore.presentation.PresentationUser;

public class UserActivity extends BaseActivity {

    private User user;
    private String mUid;
    private RecyclerView rcvUserPost;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private PresentationUser mPresentationUser;
    private TextView tvUsername;
    private TextView tvUserEmail;
    private PresentationPost mPresentationPost;
    private PostAdapter postAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        init();
    }

    private void init() {
        mUid = getIntent().getStringExtra("uid");
        mPresentationUser = new PresentationUser(this);
        mPresentationPost= new PresentationPost(this);
        rcvUserPost= (RecyclerView) findViewById(R.id.rcv_user_list);
        toolbar= (Toolbar) findViewById(R.id.toolbar_user);
        collapsingToolbarLayout= findViewById(R.id.collapsing_toolbar);

        tvUserEmail= findViewById(R.id.email_user);
        tvUsername= findViewById(R.id.name_user);
        mPresentationUser.getUser(mUid, new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                user= (User) object;
                tvUsername.setText(user.getName());
                tvUserEmail.setText(user.getEmail());
                configRecyclerView();
                showPostList();
            }

            @Override
            public void getDataFailure(String error) {

            }
        });

    }

    private void configRecyclerView() {
        rcvUserPost.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvUserPost.setLayoutManager(layoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        rcvUserPost.addItemDecoration(decoration);
    }

    private void showPostList() {
        showProgressDialog("Đang tải...");

        mPresentationPost.getUserPost(mUid, new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                ArrayList<Post> lstPost= (ArrayList<Post>) object;
                postAdapter= new PostAdapter(lstPost,UserActivity.this);
                postAdapter.notifyDataSetChanged();
                rcvUserPost.setAdapter(postAdapter);
                hideProgressDialog();
            }

            @Override
            public void getDataFailure(String error) {
                hideProgressDialog();
                Toast.makeText(UserActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
