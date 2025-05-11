package com.example.socialmedia.Controller;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.FriendRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.NotificationRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.PostRepository;
import com.example.socialmedia.Database.RemoteDatabase.StorageDatabase.StorageFirebase;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Notification;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


//this class is manager of Post
public class PostManager {
    private final String TAG = "TAG: PostManager";//the TAG is for debug in logcat
    private StorageManager storageManager;
    private PostRepository postRepository;

    public PostManager() {
        storageManager = new StorageManager();
        postRepository = new PostRepository();
    }

    //create post in realtime database and storage
    //storage store the video or image and return the download url if exist
    //realtime store the information of post and return the id of post
    public void CreatePost(Post post, String FiledName, Uri filedUri, PostRepository.CreatePostCallback createPostCallback) {
        //initialize the callBack of storage
        StorageFirebase.UploadCallback uploadCallback = new StorageFirebase.UploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                //upload file successfully
                Log.d(TAG, "File Uploaded Successfully");
                post.setLink(downloadUrl);//set the download url to post
                CreatePostInRealtime(post, createPostCallback);//create post in realtime database
            }

            @Override
            public void onFailure(Exception e) {
                //upload file failed
                Log.d(TAG, "File Upload Failed");
                createPostCallback.createPostFailure();
            }
        };

        switch (FiledName) {
            case "image":
                storageManager.uploadImage(filedUri, uploadCallback);
                break;
            case "video":
                storageManager.uploadVideo(filedUri, uploadCallback);
                break;
            default:
                post.setLink("");
                CreatePostInRealtime(post, createPostCallback);
                break;
        }
    }

    //create post in realtime database
    private void CreatePostInRealtime(Post post, PostRepository.CreatePostCallback createPostCallback) {
        postRepository.addPost(post, new PostRepository.CreatePostCallback() {
            @Override
            public void createPostSuccess() {
                Log.d(TAG, "Post Created Successfully");
                createPostCallback.createPostSuccess();

            }

            @Override
            public void createPostFailure() {
                Log.d(TAG, "Post Created Failed");
                if (!post.getLink().isEmpty()) {
                    storageManager.Delete(post.getLink());
                }
                createPostCallback.createPostFailure();
            }
        });
    }

    public void getPostsUser(User user, PostRepository.GetPostsUserCallback getPostsUserCallback) {
        postRepository.getPostsUser(user, new PostRepository.GetPostsUserCallback() {
            @Override
            public void getPostsUserSuccess(List<Post> post) {
                Log.d(TAG, "Get Posts User Successfully");
                getPostsUserCallback.getPostsUserSuccess(post);

            }

            @Override
            public void getPostsUserFailure(Exception e) {
                Log.d(TAG, "Get Posts User Failed " + e.getMessage());
            }
        });
    }

    public void addNumLikeOrCommentInPost(Post post, Context context, String string, PostRepository.AddLikeInPostCallback addLikeInPostCallback) {
        postRepository.addNumLikeOrCommentInPost(post, string, new PostRepository.AddLikeInPostCallback() {
            @Override
            public void addLikeInPostSuccess(long numberLike) {
                Log.d(TAG, "Add Like In Post Successfully ,,, number like = " + numberLike);
                addLikeInPostCallback.addLikeInPostSuccess(numberLike);

                int typeNotification = string.equals(PostRepository.ADD_NUM_LIKES_IN_POST) ? Notification.LIKE_POST : Notification.COMMENT_POST;

                User user = SharedPreferencesHelper.getUser(context);

                Notification notification = new Notification(user.getId(), post.getIdPost(), typeNotification);
                NotificationManager notificationManager = new NotificationManager();
                notificationManager.addNotification(context,notification, post.getUserCreatePost().getId(), new NotificationRepository.AddNotificationCallback() {
                    @Override
                    public void addNotificationSuccess() {
                        Log.d(TAG, "Add Notification Success");
                    }

                    @Override
                    public void addNotificationFailure(Exception e) {
                        Log.d(TAG, "Add Notification Failed " + e.getMessage());
                    }
                });
            }

            @Override
            public void addLikeInPostFailure() {
                Log.d(TAG, "Add Like In Post Failed");
                addLikeInPostCallback.addLikeInPostFailure();
            }
        });
    }

    public void GetPostById(String idPost, PostRepository.GetPostByIdCallback getPostByIdCallback) {
        postRepository.getPostById(idPost, new PostRepository.GetPostByIdCallback() {
            @Override
            public void getPostByIdSuccess(Post post) {
                Log.d(TAG, "Get Post By Id Successfully");
                getPostByIdCallback.getPostByIdSuccess(post);
            }

            @Override
            public void getPostByIdFailure(Exception e) {
                Log.d(TAG, "Get Post By Id Failed " + e.getMessage());
                getPostByIdCallback.getPostByIdFailure(e);
            }
        });
    }


    public void DeletePostsUser(String idPost) {
        postRepository.DeletePostsUser(idPost, new PostRepository.DeletePostsCallBack() {
            @Override
            public void deletePostsSuccess() {
                Log.d(TAG, "Delete Post Successfully");

            }

            @Override
            public void deletePostsFailure(Exception e) {
                Log.d(TAG, "Delete Post Failed " + e.getMessage());
            }

        });
    }


    public void getFeedPosts(User user, FeedPostsCallback callback) {
        FriendManager friendManager = new FriendManager();
        PostManager postManager = new PostManager();

        friendManager.GetFriendsByIdUser(user.getId(), new FriendRepository.GetFriendsByIdUserCallback() {
            @Override
            public void getFriendsByIdUserSuccess(List<User> friends) {

                friends.add(user);

                List<Post> allPosts = new ArrayList<>();
                AtomicInteger counter = new AtomicInteger(friends.size());

                for (User u : friends) {
                    postManager.getPostsUser(u, new PostRepository.GetPostsUserCallback() {
                        @Override
                        public void getPostsUserSuccess(List<Post> posts) {
                            synchronized (allPosts) {
                                allPosts.addAll(posts);
                                if (counter.decrementAndGet() == 0) {
                                    // ترتيب المنشورات حسب الأحدث (التاريخ)


                                    Collections.sort(allPosts, (p1, p2) -> Long.compare(p2.getDate(), p1.getDate()));
                                    callback.onSuccess(allPosts);
                                }
                            }
                        }

                        @Override
                        public void getPostsUserFailure(Exception e) {
                            Log.d(TAG, "Failed to load posts for user: " + u, e);
                            if (counter.decrementAndGet() == 0) {
                                callback.onSuccess(allPosts); // إرجاع ما تم تحميله حتى لو كان هناك أخطاء
                            }
                        }
                    });
                }
            }

            @Override
            public void getFriendsByIdUserFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void DeletePostById(String idUser, Post post, PostRepository.DeletePostCallBack callBack){
        postRepository.DeletePostById(idUser, post.getIdPost(), new PostRepository.DeletePostCallBack() {
            @Override
            public void deletePostSuccess() {
                Log.d(TAG,"delete post success .id post: "+post.getIdPost());
                if(!post.getLink().isEmpty()){
                    storageManager.Delete(post.getLink());
                }
                callBack.deletePostSuccess();
            }

            @Override
            public void deletePostsFailure(Exception e) {
                Log.d(TAG,"delete post failure .id post: "+post.getIdPost());
            }
        });
    }

    public void GetCountPosts(PostRepository.GetCountPosts callback){
        postRepository.GetCountPosts(new PostRepository.GetCountPosts() {
            @Override
            public void onSuccess(long count) {
                Log.d(TAG,"count posts ="+count);
                callback.onSuccess(count);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG,"get count posts failure .error: "+e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    public void GetAllPosts(PostRepository.GetAllPostsCallback callback){
        postRepository.GetAllPosts(new PostRepository.GetAllPostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                Log.d(TAG,"get all posts success .count posts ="+posts.size());
                callback.onSuccess(posts);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG,"get all posts failure .error: "+e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    public interface FeedPostsCallback {
        void onSuccess(List<Post> posts);

        void onFailure(Exception e);
    }

}
