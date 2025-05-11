package com.example.socialmedia.UI.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Controller.UserManager;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.RecyclerView.ShowUserAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class SearchFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchFragment newInstance(int columnCount) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);

        //initialize the recycler view
        RecyclerView recyclerView = view.findViewById(R.id.searchRecyclerView);
        EditText searchEditText = view.findViewById(R.id.searchEditText);


        // initialize the handler and the runnable
        Handler handler = new Handler();//for delay the search
        final Runnable[] searchRunnable = {null};//for cancel the search if the user type again

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // cancel the search if the user type again
                if (searchRunnable[0] != null) {//cancel the search if the user type again when the search is running
                    handler.removeCallbacks(searchRunnable[0]);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // start the search after 0.5 seconds
                searchRunnable[0] = () -> {
                    String username = s.toString().trim();
                    if (!username.isEmpty()) { // when the edite text is not empty
                        UserManager userManager = new UserManager();
                        userManager.SearchUserByName(username, new UserRepository.UserCallBack<List<User>>() {
                            @Override
                            public void onSuccess(List<User> value) {
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                ShowUserAdapter adapter = new ShowUserAdapter(getActivity(), getContext(), value);
                                recyclerView.setAdapter(adapter);
                            }

                            @Override
                            public void onFailure(Exception e) {
                            }
                        });
                    } else {//if the edite text is empty the recycler view will be empty
                        recyclerView.setAdapter(new ShowUserAdapter(getActivity(), getContext(), new ArrayList<>()));
                    }
                };
                handler.postDelayed(searchRunnable[0], 500); // pause 0.5 seconds
            }
        });

        ImageView back=view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().findViewById(R.id.searchFragment).setVisibility(View.GONE);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        return view;
    }
}