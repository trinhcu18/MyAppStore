package hust.trinhnd.myappstore.presentation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import hust.trinhnd.myappstore.R;
import hust.trinhnd.myappstore.listener.DeleteDataListener;
import hust.trinhnd.myappstore.listener.GetDataListener;
import hust.trinhnd.myappstore.listener.TaskUploadListener;
import hust.trinhnd.myappstore.model.Comment;
import hust.trinhnd.myappstore.model.Post;
import hust.trinhnd.myappstore.utils.Constants;

/**
 * Created by Trinh on 17/12/2017.
 */

public class PresentationComment {
    private Context context;
    private DatabaseReference rootRef;
    private DatabaseReference cmtRef;

    public PresentationComment(Context context) {
        this.context = context;
        rootRef = FirebaseDatabase.getInstance().getReference();
        cmtRef = rootRef.child(Constants.COMMENTS);
    }


    public void getCommentByPost(String postId, final GetDataListener listener) {
        cmtRef.orderByChild(context.getString(R.string.postid))
                .equalTo(postId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Comment> lstComment = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Comment comment = ds.getValue(Comment.class);
                            lstComment.add(comment);
                        }

                        Collections.sort(lstComment, new Comparator<Comment>() {
                            @Override
                            public int compare(Comment o1, Comment o2) {
                                Long long1 = new Long(o1.getDateCreated());
                                Long long2 = new Long(o2.getDateCreated());
                                return long1.compareTo(long2);
                            }
                        });
                        listener.getDataSuccess(lstComment);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.getDataFailure(databaseError.getMessage());
                    }
                });
    }

    public void getCommentByUser(String uid, final GetDataListener listener) {
        cmtRef.orderByChild(context.getString(R.string.userId))
                .equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Comment> lstComment = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Comment comment = ds.getValue(Comment.class);
                            lstComment.add(comment);
                        }

                        Collections.sort(lstComment, new Comparator<Comment>() {
                            @Override
                            public int compare(Comment o1, Comment o2) {
                                Long long1 = new Long(o1.getDateCreated());
                                Long long2 = new Long(o2.getDateCreated());
                                return long2.compareTo(long1);
                            }
                        });
                        listener.getDataSuccess(lstComment);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.getDataFailure(databaseError.getMessage());
                    }
                });
    }

    public void addComment(String currentUid, String postId, String text, final TaskUploadListener taskUploadListener) {
        Comment comment = new Comment(currentUid, postId, text);
        final String commentId = cmtRef.push().getKey();
        cmtRef.child(commentId)
                .setValue(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cmtRef.child(commentId).child(context.getString(R.string.commentid)).setValue(commentId);
                        taskUploadListener.taskSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        taskUploadListener.taskFailure(e.getMessage());
                    }
                });
    }

    public void deleteCmt(String commentId, final DeleteDataListener deleteDataListener) {
        cmtRef.child(commentId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteDataListener.deleteSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deleteDataListener.deleteFailure(e.getMessage());
            }
        });
    }
}
