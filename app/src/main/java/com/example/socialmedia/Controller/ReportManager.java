package com.example.socialmedia.Controller;

import android.util.Log;

import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.ReportRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Report;

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

    public void DeleteReportById(String reportId,ReportRepository.DeleteReportByIdCallback callback){
        repository.DeleteReportById(reportId, new ReportRepository.DeleteReportByIdCallback() {
            @Override
            public void deleteReportByIdSuccess() {
                Log.d(TAG,"delete report by id success .report id: "+reportId);
                callback.deleteReportByIdSuccess();
            }

            @Override
            public void deleteReportByIdFailure(Exception e) {
                Log.d(TAG,"delete report by id failure .error: "+e.getMessage());
                callback.deleteReportByIdFailure(e);
            }
        });
    }

    public void GetNumOfReport(ReportRepository.GetNumOfReportCallback callback){
        repository.GetNumOfReport(new ReportRepository.GetNumOfReportCallback() {
            @Override
            public void getNumOfReportSuccess(long numOfReport) {
                Log.d(TAG, "get num of report success .num of report: " + numOfReport);
                callback.getNumOfReportSuccess(numOfReport);

            }

            @Override
            public void getNumOfReportFailure(Exception e) {
                Log.d(TAG,"get num of report failure .error: "+e.getMessage());
                callback.getNumOfReportFailure(e);
            }
        });
    }

    public void NumReportOfToday(ReportRepository.NumReportOfToday callback){
        repository.NumReportOfToday(new ReportRepository.NumReportOfToday() {
            @Override
            public void onSuccess(long num) {
                Log.d(TAG, "get num of report of today success .num of report: " + num);
                callback.onSuccess(num);
            }
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "get num of report of today failure .error: " + e.getMessage());
                callback.onFailure(e);
            }
        });
    }


}
