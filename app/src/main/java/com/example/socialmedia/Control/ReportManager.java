package com.example.socialmedia.Control;

import android.util.Log;

import com.example.socialmedia.Data.Firebase.RealtimeDatabase.ReportRepository;
import com.example.socialmedia.Model.Report;

import java.util.List;

public class ReportManager {
    private final String TAG="TAG: report Manager";
    private ReportRepository repository;
    public ReportManager(){
        repository=new ReportRepository();
    }

    public void AddReport(Report report, ReportRepository.AddReportCallBack callBack){
        repository.AddReport(report, new ReportRepository.AddReportCallBack() {
            @Override
            public void addReportSuccess() {
                Log.d(TAG, "add report success .report contain: "+report.getReportContent());
                callBack.addReportSuccess();
            }

            @Override
            public void addReportFailure(Exception e) {
                Log.d(TAG,"add report failure .error: "+e.getMessage());
                callBack.addReportFailure(e);
            }
        });
    }
    public void GetReports(ReportRepository.GetReportsCallBack callBack){
        repository.GetReports(new ReportRepository.GetReportsCallBack() {
            @Override
            public void GetReportsSuccess(List<Report> list) {
                Log.d(TAG,"get reports success .list size: "+list.size());
                callBack.GetReportsSuccess(list);
            }

            @Override
            public void GetReportsFailure(Exception e) {
                Log.d(TAG,"get reports failure .error: "+e.getMessage());
                callBack.GetReportsFailure(e);
            }
        });
    }

}
