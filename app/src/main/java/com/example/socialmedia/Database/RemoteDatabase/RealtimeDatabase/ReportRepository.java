package com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase;

import androidx.annotation.NonNull;

import com.example.socialmedia.Database.RemoteDatabase.Entity.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportRepository {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;

    public ReportRepository() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("report");

    }

    public void AddReport(Report report, AddReportCallBack callBack) {
        report.setReportId(ref.push().getKey());
        ref.child(report.getReportId()).setValue(report).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callBack.addReportSuccess();
                    return;
                }else{
                    callBack.addReportFailure(task.getException());
                }

            }
        });
    }

    public void GetReports(GetReportsCallBack callBack){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Report> reportList=new ArrayList<>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Report report=dataSnapshot.getValue(Report.class);
                    reportList.add(report);
                }
                callBack.GetReportsSuccess(reportList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.GetReportsFailure(error.toException());
            }
        });
    }

    public void DeleteReportById(String reportId,DeleteReportByIdCallback callback){
        ref.child(reportId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    callback.deleteReportByIdSuccess();
                }else{
                    callback.deleteReportByIdFailure(task.getException());
                }
            }
        });
    }
    public void GetNumOfReport(GetNumOfReportCallback callback){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long numOfReport=snapshot.getChildrenCount();
                callback.getNumOfReportSuccess(numOfReport);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.getNumOfReportFailure(error.toException());
            }
        });
    }

    public void NumReportOfToday(NumReportOfToday callback){

        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfDay = calendar.getTimeInMillis();


        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long endOfDay = calendar.getTimeInMillis();
        Query query=ref.orderByChild("reportTimestamp").startAt(startOfDay).endAt(endOfDay);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onSuccess(snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    public interface NumReportOfToday{
        void onSuccess(long num);
        void onFailure(Exception e);
    }

    public interface AddReportCallBack {
        void addReportSuccess();

        void addReportFailure(Exception e);
    }
    public interface GetReportsCallBack{
        void GetReportsSuccess(List<Report> list);
        void GetReportsFailure(Exception e);
    }
    public interface DeleteReportByIdCallback{
        void deleteReportByIdSuccess();
        void deleteReportByIdFailure(Exception e);
    }
    public interface GetNumOfReportCallback{
        void getNumOfReportSuccess(long numOfReport);
        void getNumOfReportFailure(Exception e);
    }
}
