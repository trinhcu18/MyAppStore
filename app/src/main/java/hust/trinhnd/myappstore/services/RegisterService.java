package hust.trinhnd.myappstore.services;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import hust.trinhnd.myappstore.listener.RegisterListener;
import hust.trinhnd.myappstore.Users;
import hust.trinhnd.myappstore.base.BaseFirebase;
import hust.trinhnd.myappstore.utils.Constants;

/**
 * Created by Trinh on 31/10/2017.
 */

public class RegisterService extends BaseFirebase {
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private Activity activity;

    public RegisterService(Activity activity) {
        this.activity = activity;
        auth = getFirebaseAuth();
        mDatabase = getDatabaseReference();
    }

    /**
     * Đăng lý tài khoản bằng Email
     *
     * @param email
     * @param passWord
     * @param listener
     */
    public void registerAccount(String email, String passWord, final RegisterListener listener) {
        auth.createUserWithEmailAndPassword(email, passWord)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser userFB = task.getResult().getUser();
                            if (userFB != null) {
                                //Gửi 1 email xác thực tài khoản
                                userFB.sendEmailVerification()
                                        .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //Tiến hành thông tin user vào Database
                                            Users users = new Users();
                                            users.setUid(userFB.getUid());
                                            users.setName("Mr.Test");
                                            users.setEmail(userFB.getEmail());
                                            createAccountInDatabase(users, new RegisterListener() {
                                                @Override
                                                public void registerSuccess() {
                                                    auth.signOut(); // Đăng xuất.
                                                    listener.registerSuccess();
                                                }

                                                @Override
                                                public void registerFailure(String message) {
                                                    listener.registerFailure(message);
                                                }
                                            });

                                        }
                                    }
                                });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.registerFailure(e.getMessage());
            }
        });

    }

    /**
     * Lưu thông tin user
     *
     * @param users
     */
    public void createAccountInDatabase(Users users, final RegisterListener dbListener) {
        mDatabase.child(Constants.USERS)
                .child(users.getUid())
                .setValue(users)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dbListener.registerSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dbListener.registerFailure(e.getMessage());
            }
        });
    }
}
