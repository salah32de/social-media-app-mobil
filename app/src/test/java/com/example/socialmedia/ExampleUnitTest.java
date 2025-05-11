package com.example.socialmedia;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import com.example.socialmedia.Controller.ReportManager;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.ReportRepository;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testDeleteReportById_success() {



        ReportManager reportManager = new ReportManager();
        String testReportId = "-OO-gJCumoWPibNJ9Kp9";

        final boolean[] callbackCalled = {false};

        reportManager.DeleteReportById(testReportId, new ReportRepository.DeleteReportByIdCallback() {
            @Override
            public void deleteReportByIdSuccess() {
                Log.d("Test", "Delete success");
                callbackCalled[0] = true;
                // Assertion بعد نجاح الحذف
                assertTrue(true);
            }

            @Override
            public void deleteReportByIdFailure(Exception e) {
                Log.d("Test", "Delete failed: " + e.getMessage());
                fail("Delete failed: " + e.getMessage());
            }
        });

        // ✳️ تنتظر قليلاً (مؤقتًا) عشان الكولباك ينفذ
        try {
            Thread.sleep(3000); // مش أفضل طريقة لكنها مؤقتًا تنفع للاختبار البسيط
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue("Callback was not called", callbackCalled[0]);
    }
}