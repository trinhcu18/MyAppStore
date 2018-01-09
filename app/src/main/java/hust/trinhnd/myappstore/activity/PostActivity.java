package hust.trinhnd.myappstore.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import hust.trinhnd.myappstore.R;
import hust.trinhnd.myappstore.base.BaseActivity;
import hust.trinhnd.myappstore.listener.DeleteDataListener;
import hust.trinhnd.myappstore.listener.GetDataListener;
import hust.trinhnd.myappstore.model.Course;
import hust.trinhnd.myappstore.model.Post;
import hust.trinhnd.myappstore.model.User;
import hust.trinhnd.myappstore.presentation.PresentationCourse;
import hust.trinhnd.myappstore.presentation.PresentationPost;
import hust.trinhnd.myappstore.presentation.PresentationUser;
import hust.trinhnd.myappstore.utils.SharedPreferencesUtils;

public class PostActivity extends BaseActivity {

    private static final int COMPLETE_NOTIFICATION = 455;
    private static final int REQUEST_PERMISSIONS = 69;
    private static final int REQUEST_OPEN_FILE = 1;
    @BindView(R.id.btn_back_post)
    ImageButton btnBack;
    @BindView(R.id.tv_post_title)
    TextView tvPostTitle;
    @BindView(R.id.tv_post_desc)
    TextView tvPostDesc;
    @BindView(R.id.iv_post)
    ImageView ivPost;
    @BindView(R.id.iv_profile_photo)
    CircleImageView ivProfilePhoto;
    @BindView(R.id.btn_download)
    Button btnDownload;
    @BindView(R.id.tv_filename)
    TextView tvFileName;
    @BindView(R.id.btn_view_cmt)
    Button btnCmt;
    @BindView(R.id.tv_post_user)
    TextView tvPostUser;
    @BindView(R.id.tv_post_date)
    TextView tvPostDate;
    @BindView(R.id.btn_setting)
    ImageButton btnSetting;
    @BindView(R.id.tv_course_2)
    TextView tvCourse;

