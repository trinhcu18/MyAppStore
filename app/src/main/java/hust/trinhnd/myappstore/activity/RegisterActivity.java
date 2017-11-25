package hust.trinhnd.myappstore.activity;

import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hust.trinhnd.myappstore.R;
import hust.trinhnd.myappstore.listener.RegisterListener;
import hust.trinhnd.myappstore.base.BaseActivity;
import hust.trinhnd.myappstore.services.RegisterService;
import hust.trinhnd.myappstore.utils.Utils;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.edt_email_register)
    EditText edtEmailReg;
    @BindView(R.id.edt_password)
    EditText edtPassReg;
    @BindView(R.id.btn_reg)
    Button btnReg;
    @BindView(R.id.btn_return)
    Button btnReturn;
    private RegisterService registerService;
    private String email;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        registerService= new RegisterService(this);
    }

    @OnClick({R.id.btn_reg, R.id.btn_return})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.btn_reg:
                if(checkInputData()){
                    showProgressDialog("Vui lòng đợi");
                    registerService.registerAccount(email, password, new RegisterListener() {
                        @Override
                        public void registerSuccess() {
                            hideProgressDialog();
                            edtEmailReg.setText("");
                            edtPassReg.setText("");
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setTitle("Thông báo");
                            builder.setMessage(R.string.email_verification);
                            builder.setIcon(R.drawable.astralis_logo);
                            builder.create().show();
                        }

                        @Override
                        public void registerFailure(String message) {
                            hideProgressDialog();
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.btn_return:
                finish();
                break;
        }
    }

    private boolean checkInputData() {
        if(!Utils.isEmpty(edtEmailReg) && !Utils.isEmpty(edtPassReg)){
            email = edtEmailReg.getText().toString().trim();
            password= edtPassReg.getText().toString().trim();
            if(Utils.isEmailValid(email)){
                edtEmailReg.requestFocus();
                edtEmailReg.setError(getResources().getString(R.string.email_error));
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
