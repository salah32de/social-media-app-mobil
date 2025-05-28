package com.example.socialmedia.UI.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.R;

import java.util.List;

public class PostManagementAdapter extends RecyclerView.Adapter<ProfileAdapter.PostViewHolder> {
    private ProfileAdapter.deletePostCallBack callBack;
    private Context context;
    private List<Post> postList;

    public PostManagementAdapter(Context context, List<Post> postList, ProfileAdapter.deletePostCallBack callBack) {
        this.context = context;
        this.postList = postList;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public ProfileAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProfileAdapter.PostViewHolder(LayoutInflater.from(context).inflate(R.layout.post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.PostViewHolder holder, int position) {
        holder.bind(postList.get(position), context, postList, position,callBack);
    }

    @Override
    public int getItemCount() {
        if (postList == null)
            return 0;
        return postList.size();
    }
}
