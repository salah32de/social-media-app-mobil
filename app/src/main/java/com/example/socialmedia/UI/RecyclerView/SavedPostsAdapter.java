package com.example.socialmedia.UI.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.Controller.RoomDatabaseManager;
import com.example.socialmedia.Database.LocalDatabase.Entity.PostsSavedEntity;
import com.example.socialmedia.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SavedPostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_TITLE = 0;
    private final int TYPE_POST = 1;

    private List<PostsSavedEntity> postList;
    private Context context;
    private ProfileAdapter.deletePostCallBack deletePostCallBack;

    public SavedPostsAdapter(List<PostsSavedEntity> postList, Context context, ProfileAdapter.deletePostCallBack deletePostCallBack) {
        this.postList = postList;
        this.context = context;
        this.deletePostCallBack = deletePostCallBack;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_TITLE) {
            return new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.text_view, parent, false));

        } else {
            return new SavedPostsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TitleViewHolder) {
            ((TitleViewHolder) holder).bind();
        } else {
            ((SavedPostsViewHolder) holder).bind(postList.get(position - 1), context, position, deletePostCallBack);
        }
    }

    @Override
    public int getItemCount() {
        if (postList == null)
            return 0;
        return postList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TITLE;
        } else {
            return TYPE_POST;
        }
    }

    public static class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text);
        }

        public void bind() {
            title.setText("Saved Posts");
        }
    }


    public static class SavedPostsViewHolder extends RecyclerView.ViewHolder {
        ImageView menuSitting, imagePost;
        PlayerView videoPost;
        TextView nameUser, datePublish, textPost, deletePost;
        TextView like, comment;
        SimpleExoPlayer player;
        View itemView, deletePostLayout;

        public SavedPostsViewHolder(@NonNull View itemView) {
            super(itemView);
            menuSitting = itemView.findViewById(R.id.menuSetting);
            imagePost = itemView.findViewById(R.id.imagePost);
            videoPost = itemView.findViewById(R.id.videoPost);
            nameUser = itemView.findViewById(R.id.nameUser);
            datePublish = itemView.findViewById(R.id.date);
            textPost = itemView.findViewById(R.id.textPost);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            player = new SimpleExoPlayer.Builder(itemView.getContext()).build();
            videoPost.setPlayer(player);
            this.itemView = itemView;
            deletePost = itemView.findViewById(R.id.deletePost);
            deletePostLayout = itemView.findViewById(R.id.deletePostLayout);
        }

        public void bind(PostsSavedEntity post, Context context, int position, ProfileAdapter.deletePostCallBack deletePostCallBack)  {

            if (post.getLink().endsWith(".mp4")) {
                imagePost.setVisibility(View.GONE);
                videoPost.setVisibility(View.VISIBLE);


                Uri uri = Uri.parse(post.getLink()); // استخدم Uri بدلاً من URI
                MediaItem mediaItem = MediaItem.fromUri(uri);
                player.setMediaItem(mediaItem);
                player.prepare();

            } else if (post.getLink().endsWith(".jpg") || post.getLink().endsWith(".png")) {
                videoPost.setVisibility(View.GONE);
                imagePost.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(post.getLink())
                        .placeholder(R.drawable.edittext_background)
                        .into(imagePost);

            } else {
                imagePost.setVisibility(View.GONE);
                videoPost.setVisibility(View.GONE);
            }



            textPost.setText(post.getText());
            nameUser.setText("????");
            datePublish.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(post.getDate()));

            like.setText(post.getLikes() + "");
            comment.setText(post.getNumComments() + "");


            menuSitting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePostLayout.setVisibility(View.VISIBLE);
                }
            });


            deletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(() -> {

                        RoomDatabaseManager roomDatabaseManager = new RoomDatabaseManager(context);
                        roomDatabaseManager.deletePost(post.getPostId());

                    }).start();

                    deletePostCallBack.onRemove(position);
                    deletePostLayout.setVisibility(View.GONE);
                    Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show();
                }
            });


        }

    }
}

