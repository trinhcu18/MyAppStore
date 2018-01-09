package hust.trinhnd.myappstore.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import hust.trinhnd.myappstore.base.BaseFirebase;
import hust.trinhnd.myappstore.listener.LoginListener;
import hust.trinhnd.myappstore.listener.ResetPasswordListener;
import hust.trinhnd.myappstore.utils.SharedPreferencesUtils;

/**
 * Created by Trinh on 18/11/2017.
 */

public class LoginService extends BaseFirebase {
    private FirebaseAuth auth;

    public LoginService() {
        this.auth = getFirebaseAuth();
    }

    public void loginAccountEmail(String email, String password, final LoginListener listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.loginFailure(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        listener.loginSuccess();
                    }
                });
    }

    public void resetPass(String emailAddress, final ResetPasswordListener listener) {
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("LoginService", "Email sent.");
                            listener.onSuccess();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e.getMessage());
                    }
                });
    }
}
