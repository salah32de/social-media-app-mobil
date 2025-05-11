package com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase;

import androidx.annotation.NonNull;

import com.example.socialmedia.Controller.UserManager;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Friend;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendRepository {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference DRef;

    public FriendRepository() {//constructor
        firebaseDatabase = FirebaseDatabase.getInstance();
        DRef = firebaseDatabase.getReference("friends");
    }

    public void AddFriend(Friend friend, AddFriendCallback addFriendCallback) {
        friend.setId(friend.getIdUser1() + "-" + friend.getIdUser2());
        DRef.child(friend.getId()).setValue(friend).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addFriendCallback.addFriendSuccess();
            } else {
                addFriendCallback.addFriendFailure(task.getException());
            }
        });
    }

    public void GetFriendsByIdUser(String idUser, GetFriendsByIdUserCallback getFriendsByIdUserCallback) {
        Query query1 = DRef.orderByChild("idUser1").equalTo(idUser);
        Query query2 = DRef.orderByChild("idUser2").equalTo(idUser);

        UserManager userManager = new UserManager();

        Set<User> friendSet = Collections.synchronizedSet(new HashSet<>()); // تجنب التكرار مع دعم التزامن
        AtomicInteger queryCount = new AtomicInteger(2);
        AtomicInteger getUserCount = new AtomicInteger(0);


        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Friend friend = dataSnapshot.getValue(Friend.class);
                    if (friend != null&&friend.getStatus()==Friend.ACCEPT) {
                        String id = friend.getIdUser1().equals(idUser) ? friend.getIdUser2() : friend.getIdUser1();

                        getUserCount.incrementAndGet(); // زيادة عدد عمليات جلب المستخدمين
                        userManager.getUserById(id, new UserRepository.UserCallBack<User>() {
                            @Override
                            public void onSuccess(User value) {
                                friendSet.add(value); // إضافة المستخدم إلى مجموعة الأصدقاء
                                getUserCount.decrementAndGet();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                getUserCount.decrementAndGet();
                            }
                        });
                    }
                }
                checkAndSendResult(); // التحقق من اكتمال العملية بعد الانتهاء من البيانات
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handleFailure(error.toException()); // معالجة الفشل في حالة الإلغاء
            }

            private void checkAndSendResult() {
                if (queryCount.decrementAndGet() == 0) {
                    new Thread(() -> {
                        while (getUserCount.get() > 0) {
                            try {
                                Thread.sleep(50); // الانتظار حتى تكتمل عمليات جلب المستخدمين
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        getFriendsByIdUserCallback.getFriendsByIdUserSuccess(new ArrayList<>(friendSet));
                    }).start();
                }
            }

            private void handleFailure(Exception error) {

                queryCount.decrementAndGet(); // تقليل العداد في حالة الفشل
                getUserCount.decrementAndGet(); // تقليل عدد عمليات جلب المستخدمين
                getFriendsByIdUserCallback.getFriendsByIdUserFailure(error); // إرسال الفشل
            }
        };

        query1.addListenerForSingleValueEvent(listener); // إضافة Listener للاستعلام الأول
        query2.addListenerForSingleValueEvent(listener); // إضافة Listener للاستعلام الثاني
    }


    public void DeleteFriend(String id, DeleteFriendCallback deleteFriendCallback) {
        DRef.child(id).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                deleteFriendCallback.deleteFriendSuccess();
            } else {
                deleteFriendCallback.deleteFriendFailure(task.getException());
            }
        });
    }

    public void GetFriend(String idUser1, String idUser2, IsFriendCallback isFriendCallback) {



        AtomicInteger count = new AtomicInteger(2);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Friend friend = snapshot.getValue(Friend.class);

                if (friend != null && friend.getId() != null){
                    isFriendCallback.isFriendSuccess(friend);
                }
                else if (count.decrementAndGet() == 0) isFriendCallback.isFriendSuccess(null);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (count.decrementAndGet() == 0) {
                    isFriendCallback.isFriendFailure(error.toException());
                    isFriendCallback.isFriendSuccess(null);
                }
            }
        };
        DRef.child(idUser1+"-"+idUser2).addListenerForSingleValueEvent(valueEventListener);
        DRef.child(idUser2+"-"+idUser1).addListenerForSingleValueEvent(valueEventListener);

    }

    public void DeleteFriendUser(String idUser){

        Query query1=DRef.orderByChild("idUser1").equalTo(idUser);
        Query query2=DRef.orderByChild("idUser2").equalTo(idUser);

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query1.addListenerForSingleValueEvent(valueEventListener);
        query2.addListenerForSingleValueEvent(valueEventListener);

    }

    public interface AddFriendCallback {
        void addFriendSuccess();

        void addFriendFailure(Exception e);
    }

    public interface DeleteFriendCallback {
        void deleteFriendSuccess();

        void deleteFriendFailure(Exception e);
    }

    public interface GetFriendsByIdUserCallback {
        void getFriendsByIdUserSuccess(List<User> friends);

        void getFriendsByIdUserFailure(Exception e);
    }

    public interface IsFriendCallback {
        void isFriendSuccess(Friend friend);

        void isFriendFailure(Exception e);
    }
}
