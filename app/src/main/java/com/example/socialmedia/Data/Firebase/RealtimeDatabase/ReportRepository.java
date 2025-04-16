package com.example.socialmedia.Data.Firebase.RealtimeDatabase;

import androidx.annotation.NonNull;

import com.example.socialmedia.Model.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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


    public interface AddReportCallBack {
        void addReportSuccess();

        void addReportFailure(Exception e);
    }
    public interface GetReportsCallBack{
        void GetReportsSuccess(List<Report> list);
        void GetReportsFailure(Exception e);
    }
}
