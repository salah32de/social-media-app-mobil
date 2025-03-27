package com.example.socialmedia.Data.Firebase.RealtimeDatabase;

import androidx.annotation.NonNull;

import com.example.socialmedia.Model.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatRepository {
    DatabaseReference DRef;
    FirebaseDatabase firebaseDatabase;

    public ChatRepository(){
        firebaseDatabase=FirebaseDatabase.getInstance();
        DRef=firebaseDatabase.getReference("chat");
    }

    public void CreateChat(Chat chat,CreateChatCallBack callBack ){
        chat.setIdChat(chat.getIdUser1()+"_"+chat.getIdUser2());
        DRef.child(chat.getIdChat()).setValue(chat).addOnSuccessListener(aVoid -> {
            callBack.onSuccess(chat.getIdChat());
        }).addOnFailureListener(e -> {
            callBack.onFailure(e);
        });
    }
    public void GetChat(String idChat, GetChatCallBack callBack){
        DRef.child(idChat).get().addOnSuccessListener(dataSnapshot -> {
            Chat chat=dataSnapshot.getValue(Chat.class);
            if(chat!=null){
                callBack.onSuccess(chat);
            }else{
                callBack.onFailure(new Exception("chat not found"));
            }
        }).addOnFailureListener(e -> {
            callBack.onFailure(e);
        });

    }
    public void UpdateChat(Chat chat,UpdateChatCallBack callBack){
        DRef.child(chat.getIdChat()).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    callBack.onSuccess();
                }else{
                    callBack.onFailure(task.getException());
                }
            }
        });
    }
    public interface CreateChatCallBack{
        void onSuccess(String idChat);
        void onFailure(Exception e);
    }

    public interface GetChatCallBack{
        void onSuccess(Chat chat);
        void onFailure(Exception e);
    }
    public interface UpdateChatCallBack{
        void onSuccess();
        void onFailure(Exception e);
    }
}
