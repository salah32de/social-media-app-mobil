package com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmedia.Controller.StorageManager;
import com.example.socialmedia.Controller.UserManager;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class PostRepository {
    public static String ADD_NUM_LIKES_IN_POST = "likes";
    public static String ADD_NUM_COMMENTS_IN_POST = "numComments";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;

    public PostRepository() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("posts");
    }

    //add post in firebase realtime
    //data is stored in a folder 'posts' and each post is stored in a folder with the user id
    // posts:{
    //       idUser1:{
    //               post1 information
    //               post2 information ...}
    //       idUser2:{
    //               post1 information
    //               post2 information ...}
    //      .
    //      .
    //      .
    //}

    //idPost=idUser+ref.push().getKey()
    //ref.push().getKey() is a random key generated by firebase
    public void addPost(Post post, CreatePostCallback createPostCallback) {

        DatabaseReference userRef = firebaseDatabase.getReference("posts").child(post.getIdUser());
        post.setIdPost(post.getIdUser() + userRef.push().getKey());
        userRef.child(post.getIdPost()).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    createPostCallback.createPostSuccess();
                } else {
                    createPostCallback.createPostFailure();
                }
            }
        });

    }


    public void getPostsUser(User user, GetPostsUserCallback getPostsUserCallback) {
        ref.child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setUserCreatePost(user);
                    list.add(0, post);

                }
                getPostsUserCallback.getPostsUserSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                getPostsUserCallback.getPostsUserFailure(error.toException());
            }
        });
    }

    public void addNumLikeOrCommentInPost(Post post, String string, AddLikeInPostCallback addLikeInPostCallback) {


        DatabaseReference postRef = ref.child(post.getIdUser()).child(post.getIdPost()).getRef();

        postRef.child(string).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData);  // إتمام العملية بنجاح
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    addLikeInPostCallback.addLikeInPostFailure();
                } else {
                    addLikeInPostCallback.addLikeInPostSuccess(dataSnapshot.getValue(Long.class));
                }
            }
        });

    }


    public void DeletePostsUser(String idUser, DeletePostsCallBack callback) {
        // Delete all posts of the user
        ref.child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    String downloadUrl = post.getLink();
                    if (!downloadUrl.isEmpty()) {
                        Log.d("asdkowqkdwq", "onDataChange: " + downloadUrl);
                        StorageManager storageManager = new StorageManager();
                        storageManager.Delete(downloadUrl);
                    }
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getPostById(String idPost, GetPostByIdCallback getPostByIdCallback) {
        String id = idPost.substring(0, idPost.indexOf("-"));
        ref.child(id).child(idPost).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);

                UserManager userManager = new UserManager();
                userManager.getUserById(post.getIdUser(), new UserRepository.UserCallBack<User>() {
                    @Override
                    public void onSuccess(User value) {
                        post.setUserCreatePost(value);
                        getPostByIdCallback.getPostByIdSuccess(post);
                    }

                    @Override
                    public void onFailure(Exception e) {
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void DeletePostById(String idUser, String idPost, DeletePostCallBack callBack) {
        ref.child(idUser).child(idPost).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callBack.deletePostSuccess();
                } else {
                    callBack.deletePostsFailure(task.getException());
                }
            }
        });
    }

    public void GetCountPosts(GetCountPosts callback) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    count += dataSnapshot.getChildrenCount();
                }
                callback.onSuccess(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    public void GetAllPosts(GetAllPostsCallback callback){
        AtomicInteger count = new AtomicInteger();
        List<Post> list = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.onSuccess(list);
                    return;
                }

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    count.incrementAndGet();
                    String userId = snapshot1.getKey();

                    UserManager userManager = new UserManager();
                    userManager.getUserById(userId, new UserRepository.UserCallBack<User>() {
                        @Override
                        public void onSuccess(User value) {
                            for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                                Post post = snapshot2.getValue(Post.class);
                                if (post != null) {
                                    post.setUserCreatePost(value);
                                    list.add(0, post); // الأحدث أولاً
                                }
                            }
                            if (count.decrementAndGet() == 0) {
                                callback.onSuccess(list);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if (count.decrementAndGet() == 0) {
                                callback.onFailure(e);
                            }
                        }
                    });
                }

                // في حال snapshot موجود بس مافيه بيانات
                if (!snapshot.hasChildren()) {
                    callback.onSuccess(list);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }


    public interface GetAllPostsCallback {
        void onSuccess(List<Post> posts);
        void onFailure(Exception e);
    }
    public interface GetCountPosts {
        void onSuccess(long count);

        void onFailure(Exception e);
    }

    public interface GetPostsByUserIdsCallback {
        void onSuccess(List<Post> posts);

        void onFailure(Exception e);
    }

    public interface AddLikeInPostCallback {
        void addLikeInPostSuccess(long numberLike);

        void addLikeInPostFailure();
    }

    public interface CreatePostCallback {
        void createPostSuccess();

        void createPostFailure();
    }

    public interface GetPostsUserCallback {
        void getPostsUserSuccess(List<Post> post);

        void getPostsUserFailure(Exception e);
    }

    public interface GetPostByIdCallback {
        void getPostByIdSuccess(Post post);

        void getPostByIdFailure(Exception e);
    }

    public interface DeletePostsCallBack {
        void deletePostsSuccess();

        void deletePostsFailure(Exception e);
    }

    public interface DeletePostCallBack {

        void deletePostSuccess();

        void deletePostsFailure(Exception e);
    }

}
