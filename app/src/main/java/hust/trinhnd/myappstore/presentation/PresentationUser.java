package hust.trinhnd.myappstore.presentation;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import hust.trinhnd.myappstore.listener.GetDataListener;
import hust.trinhnd.myappstore.model.Course;
import hust.trinhnd.myappstore.model.Post;
import hust.trinhnd.myappstore.model.User;
import hust.trinhnd.myappstore.utils.Constants;

/**
 * Created by Trinh on 08/12/2017.
 */

public class PresentationUser {
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private Context context;

    public PresentationUser(Context context) {
        this.context = context;
        mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mStorageReference= FirebaseStorage.getInstance().getReference();
    }

    public void getUser(String uid, final GetDataListener listener){
        Query query = mDatabaseReference.child(Constants.USERS).child(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    listener.getDataSuccess(user);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context,"Đã có lỗi xảy ra",Toast.LENGTH_SHORT).show();
                listener.getDataFailure(databaseError.getMessage());
            }
        });
    }

    public void getUserByKey(final String key, final GetDataListener getDataListener) {
        final ArrayList<User> lstUser = new ArrayList<>();
        DatabaseReference userRef = mDatabaseReference.child(Constants.USERS);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User u = ds.getValue(User.class);
                    String uName = u.getName().toLowerCase();
                    String lowKey = key.toLowerCase();
                    if (uName.contains(lowKey) ) {
                        lstUser.add(u);
                    }
                }

                getDataListener.getDataSuccess(lstUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getDataListener.getDataFailure(databaseError.getMessage());
            }
        });
    }

}
