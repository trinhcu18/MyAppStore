package hust.trinhnd.myappstore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import hust.trinhnd.myappstore.R;
import hust.trinhnd.myappstore.adapter.PostAdapter;
import hust.trinhnd.myappstore.base.BaseActivity;
import hust.trinhnd.myappstore.listener.GetDataListener;
import hust.trinhnd.myappstore.model.Post;
import hust.trinhnd.myappstore.model.User;
import hust.trinhnd.myappstore.presentation.PresentationPost;
import hust.trinhnd.myappstore.presentation.PresentationUser;
import hust.trinhnd.myappstore.utils.SearchType;
import hust.trinhnd.myappstore.utils.SharedPreferencesUtils;
import hust.trinhnd.myappstore.utils.Utils;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int STATE_LASTEST = 1;
    private static final int STATE_SEARCHING = 2;

    private RecyclerView rcvHome;
    private DrawerLayout drawerLayout;
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private PostAdapter postAdapter;
    private FirebaseAuth auth;
    private PresentationUser presentationUser;
    private PresentationPost presentationPost;
    private static int lstPostState;
    private TextView tvStatus;
    private Spinner spinner;
    private RelativeLayout rlSearch;
    private ImageButton btnSearch;
    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }


    private void initViews() {

        auth = FirebaseAuth.getInstance();
        presentationUser = new PresentationUser(this);
        presentationPost = new PresentationPost(this);
        tvStatus = (TextView) findViewById(R.id.tv_home_state);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_main);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPost();
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        rlSearch = findViewById(R.id.rl_search);
        btnSearch = findViewById(R.id.btn_search);
        edtSearch = findViewById(R.id.edt_search_home_content);

        //Config search spinner
        spinner = findViewById(R.id.spinner_search);
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, SearchType.getSearchType());
        searchAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(searchAdapter);


        //Config recyclerview
        rcvHome = findViewById(R.id.recyclerHome);
        configRecyclerView();

        showPostList(STATE_LASTEST);

        showUserName();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isEmpty(edtSearch)) {
                    switch (String.valueOf(spinner.getSelectedItem())) {
                        case SearchType.TYPE_TITTLE:
                            showTitleSearch(edtSearch.getText().toString().trim());
                            break;
                        case SearchType.TYPE_COURSE:
                            showCourseSearch(edtSearch.getText().toString().trim());
                            break;
                        case SearchType.TYPE_USER:
                            showUserSearch(edtSearch.getText().toString().trim());
                            break;
                    }
                }
            }
        });

    }

    private void showUserSearch(String key) {
        showPostList(STATE_SEARCHING);
        showProgressDialog("Đang tìm");
        presentationPost.getPostByUserKey(key, new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                ArrayList<Post> lstPost = (ArrayList<Post>) object;
                postAdapter = new PostAdapter(lstPost, MainActivity.this);
                postAdapter.notifyDataSetChanged();
                rcvHome.setAdapter(postAdapter);

                hideProgressDialog();
            }

            @Override
            public void getDataFailure(String error) {
                hideProgressDialog();
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCourseSearch(String key) {
        showPostList(STATE_SEARCHING);
        showProgressDialog("Đang tìm");
        presentationPost.getPostByCourseKey(key, new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                ArrayList<Post> lstPost = (ArrayList<Post>) object;
                postAdapter = new PostAdapter(lstPost, MainActivity.this);
                postAdapter.notifyDataSetChanged();
                rcvHome.setAdapter(postAdapter);

                hideProgressDialog();
            }

            @Override
            public void getDataFailure(String error) {
                hideProgressDialog();
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTitleSearch(String key) {
        showPostList(STATE_SEARCHING);
        showProgressDialog("Đang tìm...");
        presentationPost.getPostByTitle(key, new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                ArrayList<Post> lstPost = (ArrayList<Post>) object;
                    postAdapter = new PostAdapter(lstPost, MainActivity.this);
                    postAdapter.notifyDataSetChanged();
                    rcvHome.setAdapter(postAdapter);

                hideProgressDialog();
            }

            @Override
            public void getDataFailure(String error) {
                hideProgressDialog();
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserName() {
        String uid = SharedPreferencesUtils.getCurrentUid(this);
        presentationUser.getUser(uid, new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                User user = (User) object;
                ((TextView) navigationView.findViewById(R.id.name_user)).setText(user.getName());
                ((TextView) navigationView.findViewById(R.id.email_user)).setText(user.getEmail());
            }

            @Override
            public void getDataFailure(String error) {
                ((TextView) navigationView.findViewById(R.id.name_user)).setText("#unknown");
                ((TextView) navigationView.findViewById(R.id.email_user)).setText("#unknown");
            }
        });
    }


    private void configRecyclerView() {
        rcvHome.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rcvHome.setLayoutManager(layoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        rcvHome.addItemDecoration(decoration);
    }

    private void showPostList(int state) {
        lstPostState = state;
        showProgressDialog("Đang tải...");
        switch (lstPostState) {
            case STATE_LASTEST:
                showLastestList();
                break;
            case STATE_SEARCHING:
                showSearchResult();
                break;
        }
    }

    private void showSearchResult() {
        tvStatus.setText(R.string.search_status);
    }

    private void showLastestList() {
        tvStatus.setText(R.string.latest_status);
        presentationPost.getHomePost(new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                ArrayList<Post> lstPost = (ArrayList<Post>) object;
                    postAdapter = new PostAdapter(lstPost, MainActivity.this);
                    postAdapter.notifyDataSetChanged();
                    rcvHome.setAdapter(postAdapter);

                hideProgressDialog();
            }

            @Override
            public void getDataFailure(String error) {
                hideProgressDialog();
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_search) {
            if (rlSearch.getVisibility() != View.VISIBLE) {
                rlSearch.setVisibility(View.VISIBLE);
            } else {
                rlSearch.setVisibility(View.GONE);
            }
        } else if (id == R.id.action_refresh) {
            rlSearch.setVisibility(View.GONE);
            showPostList(STATE_LASTEST);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_upload) {
            uploadPost();
        } else if (id == R.id.nav_post) {
            goToUserHome();
        } else if (id == R.id.nav_home) {
            showPostList(STATE_LASTEST);
        } else if (id == R.id.nav_comment) {
            goToCommentList();
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            auth.signOut();
            SharedPreferencesUtils.removeCurrentUid(getApplicationContext());
            Intent intentOut = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentOut);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToCommentList() {
        Intent viewCommentList = new Intent(this, ViewCommentedPostActivity.class);
        viewCommentList.putExtra(getString(R.string.userId), SharedPreferencesUtils.getCurrentUid(this));
        startActivity(viewCommentList);
    }


    private void goToUserHome() {
        Intent viewUserHomeIntent = new Intent(MainActivity.this, UserActivity.class);
        viewUserHomeIntent.putExtra("uid", SharedPreferencesUtils.getCurrentUid(this));
        startActivity(viewUserHomeIntent);
    }

    private void uploadPost() {
        Intent intentUpload = new Intent(MainActivity.this, UploadActivity.class);
        startActivity(intentUpload);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showPostList(STATE_LASTEST);
    }
}
