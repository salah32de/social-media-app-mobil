package com.example.socialmedia.Controller;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.CommentRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.NotificationRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.PostRepository;
import com.example.socialmedia.Database.RemoteDatabase.StorageDatabase.StorageFirebase;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Comment;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Notification;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.SharedPreferencesHelper;

import java.util.List;

public class CommentManager {

    private CommentRepository commentRepository;
    private StorageManager storageManager;
    private final String TAG = "TAG: CommentManager";

    public CommentManager() {
        commentRepository = new CommentRepository();
        storageManager = new StorageManager();
    }

    public void AddComment(Post post, Context context, Comment comment, Uri uri, CommentRepository.AddCommentCallback callback) {
        if (uri != null) {
            AddPhotoCommentInStorage(post,context,comment, uri, callback);
        } else {
              AddCommentInRealtime(post,context,comment, callback);
        }
    }

    private void AddPhotoCommentInStorage(Post post,Context context,Comment comment, Uri uri, CommentRepository.AddCommentCallback callback) {
        if (uri != null) {
            storageManager.uploadImage(uri, new StorageFirebase.UploadCallback() {
                @Override
                public void onSuccess(String downloadUrl) {
                    Log.d(TAG, "upload image successfully");
                    comment.setLink(downloadUrl);
                    AddCommentInRealtime(post,context,comment, callback);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "upload image is failed with error " + e);
                    callback.addCommentFailure(null);
                }
            });
        }

    }

    private void AddCommentInRealtime(Post post, Context context, Comment comment, CommentRepository.AddCommentCallback callback) {
        commentRepository.addComment(comment, new CommentRepository.AddCommentCallback() {

            @Override
            public void addCommentSuccess() {

                PostManager postManager = new PostManager();
                postManager.addNumLikeOrCommentInPost(post,context,PostRepository.ADD_NUM_COMMENTS_IN_POST, new PostRepository.AddLikeInPostCallback() {
                    @Override
                    public void addLikeInPostSuccess(long numberLike) {
                        Log.d(TAG, "increase the number of comment on the post");
                        post.setNumComments(numberLike);
                    }

                    @Override
                    public void addLikeInPostFailure() {
                        Log.d(TAG, "not increase the number of comment on the post");
                    }
                });



                Log.d(TAG, "add Comment int firebase is successful");
                callback.addCommentSuccess();
            }

            @Override
            public void addCommentFailure(Exception e) {
                Log.d(TAG, "add Comment int firebase is failed with error:" + e.getMessage());
                storageManager.Delete(comment.getLink());
                callback.addCommentFailure(null);
            }

        });
    }


    public void getCommentPost(Post post, String idParent, CommentRepository.GetCommentPostCallback callback) {
        commentRepository.getCommentPost(post, idParent, new CommentRepository.GetCommentPostCallback() {
            @Override
            public void getCommentPostSuccess(List<Comment> commentList) {
                Log.d(TAG, "get Comment Post is successful");
                callback.getCommentPostSuccess(commentList);
            }

            @Override
            public void getCommentPostFailure(Exception e) {
                Log.d(TAG, "get Comment Post is failed with error:" + e.getMessage());
                callback.getCommentPostFailure(e);
            }
        });
    }

    public void addLikeInComment(Context context,Post post,Comment comment, CommentRepository.AddLikeInCommentCallback callback) {
        commentRepository.addLikeInComment(post,comment, new CommentRepository.AddLikeInCommentCallback() {

            @Override
            public void addLikeInCommentSuccess(int nmbLike) {
                Log.d(TAG, "add like in comment is successful nmbLike=" + nmbLike);
                NotificationManager notificationManager=new NotificationManager();
                User user= SharedPreferencesHelper.getUser(context);
                Notification notification=new Notification(user.getId(),comment.getIdComment(),Notification.LIKE_COMMENT);
                notificationManager.addNotification(context,notification,post.getUserCreatePost().getId(), new NotificationRepository.AddNotificationCallback() {
                    @Override
                    public void addNotificationSuccess() {
                        Log.d(TAG,"add notification success");
                    }

                    @Override
                    public void addNotificationFailure(Exception e) {
                        Log.d(TAG,"add notification failure");
                    }
                });


                callback.addLikeInCommentSuccess(nmbLike);
            }

            @Override
            public void addLikeInCommentFailure(Exception e) {
                Log.d(TAG, "add like in comment is failed /error is " + e.getMessage());
            }
        });
    }
    public void DeleteCommentsPostsUser(String idUser) {
        commentRepository.DeleteCommentPostsUser(idUser, new CommentRepository.DeleteCommentsCallBack() {
            @Override
            public void deleteCommentsSuccess() {
                Log.d(TAG, "delete comments success");
            }

            @Override
            public void deleteCommentsFailure(Exception e) {
                Log.d(TAG, "delete comments failure");
            }
        });
    }

}
