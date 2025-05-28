package com.example.socialmedia.UI.Dashboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Controller.PostManager;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.PostRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.RecyclerView.PostManagementAdapter;
import com.example.socialmedia.UI.RecyclerView.ProfileAdapter;

import java.util.ArrayList;
import java.util.List;

public class PostsManagement extends AppCompatActivity {
    private AutoCompleteTextView spinner;
    private RecyclerView recyclerView;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_management);

        back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());

        spinner = findViewById(R.id.orderBy);
        recyclerView = findViewById(R.id.posts_recycler_view);


        List<String> items = new ArrayList<>();
        items.add("older");
        items.add("newest");
        items.add("most liked");
        items.add("most commented");
        items.add("video");
        items.add("photo");
        items.add("text");
        items.add("all");

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
            public void afterTextChanged(Editable s) {
            }


        });
        CreateAdapter("");

    }

    private void CreateAdapter(String orderBy) {

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PostManager postManager = new PostManager();
        postManager.GetAllPosts(new PostRepository.GetAllPostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                posts = FilterPosts(orderBy, posts);
                recyclerView.setAdapter(new PostManagementAdapter(PostsManagement.this, posts, new ProfileAdapter.deletePostCallBack() {
                            @Override
                            public void onRemove(int position) {
                                recyclerView.getAdapter().notifyItemRemoved(position);

                            }
                        })
                );
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });



    }

    private List<Post> FilterPosts(String filter, List<Post> postList) {
        switch (filter) {

            case ("older"):
                postList.sort((post1, post2) -> (int) (post1.getDate() - post2.getDate()));
                break;
            case ("newest"):
                postList.sort((post1, post2) -> (int) (post2.getDate() - post1.getDate()));
                break;
            case ("most liked"):
                postList.sort((post1, post2) -> Math.toIntExact(post2.getLikes() - post1.getLikes()));
                break;
            case ("most commented"):
                postList.sort((post1, post2) -> Math.toIntExact(post2.getNumComments() - post1.getNumComments()));
                break;
            case ("video"):
                postList.removeIf(post -> !post.getLink().contains(".mp4"));
                break;
            case ("photo"):
                postList.removeIf(post -> !post.getLink().contains(".jpg"));
                break;
            case ("text"):
                postList.removeIf(post -> !post.getLink().isEmpty());
                break;
            case ("all"):
                break;
            default:
        }
        return postList;
    }
}