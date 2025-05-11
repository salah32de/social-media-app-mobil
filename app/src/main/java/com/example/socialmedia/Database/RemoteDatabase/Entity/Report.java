package com.example.socialmedia.Database.RemoteDatabase.Entity;

public class Report {

    public enum ReportCategory {
        ABUSE,
        FRAUD,
        INAPPROPRIATE
    }
    public enum ReportItem{
        USER,
        POST
    }
    private String reportId;
    private String reporterUserId;
    private String reportedUserId;
    private String reportedItemId;
    private String reportContent;
    private ReportCategory reportCategory;
    private ReportItem reportItem;
    private long reportTimestamp;


    // Constructor
    public Report(){}
    public Report(String reporterUserId, String reportedUserId, String reportedItemId,String reportContent, ReportCategory reportCategory,ReportItem reportItem) {
        this.reporterUserId = reporterUserId;
        this.reportedUserId = reportedUserId;
        this.reportedItemId = reportedItemId;
        this.reportContent = reportContent;
        this.reportCategory=reportCategory;
        this.reportItem=reportItem;
        this.reportTimestamp = System.currentTimeMillis();
     }

    public ReportItem getReportItem() {
        return reportItem;
    }

    public void setReportItem(ReportItem reportItem) {
        this.reportItem = reportItem;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReporterUserId() {
        return reporterUserId;
    }

    public void setReporterUserId(String reporterUserId) {
        this.reporterUserId = reporterUserId;
    }

    public String getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(String reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public String getReportedItemId() {
        return reportedItemId;
    }

    public void setReportedItemId(String reportedItemId) {
        this.reportedItemId = reportedItemId;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }


    public ReportCategory getReportCategory() {
        return reportCategory;
    }

    public void setReportCategory(ReportCategory reportCategory) {
        this.reportCategory = reportCategory;
    }

    public long getReportTimestamp() {
        return reportTimestamp;
    }

    public void setReportTimestamp(long reportTimestamp) {
        this.reportTimestamp = reportTimestamp;
    }

}
