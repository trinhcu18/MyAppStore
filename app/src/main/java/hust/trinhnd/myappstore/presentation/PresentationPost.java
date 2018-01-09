package hust.trinhnd.myappstore.presentation;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import hust.trinhnd.myappstore.listener.DeleteDataListener;
import hust.trinhnd.myappstore.listener.GetDataListener;
import hust.trinhnd.myappstore.listener.TaskUploadListener;
import hust.trinhnd.myappstore.listener.UploadPostListener;
import hust.trinhnd.myappstore.model.Course;
import hust.trinhnd.myappstore.model.Post;
import hust.trinhnd.myappstore.model.User;
import hust.trinhnd.myappstore.utils.Constants;
import hust.trinhnd.myappstore.utils.Utils;

/**
 * Created by Trinh on 08/12/2017.
 */

public class PresentationPost {
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private Context context;

    public PresentationPost(Context context) {
        this.context = context;
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void getHomePost(final GetDataListener getDataListener) {
        final ArrayList<Post> lstPost = new ArrayList<>();
        DatabaseReference mPostRef = mDatabaseReference.child(Constants.POSTS);
        mPostRef.orderByChild("dateCreated")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            lstPost.add(ds.getValue(Post.class));
                        }
                        Collections.sort(lstPost, new Comparator<Post>() {
                            @Override
                            public int compare(Post o1, Post o2) {
                                Long long1 = new Long(o1.getDateCreated());
                                Long long2 = new Long(o2.getDateCreated());
                                return long2.compareTo(long1);
                            }
                        });
                        getDataListener.getDataSuccess(lstPost);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        getDataListener.getDataFailure(databaseError.getMessage());
                    }
                });
    }

    public void getUserPost(String uid, final GetDataListener getDataListener) {
        final ArrayList<Post> lstPost = new ArrayList<>();
        DatabaseReference mPostRef = mDatabaseReference.child(Constants.POSTS);
        mPostRef.orderByChild("uid")
                .equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            lstPost.add(ds.getValue(Post.class));
                        }
                        Collections.sort(lstPost, new Comparator<Post>() {
                            @Override
                            public int compare(Post o1, Post o2) {
                                Long long1 = new Long(o1.getDateCreated());
                                Long long2 = new Long(o2.getDateCreated());
                                return long2.compareTo(long1);
                            }
                        });
                        getDataListener.getDataSuccess(lstPost);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        getDataListener.getDataFailure(databaseError.getMessage());
                    }
                });
    }

    public void addPost(final String uid, String title, String desc, byte[] dataImg,
                        String courseId, final Uri filePost, final UploadPostListener listener) {
        final Post post = new Post(uid, title, desc, courseId);
        StorageReference imgStorageRef = mStorageReference.child(Constants.FIREBASE_IMAGE_STORAGE + "/" + uid + "/img" + System.currentTimeMillis());
        UploadTask uploadTask = imgStorageRef.putBytes(dataImg);
        uploadTask.addOnCompleteListener((Activity) context, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    {
                        Uri imgUrl = task.getResult().getMetadata().getDownloadUrl();
                        post.setImage(String.valueOf(imgUrl));
                        uploadFileToStorage(uid, filePost, post, new TaskUploadListener() {
                            @Override
                            public void taskSuccess() {
                                savePostToDatabase(post, new UploadPostListener() {
                                    @Override
                                    public void uploadPostSuccess(Post post) {
                                        listener.uploadPostSuccess(post);
                                    }

                                    @Override
                                    public void uploadPostFailure(String error) {
                                        listener.uploadPostFailure(error);
                                    }
                                });
                            }

                            @Override
                            public void taskFailure(String error) {
                                listener.uploadPostFailure(error);
                            }
                        });
                    }
                }
            }
        });

    }

    private void savePostToDatabase(final Post post, final UploadPostListener savePostListener) {
        final String newKey = mDatabaseReference
                .child(Constants.POSTS)
                .push().getKey();
        mDatabaseReference.child(Constants.POSTS).child(newKey).setValue(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        mDatabaseReference.child(Constants.POSTS).child(newKey).child("postId").setValue(newKey);
                        savePostListener.uploadPostSuccess(post);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        savePostListener.uploadPostFailure(e.getMessage());
                    }
                });
    }

    private void uploadFileToStorage(String uid, Uri filePost, final Post post, final TaskUploadListener upFileListener) {
        if(filePost== null) {
            upFileListener.taskSuccess();
            return;
        }
        StorageReference fileStorageRef = mStorageReference.child(Constants.FIREBASE_FILE_STORAGE + "/" + uid + "/" + Utils.getFilename(context, filePost));
        fileStorageRef.putFile(filePost).addOnCompleteListener((Activity) context, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Uri fileUrl = task.getResult().getDownloadUrl();
                    post.setLink(String.valueOf(fileUrl));
                    String fileName = task.getResult().getMetadata().getName();
                    post.setFileName(fileName);
                    upFileListener.taskSuccess();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                upFileListener.taskFailure(e.getMessage());
            }
        });
    }

    public void getPostById(String postId, final GetDataListener listener) {
        if(postId==null){
            listener.getDataFailure("Failed");
            return;
        }
        Query query = mDatabaseReference.child(Constants.POSTS).child(postId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                listener.getDataSuccess(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                listener.getDataFailure(databaseError.getMessage());
            }
        });
    }

    public void deletePost(String postId, final DeleteDataListener deleteDataListener) {
        mDatabaseReference.child(Constants.POSTS).child(postId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public void getPostByTitle(final String key, final GetDataListener getDataListener) {
        final ArrayList<Post> lstPost = new ArrayList<>();
        DatabaseReference mPostRef = mDatabaseReference.child(Constants.POSTS);
        mPostRef.orderByChild("dateCreated")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Post p=ds.getValue(Post.class);
                            String pTitle= p.getTitle();
                            String s1=pTitle.toLowerCase();
                            String s2= key.toLowerCase();
                            if(s1.contains(s2)){
                                lstPost.add(p);
                            }
                        }

                        getDataListener.getDataSuccess(lstPost);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        getDataListener.getDataFailure(databaseError.getMessage());
                    }
                });
    }

    public void getPostByCourseKey(String key, final GetDataListener listener) {
        PresentationCourse presentationCourse= new PresentationCourse(context);
        presentationCourse.getCourseByKey(key, new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                final ArrayList<Post> posts= new ArrayList<>();
                ArrayList<Course> courses= (ArrayList<Course>) object;
                for(Course c: courses){
                    String courseId= c.getId();
                    mDatabaseReference.child(Constants.POSTS)
                            .orderByChild("courseId")
                            .equalTo(courseId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                                        posts.add(ds.getValue(Post.class));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    listener.getDataFailure(databaseError.getMessage());
                                }
                            });
                }
                Collections.sort(posts, new Comparator<Post>() {
                    @Override
                    public int compare(Post o1, Post o2) {
                        Long long1 = new Long(o1.getDateCreated());
                        Long long2 = new Long(o2.getDateCreated());
                        return long2.compareTo(long1);
                    }
                });
                listener.getDataSuccess(posts);
            }

            @Override
            public void getDataFailure(String error) {
                listener.getDataFailure(error);
            }
        });

    }

    public void getPostByUserKey(String key, final GetDataListener listener) {
        PresentationUser presentationUser= new PresentationUser(context);
        presentationUser.getUserByKey(key, new GetDataListener() {
            @Override
            public void getDataSuccess(Object object) {
                final ArrayList<Post> posts= new ArrayList<>();
                ArrayList<User> lstUser= (ArrayList<User>) object;
                for(User user: lstUser){
                    String userId= user.getUid();
                    mDatabaseReference.child(Constants.POSTS)
                            .orderByChild("uid")
                            .equalTo(userId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                                        posts.add(ds.getValue(Post.class));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    listener.getDataFailure(databaseError.getMessage());
                                }
                            });
                }
                Collections.sort(posts, new Comparator<Post>() {
                    @Override
                    public int compare(Post o1, Post o2) {
                        Long long1 = new Long(o1.getDateCreated());
                        Long long2 = new Long(o2.getDateCreated());
                        return long2.compareTo(long1);
                    }
                });
                listener.getDataSuccess(posts);
            }

            @Override
            public void getDataFailure(String error) {
                listener.getDataFailure(error);
            }
        });
    }
}
