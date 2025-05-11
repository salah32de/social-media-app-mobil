package com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase;

import androidx.annotation.NonNull;

import com.example.socialmedia.Database.RemoteDatabase.Entity.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessagesRepository {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;

    public MessagesRepository() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("messages");
    }


    public void SendMessage(Message message, SendMessageCallBack callBack) {
        ref.child(message.getChatId());
        message.setId(ref.push().getKey());
        ref.child(message.getChatId()).child(message.getId()).setValue(message).addOnSuccessListener(aVoid -> {
            callBack.onSuccess(message.getId());
        }).addOnFailureListener(e -> {
            callBack.onFailure(e);
        });
    }


    public void GetMessagesChat(String chatId, GetMessagesChatCallBack callBack) {
        ref.child(chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Message> messageList = new ArrayList<>();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    messageList.add(message);
                }

                callBack.onSuccess(messageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onFailure(error.toException());
            }
        });
    }

    public void DeleteMessagesChat(String chatId, DeleteMessageCallBack callBack) {
        ref.child(chatId).removeValue().addOnSuccessListener(aVoid -> {
            callBack.onSuccess();
        }).addOnFailureListener(e -> {
            callBack.onFailure(e);
        });

    }



public interface SendMessageCallBack {
    void onSuccess(String idMessage);

    void onFailure(Exception e);
}

public interface GetMessagesChatCallBack {
    void onSuccess(List<Message> messageList);

    void onFailure(Exception e);
}

public interface DeleteMessageCallBack {
    void onSuccess();

    void onFailure(Exception e);
}
}
