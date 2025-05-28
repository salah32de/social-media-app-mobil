package com.example.socialmedia.UI.RecyclerView;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.Controller.FriendManager;
import com.example.socialmedia.Controller.NotificationManager;
import com.example.socialmedia.Controller.PostManager;
import com.example.socialmedia.Controller.RoomDatabaseManager;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.FriendRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.NotificationRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.PostRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Friend;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Notification;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Report;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.Chat.MessagesActivity;
import com.example.socialmedia.UI.Activity.Profile;
import com.example.socialmedia.UI.Fragment.CommentFragment;
import com.example.socialmedia.UI.Fragment.ReportFragment;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> postList;
    private Context context;
    private User userProfile;
    private deletePostCallBack deletePostCallBack;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FRIENDS = 1;
    private static final int TYPE_POST = 2;

    public ProfileAdapter(User user, Context context, List<Post> postList, deletePostCallBack deletePostCallBack) {
        this.userProfile = user;
        this.context = context;
        this.postList = postList;
        this.deletePostCallBack=deletePostCallBack;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.head_profile, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.post_item, parent, false);
            return new PostViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(userProfile, context);
        } else {
            ((PostViewHolder) holder).bind(postList.get(position - 1), context, postList,position,deletePostCallBack);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;
        else if (position == 1) return TYPE_FRIENDS;
        else return TYPE_POST;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        ImageView imageProfile, back, setting;
        TextView nameUser, text, addFriend, deleteFriend, sendMessage, acceptFriendRequest, rejectFriendRequest, report;
        RecyclerView showFriendRecyclerView;
        Friend friend;
        CardView reportLayout;


        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            nameUser = itemView.findViewById(R.id.nameUser);
            text = itemView.findViewById(R.id.textAboutMyself);
            back = itemView.findViewById(R.id.back);
            addFriend = itemView.findViewById(R.id.addFriend);
            deleteFriend = itemView.findViewById(R.id.deleteFriend);
            acceptFriendRequest = itemView.findViewById(R.id.accept);
            rejectFriendRequest = itemView.findViewById(R.id.reject);
            sendMessage = itemView.findViewById(R.id.sendMessage);
            showFriendRecyclerView = itemView.findViewById(R.id.showFriendsRecyclerView);
            reportLayout = itemView.findViewById(R.id.reportLayout);
            report = itemView.findViewById(R.id.report);
            setting = itemView.findViewById(R.id.setting);
        }

        public void bind(User userProfile, Context context) {
            User user = SharedPreferencesHelper.getUser(context);

            FriendManager friendManager = new FriendManager();
            showFriendRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            friendManager.GetFriendsByIdUser(userProfile.getId(), new FriendRepository.GetFriendsByIdUserCallback() {
                @Override
                public void getFriendsByIdUserSuccess(List<User> fr) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showFriendRecyclerView.setAdapter(new FriendAdapter(context, fr));
                        }
                    });

                }

                @Override
                public void getFriendsByIdUserFailure(Exception e) {
                }
            });

            Log.d("alspdlpasdsa",user.getPhotoProfile().isEmpty()+"");

            if (!userProfile.getPhotoProfile().isEmpty())
                Glide.with(context).load(userProfile.getPhotoProfile()).circleCrop().placeholder(R.drawable.wait_download).into(imageProfile);
            nameUser.setText(userProfile.getName());
            text.setText(userProfile.getBio());
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AppCompatActivity) context).finish();
                }
            });

            if (userProfile.equals(user)) {
                sendMessage.setVisibility(View.GONE);
                addFriend.setVisibility(View.GONE);
                sendMessage.setVisibility(View.GONE);
                rejectFriendRequest.setVisibility(View.GONE);
                acceptFriendRequest.setVisibility(View.GONE);
            } else {
                sendMessage.setVisibility(View.VISIBLE);

                sendMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, MessagesActivity.class);
                        intent.putExtra(SharedPreferencesHelper.USER_KEY, userProfile);
                        (context).startActivity(intent);
                    }
                });

                friendManager.GetFriend(user.getId(), userProfile.getId(), new FriendRepository.IsFriendCallback() {
                    @Override
                    public void isFriendSuccess(Friend fr) {
                        if (fr != null && fr.getId() != null) {

                            if (fr.getStatus() == Friend.REQUEST) {
                                if (user.getId().equals(fr.getIdUser2())) {
                                    acceptFriendRequest.setVisibility(View.VISIBLE);
                                    rejectFriendRequest.setVisibility(View.VISIBLE);
                                    addFriend.setVisibility(View.GONE);
                                    deleteFriend.setVisibility(View.GONE);
                                } else {
                                    rejectFriendRequest.setVisibility(View.GONE);
                                    acceptFriendRequest.setVisibility(View.GONE);
                                    deleteFriend.setVisibility(View.GONE);
                                    addFriend.setVisibility(View.VISIBLE);
                                }
                            } else if (fr.getStatus() == Friend.ACCEPT) {
                                deleteFriend.setVisibility(View.VISIBLE);
                                addFriend.setVisibility(View.GONE);
                                acceptFriendRequest.setVisibility(View.GONE);
                                rejectFriendRequest.setVisibility(View.GONE);
                            }
                            friend = fr;
                        } else {
                            rejectFriendRequest.setVisibility(View.GONE);
                            acceptFriendRequest.setVisibility(View.GONE);
                            deleteFriend.setVisibility(View.GONE);
                            addFriend.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void isFriendFailure(Exception e) {
                    }
                });

            }

            View.OnClickListener addFriendListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Friend fri;
                    if (v.getId() == R.id.addFriend) {
                        fri = new Friend(user.getId(), userProfile.getId(), Friend.REQUEST);
                    } else {
                        fri = friend;
                        fri.setStatus(Friend.ACCEPT);
                    }
                    FriendManager friendManager = new FriendManager();
                    friendManager.AddFriend(fri, new FriendRepository.AddFriendCallback() {
                        @Override
                        public void addFriendSuccess() {
                            Toast.makeText(context, "add friend success", Toast.LENGTH_SHORT).show();
                            if (v.getId() == R.id.accept) {
                                addFriend.setVisibility(View.GONE);
                                acceptFriendRequest.setVisibility(View.GONE);
                                rejectFriendRequest.setVisibility(View.GONE);
                                deleteFriend.setVisibility(View.VISIBLE);
                            }
                            NotificationManager notificationManager = new NotificationManager();
                            Notification notification = new Notification(user.getId(), userProfile.getId(), (v.getId() == R.id.addFriend) ? Notification.FRIEND_REQUEST : Notification.ACCEPT_FRIEND_REQUEST);
                            notificationManager.addNotification(context,notification, userProfile.getId(), new NotificationRepository.AddNotificationCallback() {
                                @Override
                                public void addNotificationSuccess() {
                                }

                                @Override
                                public void addNotificationFailure(Exception e) {
                                }
                            });
                        }

                        @Override
                        public void addFriendFailure(Exception e) {
                            Toast.makeText(context, "add friend failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            };

            addFriend.setOnClickListener(addFriendListener);
            acceptFriendRequest.setOnClickListener(addFriendListener);


            View.OnClickListener deleteFreindListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friendManager.DeleteFriend(friend.getId(), new FriendRepository.DeleteFriendCallback() {
                        @Override
                        public void deleteFriendSuccess() {
                            if (v.getId() == R.id.deleteFriend) {
                                Toast.makeText(context, "delete friend success", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "reject request success", Toast.LENGTH_SHORT).show();
                            }
                            addFriend.setVisibility(View.VISIBLE);
                            acceptFriendRequest.setVisibility(View.GONE);
                            rejectFriendRequest.setVisibility(View.GONE);
                            deleteFriend.setVisibility(View.GONE);
                        }

                        @Override
                        public void deleteFriendFailure(Exception e) {
                            if (v.getId() == R.id.deleteFriend) {
                                Toast.makeText(context, "delete friend failure", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "reject request failure", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            };


            deleteFriend.setOnClickListener(deleteFreindListener);
            rejectFriendRequest.setOnClickListener(deleteFreindListener);

            setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!userProfile.equals(user)) {
                        reportLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //show the report fragment
                    View fragment = ((AppCompatActivity) context).findViewById(R.id.reportFragment);
                    fragment.setVisibility(View.VISIBLE);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    ReportFragment reportFragment = new ReportFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("reporterUser", userProfile);
                    bundle.putSerializable("reportedItemId", userProfile.getId());
                    bundle.putSerializable("reportItem", Report.ReportItem.USER);
                    reportFragment.setArguments(bundle);
                    transaction.replace(R.id.reportFragment, reportFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });


        }


    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile, menuSitting, imagePost;
        PlayerView videoPost;
        TextView nameUser, datePublish, textPost, deletePost;
        TextView like, comment, save, reportPost;
        SimpleExoPlayer player;
        View itemView, reportPostLayout, deletePostLayout;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.userImage);
            menuSitting = itemView.findViewById(R.id.menuSetting);
            imagePost = itemView.findViewById(R.id.imagePost);
            videoPost = itemView.findViewById(R.id.videoPost);
            nameUser = itemView.findViewById(R.id.nameUser);
            datePublish = itemView.findViewById(R.id.date);
            textPost = itemView.findViewById(R.id.textPost);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            player = new SimpleExoPlayer.Builder(itemView.getContext()).build();
            videoPost.setPlayer(player);
            this.itemView = itemView;
            reportPost = itemView.findViewById(R.id.reportPost);
            reportPostLayout = itemView.findViewById(R.id.reportLayout);
            deletePost = itemView.findViewById(R.id.deletePost);
            deletePostLayout = itemView.findViewById(R.id.deletePostLayout);
        }

        public void bind(Post post, Context context, List<Post> postList,int position, deletePostCallBack deletePostCallBack) {
            if (!post.getUserCreatePost().getPhotoProfile().isEmpty())
                Glide.with(context).load(post.getUserCreatePost().getPhotoProfile()).circleCrop().placeholder(R.drawable.wait_download).into(imageProfile);
            if (post.getLink().contains(".mp4")) {
                imagePost.setVisibility(View.GONE);
                videoPost.setVisibility(View.VISIBLE);


                MediaItem mediaItem = MediaItem.fromUri(post.getLink());
                player.setMediaItem(mediaItem);
                player.prepare();


            } else if (post.getLink().contains(".jpg") || post.getLink().contains(".png")) {
                videoPost.setVisibility(View.GONE);
                imagePost.setVisibility(View.VISIBLE);
                Glide.with(context).load(post.getLink()).placeholder(R.drawable.edittext_background).into(imagePost);
            } else {
                imagePost.setVisibility(View.GONE);
                videoPost.setVisibility(View.GONE);
            }
            textPost.setText(post.getText());
            nameUser.setText(post.getUserCreatePost().getName());
            datePublish.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(post.getDate()));
            //button
            like.setText(post.getLikes() + "");
            comment.setText(post.getNumComments() + "");

            if (!post.getUserCreatePost().equals(SharedPreferencesHelper.getUser(context))) {
                imageProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Profile.class);
                        intent.putExtra(SharedPreferencesHelper.USER_KEY, post.getUserCreatePost());
                        (context).startActivity(intent);
                    }
                });
            }
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    like.setClickable(false);
                    PostManager postManager = new PostManager();
                    postManager.addNumLikeOrCommentInPost(post, context, PostRepository.ADD_NUM_LIKES_IN_POST, new PostRepository.AddLikeInPostCallback() {
                        @Override
                        public void addLikeInPostSuccess(long numberLike) {
                            post.setLikes((int) numberLike);
                            like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_is_click, 0, 0, 0);
                            TypedValue typedValue = new TypedValue();
                            context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);
                            like.setTextColor(typedValue.data);
                            context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
                            like.setBackgroundColor(typedValue.data);
                            like.setText(numberLike + "");
                        }

                        @Override
                        public void addLikeInPostFailure() {
                            like.setClickable(true);

                        }
                    });
                }
            });


            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View fragment = ((AppCompatActivity) context).findViewById(R.id.CommentFragment);
                    fragment.setVisibility(View.VISIBLE);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    CommentFragment commentFragment = new CommentFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("post", post);
                    commentFragment.setArguments(bundle);
                    transaction.replace(R.id.CommentFragment, commentFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

            menuSitting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (post.getUserCreatePost().equals(SharedPreferencesHelper.getUser(context))|| SharedPreferencesHelper.getUser(context).isAdmin())
                        deletePostLayout.setVisibility(View.VISIBLE);
                    else
                        reportPostLayout.setVisibility(View.VISIBLE);

                }
            });

            deletePostLayout.setVisibility(View.GONE);
            reportPostLayout.setVisibility(View.GONE);

            reportPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportPostLayout.setVisibility(View.GONE);

                    //show the report fragment
                    View fragment = ((AppCompatActivity) context).findViewById(R.id.reportFragment);
                    fragment.setVisibility(View.VISIBLE);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    ReportFragment reportFragment = new ReportFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("reporterUser", post.getUserCreatePost());
                    bundle.putSerializable("reportedItemId", post.getIdPost());
                    bundle.putSerializable("reportItem", Report.ReportItem.POST);
                    reportFragment.setArguments(bundle);
                    transaction.replace(R.id.reportFragment, reportFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

            deletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostManager postManager = new PostManager();
                    postManager.DeletePostById(post.getUserCreatePost().getId(), post, new PostRepository.DeletePostCallBack() {
                        @Override
                        public void deletePostSuccess() {
                            Toast.makeText(context, "delete post successful", Toast.LENGTH_SHORT).show();
                            postList.remove(post);
                            deletePostCallBack.onRemove(position);
                            //      for update list post and remove the post in adapter                      notifyDataSetChanged();
                        }

                        @Override
                        public void deletePostsFailure(Exception e) {
                            Toast.makeText(context, "delete post failure .try again another time", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Executors.newSingleThreadExecutor().execute(()->{
                                RoomDatabaseManager roomDatabaseManager=new RoomDatabaseManager(context);
                                roomDatabaseManager.SavePostInLocalDB(post);

                            });
                    Toast.makeText(context, "save post successful", Toast.LENGTH_SHORT).show();
                }
            });


        }

    }

    public interface deletePostCallBack {
        void onRemove(int position);
    }

}
