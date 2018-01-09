package hust.trinhnd.myappstore.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hust.trinhnd.myappstore.R;
import hust.trinhnd.myappstore.base.BaseActivity;
import hust.trinhnd.myappstore.fragment.FragmentDialogError;
import hust.trinhnd.myappstore.listener.UploadPostListener;
import hust.trinhnd.myappstore.model.Course;
import hust.trinhnd.myappstore.model.Post;
import hust.trinhnd.myappstore.presentation.PresentationPost;
import hust.trinhnd.myappstore.utils.SharedPreferencesUtils;
import hust.trinhnd.myappstore.utils.Utils;

public class UploadActivity extends BaseActivity {

    private static final int REQUEST_CODE_IMG = 1;
    private static final int REQUEST_CODE_PDF = 2;
    private static final int REQUEST_CODE_COURSE = 3;
    @BindView(R.id.up_post_back)
    ImageButton btnBack;
    @BindView(R.id.image_up_post)
    ImageView imgPost;
    @BindView(R.id.tv_title_up_post)
    EditText edtTitle;
    @BindView(R.id.tv_desc_up_post)
    EditText edtDesc;
    @BindView(R.id.button_get_category)
    Button btnCategory;
    @BindView(R.id.btn_choose_file)
    Button btnChooseFile;
    @BindView(R.id.btn_up_post)
    Button btnUpPost;
    @BindView(R.id.tv_filename)
    TextView tvFileName;

    private String title;
    private String desc;
    private String courseId;
    private String uid;
    private Uri filePost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);
        initViews();
    }


    @OnClick({R.id.up_post_back, R.id.button_get_category, R.id.btn_choose_file, R.id.btn_up_post, R.id.image_up_post})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.up_post_back:
                finish();
                break;
            case R.id.image_up_post:
                goToSelectImage();
                break;
            case R.id.button_get_category:
                goToSelectCourse();
                break;
            case R.id.btn_choose_file:
                goToSelectFile();
                break;
            case R.id.btn_up_post:
                if (checkInputData()) {
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Xác nhận đăng")
                            .setMessage("Bạn có chắc chắn muốn đăng?")
                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadPost();
                                }
                            })
                            .setNegativeButton("Không", null)
                            .show();
                }
                break;
        }
    }

    private void goToSelectFile() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for filePost chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_PDF);
    }

    private void uploadPost() {
        showProgressDialog("Vui lòng đợi");
        imgPost.setDrawingCacheEnabled(true);
        imgPost.buildDrawingCache();
        Bitmap bitmap = imgPost.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] dataImg = baos.toByteArray();

        uid = SharedPreferencesUtils.getCurrentUid(this.getApplicationContext());
        PresentationPost presentationPost = new PresentationPost(this);
        presentationPost.addPost(uid, title, desc, dataImg, courseId, filePost, new UploadPostListener() {
            @Override
            public void uploadPostSuccess(final Post post) {
                hideProgressDialog();
                FragmentManager fm = getFragmentManager();
                FragmentDialogError fragmentDialogError = FragmentDialogError.newInstance("Đăng bài thành công !!!");
                fragmentDialogError.setOnDismissListener(new FragmentDialogError.OnDismissListener() {
                    @Override
                    public void handleDialogClose() {
                        Intent postIntent= new Intent(UploadActivity.this, PostActivity.class);
                        postIntent.putExtra("post",post);
                        startActivity(postIntent);
                        finish();
                    }
                });
                fragmentDialogError.show(fm, "Upload post success");
            }

            @Override
            public void uploadPostFailure(String error) {
                hideProgressDialog();
                FragmentManager fm = getFragmentManager();
                FragmentDialogError fragmentDialogError = FragmentDialogError.newInstance("Đăng bài không thành công !!!\nLỗi: "+error);
                fragmentDialogError.show(fm, "Upload post fail");
            }
        });
    }

    private void goToSelectImage() {
        Intent intentImg = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentImg, REQUEST_CODE_IMG);
    }

    private void goToSelectCourse() {
        Intent intentCourse= new Intent(UploadActivity.this, SelectCourseActivity.class);
        startActivityForResult(intentCourse, REQUEST_CODE_COURSE);
    }

    private void initViews() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMG && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            imgPost.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }

        if (requestCode == REQUEST_CODE_PDF && resultCode == RESULT_OK && data != null) {
            //if a filePost is selected
            if (data.getData() != null) {
                //uploading the filePost
                filePost = data.getData();
                tvFileName.setText(Utils.getFilename(UploadActivity.this, filePost));
            } else {
                tvFileName.setText("Chưa chọn file");
                Toast.makeText(this, "No filePost chosen", Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode== REQUEST_CODE_COURSE && resultCode== RESULT_OK && data!=null){
            Course course= (Course) data.getSerializableExtra("course");
            btnCategory.setTextColor(Color.parseColor("#D8202A"));
            btnCategory.setText(course.getName());
            courseId= course.getId();
        }
    }

    private boolean checkInputData() {
        if (!Utils.isEmpty(edtTitle) && !Utils.isEmpty(edtDesc)) {
            title = edtTitle.getText().toString().trim();
            desc = edtDesc.getText().toString().trim();
            if (courseId==null) {
                Toast.makeText(this, "Vui lòng chọn học phần tương ứng", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
