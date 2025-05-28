package com.example.socialmedia.UI.Activity.Setting;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Controller.RoomDatabaseManager;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Database.LocalDatabase.Entity.PostsSavedEntity;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.RecyclerView.ProfileAdapter;
import com.example.socialmedia.UI.RecyclerView.SavedPostsAdapter;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SavedPosts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_saved_posts);
        RecyclerView recyclerView=findViewById(R.id.savedPostsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));





        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            RoomDatabaseManager roomDatabaseManager=new RoomDatabaseManager(getApplicationContext());
            List<PostsSavedEntity> list = roomDatabaseManager.getPostsSavesUser(
                    SharedPreferencesHelper.getUser(getApplicationContext()).getId()
            );

            runOnUiThread(() -> {
                recyclerView.setAdapter(new SavedPostsAdapter(list, getApplicationContext(), new ProfileAdapter.deletePostCallBack() {
                    @Override
                    public void onRemove(int position) {
                        list.remove(position);
                        recyclerView.getAdapter().notifyItemRemoved(position);
                    }
                }));
            });
        });

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}