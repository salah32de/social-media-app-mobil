package com.example.socialmedia.UI.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.socialmedia.Controller.ReportManager;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.ReportRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Report;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_report, container, false);

        User reporterUser= SharedPreferencesHelper.getUser(getContext());
        User reportedUser=(User) getArguments().getSerializable("reporterUser");
        String reportedItemId=(String) getArguments().getSerializable("reportedItemId");
        Report.ReportItem reportItem=(Report.ReportItem) getArguments().getSerializable("reportItem");
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        AutoCompleteTextView spinnerReportType = view.findViewById(R.id.spinnerReportType);

        String[] reportTypes = {String.valueOf(Report.ReportCategory.ABUSE), String.valueOf(Report.ReportCategory.FRAUD), String.valueOf(Report.ReportCategory.INAPPROPRIATE)};


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.report_item, reportTypes);
        spinnerReportType.setAdapter(adapter);


        Button sendReport = view.findViewById(R.id.btnSubmitReport);
        TextInputEditText reportContain = view.findViewById(R.id.editTextReportContent);
        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typeReport = spinnerReportType.getText().toString();
                String contain = reportContain.getText().toString();

                if (typeReport.isEmpty()) {
                    spinnerReportType.setError("please select report type");
                }
                if (contain.isEmpty()) {
                    reportContain.setError("please enter report content");
                }

                if(!contain.isEmpty()&&!typeReport.isEmpty()){
                    Report report=new Report(reporterUser.getId(),reportedUser.getId(),reportedItemId,contain,Report.ReportCategory.valueOf(typeReport),reportItem);

                    ReportManager reportManager=new ReportManager();
                    reportManager.AddReport(report, new ReportRepository.AddReportCallBack() {
                        @Override
                        public void addReportSuccess() {
                            Toast.makeText(getContext(), "send report success", Toast.LENGTH_SHORT).show();
                            onDestroy();
                        }

                        @Override
                        public void addReportFailure(Exception e) {
                            Toast.makeText(getContext(), "send report failure .try again another time", Toast.LENGTH_SHORT).show();
                            onDestroy();
                        }
                    });

                }

            }
        });

        deleteMessageError(view);
        return view;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((AppCompatActivity) getContext()).findViewById(R.id.reportFragment).setVisibility(View.GONE);
        getParentFragmentManager().popBackStack();

    }

    private void deleteMessageError(View view) {

        AutoCompleteTextView spinnerReportType = view.findViewById(R.id.spinnerReportType);
        TextInputLayout inputLayoutReportType = view.findViewById(R.id.inputLayoutReportType);
        TextInputEditText reportContain = view.findViewById(R.id.editTextReportContent);

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (v.getId() == spinnerReportType.getId()) {
                    spinnerReportType.setError(null);
                } else if (v.getId() == inputLayoutReportType.getId()||v.getId()==reportContain.getId()) {
                    inputLayoutReportType.setError(null);
                    reportContain.setError(null);
                }
                    return false;
            }

        };
        spinnerReportType.setOnTouchListener(touchListener);
        inputLayoutReportType.setOnTouchListener(touchListener);
        reportContain.setOnTouchListener(touchListener);

    }
}