package com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmedia.Controller.UserManager;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Comment;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CommentRepository {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference DRef;

    public CommentRepository() {//constructor
        firebaseDatabase = FirebaseDatabase.getInstance();
        DRef = firebaseDatabase.getReference("comments");
    }

    //structure of database in realtime
    //comment{
    //  idPost1{
    //      idComment1
    //      idComment2
    //      idComment3
    //  }
    //  idPost2{
    //      idComment1
    //      idComment2
    //      idComment2reply{
    //          idComment1
    //          idComment2
    //          idComment2reply{
    //              idComment1
    //              idComment2
    //              ....
    //          }
    //          idComment3
    //      ....
    //      }
    //     idComment3
    //  }
    //  idPost3{
    //
    //
    //  }
    // }
    public void addComment(Comment comment, AddCommentCallback addCommentCallback) {
        DatabaseReference ref = DRef.child(comment.getIdPost());//get the reference of the post
        if (!comment.getIdParent().isEmpty()) {//get the reference of the parent if exist
            ref = ref.getRef().child(comment.getIdParent());
            addNbrReplyInComment(comment);
        }

        comment.setIdComment(ref.push().getKey());//set the id of the comment


        //add the comment to the database
        ref.child(comment.getIdComment()).setValue(comment).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addCommentCallback.addCommentSuccess();
            } else {
                addCommentCallback.addCommentFailure(task.getException());
            }
        });
    }


    public void getCommentPost(Post post, String idParent, GetCommentPostCallback getCommentPostCallback) {
        DatabaseReference ref = DRef.child(post.getIdPost()).child(idParent);//get the reference of the post and the parent if exist

        UserManager userManager = new UserManager();
        //get all comments from reference
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Comment> list = new ArrayList<>();
                AtomicInteger counter = new AtomicInteger(0);//counter for asynchronous operations

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (!dataSnapshot.getKey().contains("reply")) {
                        Comment comment = dataSnapshot.getValue(Comment.class);

                        list.add(0, comment); // add the comment in list head for show it first
                        counter.incrementAndGet(); // increment the counter when asynchronous operation is start

                        //get user create comment
                        userManager.getUserById(comment.getIdUser(), new UserRepository.UserCallBack<User>() {
                            @Override
                            public void onSuccess(User value) {
                                comment.setUserCreateComment(value);//set the user create comment
                                if (counter.decrementAndGet() == 0) {//if all asynchronous operations are finished
                                    getCommentPostCallback.getCommentPostSuccess(list);
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                list.remove(comment); // delete the comment if get user is failed
                                if (counter.decrementAndGet() == 0) {//if all asynchronous operations are finished
                                    getCommentPostCallback.getCommentPostSuccess(list);
                                }
                            }
                        });
                    }
                }

                //if there is no comment
                if (counter.get() == 0) {
                    getCommentPostCallback.getCommentPostSuccess(list);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                getCommentPostCallback.getCommentPostFailure(error.toException());
            }
        });
    }

    public void addLikeInComment(Post post,Comment comment, AddLikeInCommentCallback addLikeInCommentCallback) {
        DatabaseReference ref = DRef.child(comment.getIdPost());//get the reference of the post
        if (comment.getIdParent() != null && !comment.getIdParent().isEmpty()) {//get the reference of the parent if exist
            ref = ref.child(comment.getIdParent());
        }
        ref = ref.child(comment.getIdComment());//?? i mean is it necessary? i don't know .but he work so i don't touch this
        ref.child("nmbLike").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                if (currentData.getValue() == null) {// if the number of likes is not exist, set it to 1
                    currentData.setValue(1);
                } else {// increase the number of likes by 1
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData);  //success
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    addLikeInCommentCallback.addLikeInCommentFailure(error.toException());
                } else {
                    addLikeInCommentCallback.addLikeInCommentSuccess(currentData.getValue(Integer.class));
                }
            }
        });
    }

    public void addNbrReplyInComment(Comment comment) {
        DatabaseReference dRef = DRef.child(comment.getIdPost());
        dRef = dRef.child(comment.getIdParent().substring(0, comment.getIdParent().length() - 5));
        dRef.child("nmbReply").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                // إذا كانت قيمة الإعجابات غير موجودة، قم بتهيئتها بـ 1
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData);  // إتمام العملية بنجاح
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
            }
        });
    }
    public void DeleteCommentPostsUser(String idUser, DeleteCommentsCallBack callback) {
        Query query = DRef.orderByKey().startAt(idUser);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    postSnapshot.getRef().removeValue();
                }
                callback.deleteCommentsSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.deleteCommentsFailure(error.toException());
            }
        });
    }


    public interface DeleteCommentsCallBack {
        void deleteCommentsSuccess();
        void deleteCommentsFailure(Exception e);
    }

    public interface AddLikeInCommentCallback {
        void addLikeInCommentSuccess(int nmbLike);

        void addLikeInCommentFailure(Exception e);
    }

    public interface AddCommentCallback {
        void addCommentSuccess();

        void addCommentFailure(Exception e);
    }


    public interface GetCommentPostCallback {
        void getCommentPostSuccess(List<Comment> commentList);

        void getCommentPostFailure(Exception e);
    }

}
