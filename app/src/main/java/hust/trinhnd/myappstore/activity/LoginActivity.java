package hust.trinhnd.myappstore.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hust.trinhnd.myappstore.R;
import hust.trinhnd.myappstore.base.BaseActivity;
import hust.trinhnd.myappstore.listener.LoginListener;
import hust.trinhnd.myappstore.listener.ResetPasswordListener;
import hust.trinhnd.myappstore.services.LoginService;
import hust.trinhnd.myappstore.utils.SharedPreferencesUtils;
import hust.trinhnd.myappstore.utils.Utils;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edt_email)
    EditText edtEmail;
    @BindView(R.id.edt_password)
    EditText edtPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
//    @BindView(R.id.btn_fb_login)
//    Button btnFbLogin;
    @BindView(R.id.btn_go_register)
    Button btnGoReg;
    @BindView(R.id.btn_lost_pass)
    Button btnLostPass;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String email;
    private String password;
    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        loginService = new LoginService();
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser userFB = firebaseAuth.getCurrentUser();
                if (userFB != null) {
                    if (userFB.isEmailVerified()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        SharedPreferencesUtils.setCurrentUid(getApplicationContext(), userFB.getUid());
                        startActivity(intent);
                        finish();
                    } else {
                        mAuth.signOut();
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Thông báo");
                        builder.setMessage(R.string.email_verification);
                        builder.setIcon(R.drawable.astralis_logo);
                        builder.create().show();
                    }
                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @OnClick({R.id.btn_login, R.id.btn_go_register, R.id.btn_lost_pass})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (checkInputData()) {
                    showProgressDialog("Đăng nhập...");
                    loginService.loginAccountEmail(email, password, new LoginListener() {
                        @Override
                        public void loginSuccess() {
                            hideProgressDialog();
                            SharedPreferencesUtils.setCurrentUid(LoginActivity.this,mAuth.getUid());
                            Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void loginFailure(String message) {
                            hideProgressDialog();
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.btn_go_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btn_lost_pass:
                showLostPasswordDialog();
                break;
        }
    }

    private void showLostPasswordDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle("Lấy lại mật khẩu");
        alertDialog.setMessage("Nhập email");

        final EditText input = new EditText(LoginActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.astralis_logo);

        alertDialog.setPositiveButton("Gửi",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!Utils.isEmpty(input)) {

                            showProgressDialog("Đang gửi yêu cầu...");
                            String resetEmail = input.getText().toString();
                            loginService.resetPass(resetEmail, new ResetPasswordListener() {
                                @Override
                                public void onSuccess() {
                                    hideProgressDialog();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setTitle("Thông báo");
                                    builder.setMessage(R.string.reset_password_text);
                                    builder.setIcon(R.drawable.astralis_logo);
                                    builder.create().show();
                                }

                                @Override
                                public void onFailure(String error) {
                                    hideProgressDialog();
                                    Toast.makeText(LoginActivity.this,error,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

        alertDialog.setNegativeButton("Hủy",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private boolean checkInputData() {
        if (!Utils.isEmpty(edtEmail) && !Utils.isEmpty(edtPassword)) {
            email = edtEmail.getText().toString().trim();
            password = edtPassword.getText().toString().trim();
            if (Utils.isEmailValid(email)) {
                edtEmail.requestFocus();
                edtEmail.setError(getResources().getString(R.string.email_error));
                return false;
            }
            return true;

        } else {
            return false;
        }
    }
}
