package com.example.socialmedia.UI.Dashboard;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Controller.ReportManager;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.ReportRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Report;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.RecyclerView.ReviewReportAdapter;

import java.util.List;

public class ReviewReports extends AppCompatActivity {
    private User admen;
    private ImageView back;
    private List<Report> reportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_reports);

        back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.reports_recycler);

        ReportManager reportManager = new ReportManager();


        reportManager.GetReports(new ReportRepository.GetReportsCallBack() {
            @Override
            public void GetReportsSuccess(List<Report> list) {
                recyclerView.setAdapter(new ReviewReportAdapter(ReviewReports.this, list));
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

            }

            @Override
            public void GetReportsFailure(Exception e) {

            }
        });


    }
}