    private Post post;
    private User postUser;
    private PresentationUser mPresentationUser;
    private DownloadManager mgr;
    private BroadcastReceiver onComplete;
    private long lastDownload;
    private PresentationCourse mPresentationCourse;
    private boolean isImageFitToScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        lastDownload = -1L;
        initBroadcast();
        mgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        init();
    }


    private void initBroadcast() {
        onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideProgressDialog();
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(PostActivity.this)
                                .setSmallIcon(R.drawable.astralis_logo)
                                .setContentTitle(getResources().getString(R.string.app_name))
                                .setContentText("Download completed");
                Intent fileOpenIntent = new Intent(Intent.ACTION_VIEW);
                fileOpenIntent.setDataAndType(mgr.getUriForDownloadedFile(lastDownload),
                        mgr.getMimeTypeForDownloadedFile(lastDownload));
                fileOpenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent piFileOpen = PendingIntent.getActivity(getApplicationContext(), REQUEST_OPEN_FILE, fileOpenIntent, 0);
                mBuilder.setContentIntent(piFileOpen);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(COMPLETE_NOTIFICATION, mBuilder.build());
            }
        };

    }

    @OnClick({R.id.btn_back_post, R.id.btn_view_cmt, R.id.btn_download, R.id.btn_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back_post:
                finish();
                break;
            case R.id.btn_download:
                PostActivity.super.requestAppPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        R.string.runtime_permissions_code,
                        REQUEST_PERMISSIONS);
                break;
            case R.id.btn_setting:
                popupMenu();
                break;
            case R.id.btn_view_cmt:
                Intent viewCmtIntent = new Intent(this, ViewCommentActivity.class);
                viewCmtIntent.putExtra(getString(R.string.post), post);
                viewCmtIntent.putExtra(getString(R.string.user), postUser);
                startActivity(viewCmtIntent);
                break;

        }
    }

    private void popupMenu() {
        PopupMenu popup = new PopupMenu(this, btnSetting);
        popup.getMenuInflater().inflate(R.menu.popup_post, popup.getMenu());
        Menu popupMenu = popup.getMenu();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.view_profile_item:
                        Intent viewUserHomeIntent = new Intent(PostActivity.this, UserActivity.class);
                        viewUserHomeIntent.putExtra("uid", postUser.getUid());
                        startActivity(viewUserHomeIntent);
                        break;
                    case R.id.delete_post_item:
                        String uid= SharedPreferencesUtils.getCurrentUid(PostActivity.this);
                        if (!(post.getUid().trim().equals(uid))) {
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(PostActivity.this);
                            builder.setTitle("Thông báo");
                            builder.setMessage(R.string.not_allow);
                            builder.setIcon(R.drawable.astralis_logo);
                            builder.create().show();
                        } else {
                            new AlertDialog.Builder(PostActivity.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Xác nhận đăng")
                                    .setMessage("Bạn có chắc chắn muốn xoá bài đăng này?")
                                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deletePost();
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

    private void deletePost() {

        showProgressDialog("Đang xử lý ...");
        PresentationPost presentationPost = new PresentationPost(PostActivity.this);
        presentationPost.deletePost(post.getPostId(), new DeleteDataListener() {
            @Override
            public void deleteSuccess() {
                hideProgressDialog();
                Toast.makeText(getApplicationContext(), "Đã xóa bài đăng", Toast.LENGTH_SHORT).show();
                PostActivity.this.finish();
            }

            @Override
            public void deleteFailure(String error) {
                hideProgressDialog();
                Toast.makeText(getApplicationContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadFile(String url) {
        showProgressDialog("Đang tải...");
        Uri uri = Uri.parse(url);
        Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .mkdirs();

        lastDownload =
                mgr.enqueue(new DownloadManager.Request(uri)
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setDescription(post.getFileName())
                        .setVisibleInDownloadsUi(true)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                post.getFileName()));
    }

    private void init() {
        Intent intent = getIntent();
        btnDownload.setClickable(false);
        post = (Post) intent.getSerializableExtra("post");
        mPresentationUser = new PresentationUser(this);
        mPresentationUser.getUser(post.getUid(), new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                postUser = (User) object;
                setValueToView();
            }

            @Override
            public void getDataFailure(String error) {
                Toast.makeText(PostActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
        mPresentationCourse = new PresentationCourse(this);
        mPresentationCourse.getCourseById(post.getCourseId(), new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                Course course = (Course) object;
                tvCourse.setText("Học phần: " + course.getName() + "\nMã học phần: " + course.getId());
            }

            @Override
            public void getDataFailure(String error) {
                tvCourse.setText("Không tìm thấy tên học phần!");
            }
        });
    }

    private void setValueToView() {
        btnDownload.setClickable(true);
        tvPostUser.setText(postUser.getName());
        tvPostUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewUserHomeIntent = new Intent(PostActivity.this, UserActivity.class);
                viewUserHomeIntent.putExtra("uid", postUser.getUid());
                startActivity(viewUserHomeIntent);
            }
        });
        tvPostTitle.setText(post.getTitle());
        SimpleDateFormat formatter = new SimpleDateFormat(getResources().getString(R.string.date_format));
        tvPostDate.setText(getResources().getString(R.string.up_post_date) + formatter.format(new Date(post.getDateCreated())));
        tvPostDesc.setText(post.getContent());
        tvFileName.setText(post.getFileName());
        Glide.with(this).asBitmap().load(post.getImage()).into(ivPost);
        ivPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullScreenIntent = new Intent(PostActivity.this, FullscreenImgActivity.class);
                fullScreenIntent.setData(Uri.parse(post.getImage()));
                startActivity(fullScreenIntent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        super.onPermissionGranted(requestCode);
        downloadFile(post.getLink());
    }

}
