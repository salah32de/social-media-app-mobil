package com.example.socialmedia.UI.Dashboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Controller.UserManager;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.RecyclerView.UserManagementRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserManagement extends AppCompatActivity {
    private AutoCompleteTextView spinner;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_managment);

        spinner = findViewById(R.id.orderBy);
        recyclerView = findViewById(R.id.users_recycler);

        List<String> items = new ArrayList<>();
        items.add("active");
        items.add("banned");
        items.add("online");
        items.add("most interactive");
        items.add("most active");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.item_oreder_by,
                R.id.spinner_item_text,
                items);

        spinner.setAdapter(adapter);
        spinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String selectedItem = s.toString();
                CreateAdapter(selectedItem);
            }
                @Override
                public void afterTextChanged (Editable s){}


        });
        CreateAdapter("");
    }

    private void CreateAdapter(String orderBy) {
        UserManager userManager = new UserManager();
        userManager.GetUsers(new UserRepository.GetUsersCallback() {
            @Override
            public void onSuccess(List<User> userList) {
                userList = FilterUsers(orderBy, userList);
                UserManagementRecyclerView adapter = new UserManagementRecyclerView(UserManagement.this, userList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(UserManagement.this, RecyclerView.VERTICAL, false));
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

    private List<User> FilterUsers(String filter, List<User> userList) {
        switch (filter) {
            case "active":
                userList.removeIf(user -> !user.isActive());
                break;
            case "banned":
                userList.removeIf(user -> user.isActive());
                break;
            case "online":
                userList.removeIf(user -> !user.isOnline());
                break;
            case "most interactive":
                userList.sort((user1, user2) -> user2.getInteractionCount() - user1.getInteractionCount());
                break;
            case "most active":
                userList.sort((user1, user2) -> (int) (user2.getTotalActiveTime() - user1.getTotalActiveTime()));
                break;
            default:
                break;
        }
        userList.removeIf(user -> user.isAdmin());
        return userList;
    }

}