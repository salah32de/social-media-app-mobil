package com.example.socialmedia.UI.RecyclerView;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.Controller.CommentManager;
import com.example.socialmedia.Controller.PostManager;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.CommentRepository;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.PostRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Comment;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Fragment.CommentFragment;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //types of view in recycler view
    private final int TYPE_BACK = 0;
    private final int TYPE_POST = 1;
    private final int TYPE_COMMENT = 2;
    private List<Comment> commentList;//list of comments
    private Context context;//context of activity
    private Post post;//post of comment
    private View fragmentView;//view of fragment

    public CommentAdapter(Context context, Post post, List<Comment> list, View fragmentView) {
        this.context = context;
        this.commentList = list;
        this.post = post;
        this.fragmentView = fragmentView;
    }


    @NonNull
    @Override//create a view holder when needed
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (viewType == TYPE_BACK) {
            view = inflater.inflate(R.layout.back_view, parent, false);
            return new BackViewHolder(view);
        } else if (viewType == TYPE_POST) {
            view = inflater.inflate(R.layout.post_item, parent, false);
            return new PostViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.comment_view, parent, false);
            return new CommentViewHolder(view);
        }

    }

    //bind data to view holder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BackViewHolder) {
            ((BackViewHolder) holder).bind(context);
        } else if (holder instanceof PostViewHolder) {
            ((PostViewHolder) holder).bind(post, context);
        } else {
            ((CommentViewHolder) holder).bind(post, commentList.get(position - 2), context, fragmentView, position - 2, commentList);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size() + 2;
    }


    //return type of view based on position
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_BACK;
        else if (position == 1) return TYPE_POST;
        else return TYPE_COMMENT;

    }

    // ViewHolder button back
    public static class BackViewHolder extends RecyclerView.ViewHolder {
        private ImageView back;

        public BackViewHolder(@NonNull View itemView) {
            super(itemView);
            back = itemView.findViewById(R.id.back);
        }

        public void bind(Context context) {
            // When click the back button, hide the Fragment and return to the previous screen.
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    // ViewHolder for the post
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile, menu, imagePost;
        PlayerView videoPost;
        TextView nameUser, datePublish, textPost;
        TextView like, comment, save;
        SimpleExoPlayer player;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            //initialize the views post
            imageProfile = itemView.findViewById(R.id.userImage);
            menu = itemView.findViewById(R.id.menuSetting);
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
        }

        //function to reset view
        public void reset() {
            imageProfile.setImageDrawable(null);
            imagePost.setVisibility(View.GONE);
            videoPost.setVisibility(View.GONE);
            nameUser.setText("");
            datePublish.setText("");
            textPost.setText("");
            like.setText("0");
            comment.setText("0");
            if (player != null) {
                player.stop();
                player.clearMediaItems();
            }
        }

        public void bind(Post post, Context context) {
            reset();
            //download the image user if exist
            if (!post.getUserCreatePost().getPhotoProfile().isEmpty()) {
                Glide.with(context).load(post.getUserCreatePost().getPhotoProfile()).circleCrop().placeholder(R.drawable.wait_download).into(imageProfile);
            } else {
                Glide.with(context).load(R.drawable.user_cicrle_duotone).circleCrop().placeholder(R.drawable.wait_download).into(imageProfile);
            }

            // specify type the link
            if (post.getLink().contains(".mp4")) {// video
                imagePost.setVisibility(View.GONE);
                videoPost.setVisibility(View.VISIBLE);
                MediaItem mediaItem = MediaItem.fromUri(post.getLink());
                player.setMediaItem(mediaItem);
                player.prepare();
            } else if (post.getLink().contains(".jpg") || post.getLink().contains(".png")) {//if image
                videoPost.setVisibility(View.GONE);
                imagePost.setVisibility(View.VISIBLE);
                Glide.with(context).load(post.getLink()).placeholder(R.drawable.wait_download).into(imagePost);//download the image use
            } else {//not exist .hide view
                imagePost.setVisibility(View.GONE);
                videoPost.setVisibility(View.GONE);
            }

            //set text ,name user and date publish
            textPost.setText(post.getText());
            nameUser.setText(post.getUserCreatePost().getName());
            datePublish.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(post.getDate()));

            // set number like and comment
            like.setText(post.getLikes() + "");
            comment.setText(post.getNumComments() + "");

            // add like to post
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    like.setClickable(false);//can't click the button like again

                    PostManager postManager = new PostManager();
                    postManager.addNumLikeOrCommentInPost(post, context, PostRepository.ADD_NUM_LIKES_IN_POST, new PostRepository.AddLikeInPostCallback() {
                        @Override
                        public void addLikeInPostSuccess(long numberLike) {
                            post.setLikes((int) numberLike);//set num like
                            like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_is_click, 0, 0, 0);//set icon the like when the button like is click
                            TypedValue typedValue = new TypedValue();
                            context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);//take the color on priamry from them
                            like.setTextColor(typedValue.data);//set the color text
                            context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);//take the color primary from them
                            like.setBackgroundColor(typedValue.data);//set the background
                            like.setText(numberLike + "");//set number like
                            like.setClickable(false);//can't click the button like
                        }

                        @Override
                        public void addLikeInPostFailure() {
                        }
                    });
                }
            });
        }
    }

    // ViewHolder for comment x
    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView userPhoto, imageComment;
        TextView nameUser, date, textComment, like, reply, showReply;
        LinearLayout mainLayout;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            userPhoto = itemView.findViewById(R.id.imageUser);
            imageComment = itemView.findViewById(R.id.imageComment);
            nameUser = itemView.findViewById(R.id.nameUser);
            date = itemView.findViewById(R.id.dateComment);
            textComment = itemView.findViewById(R.id.textComment);
            like = itemView.findViewById(R.id.likeComment);
            reply = itemView.findViewById(R.id.reply);
            showReply = itemView.findViewById(R.id.showReply);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }

        public void reset() {
            userPhoto.setImageDrawable(null);
            imageComment.setImageDrawable(null);
            imageComment.setVisibility(View.GONE);

            nameUser.setText("");
            date.setText("");
            textComment.setText("");
            showReply.setVisibility(View.GONE);

            like.setClickable(true);
            reply.setClickable(true);
            showReply.setClickable(true);
        }

        public void bind(Post post, Comment comment, Context context, View fragmentView, int position, List<Comment> commentList) {
            reset();
            //if the comment is reply, add padding
            if (comment.getIdParent() != null && !comment.getIdParent().isEmpty()) {
                mainLayout.setPadding(60, 0, 0, 0);
            } else {
                mainLayout.setPadding(0, 0, 0, 0);
            }

            // download the image user if exist
            if (!comment.getUserCreateComment().getPhotoProfile().isEmpty()) {
                Glide.with(context).load(comment.getUserCreateComment().getPhotoProfile()).circleCrop().placeholder(R.drawable.wait_download).into(userPhoto);
            } else {
                Glide.with(context).load(R.drawable.user_cicrle_duotone).circleCrop().placeholder(R.drawable.wait_download).into(userPhoto);
            }
            // download the image comment if exist
            if (comment.getLink() != null && !comment.getLink().isEmpty()) {
                imageComment.setVisibility(View.VISIBLE);
                Glide.with(context).load(comment.getLink()).placeholder(R.drawable.edittext_background).into(imageComment);
            } else {
                imageComment.setVisibility(View.GONE);
            }

            // set text comment , name user and date publish
            nameUser.setText(comment.getUserCreateComment().getName());
            date.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(comment.getDate()));
            textComment.setText(comment.getText());

            // show button "show reply" if the comment has reply
            if (comment.getNmbReply() != null && comment.getNmbReply() > 0) {
                showReply.setVisibility(View.VISIBLE);
                showReply.setText("show reply (" + comment.getNmbReply() + ")");
            } else {
                showReply.setVisibility(View.GONE);
            }

            // when click "show reply"
            showReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showReply.setClickable(false);//can't click the button again
                    CommentManager commentManager = new CommentManager();
                    //get all reply of comment
                    commentManager.getCommentPost(post, comment.getIdParent() + "/" + comment.getIdComment() + "reply", new CommentRepository.GetCommentPostCallback() {
                        @Override
                        public void getCommentPostSuccess(List<Comment> commentLi) {
                            commentList.addAll(position + 1, commentLi);//add comment reply to list

                            //for hide button "show reply"
                            comment.setNmbReply(new Long(0));
                            showReply.setVisibility(View.GONE);

                            notifyItemRangeInserted(position + 3, commentLi.size());//update recycler view
                        }

                        @Override
                        public void getCommentPostFailure(Exception e) {
                        }
                    });
                }
            });

            // when click "like"
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    like.setClickable(false);//can't click the button "like" again
                    CommentManager commentManager = new CommentManager();
                    commentManager.addLikeInComment(context, post, comment, new CommentRepository.AddLikeInCommentCallback() {
                        @Override
                        public void addLikeInCommentSuccess(int nmbLike) {
                            comment.setNmbLike(nmbLike);//set number like in comment
                            TypedValue typedValue = new TypedValue();
                            context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);//get color primary from Theme
                            like.setTextColor(typedValue.data);//set color text view
                            like.setText("like " + nmbLike);//set number like view
                        }

                        @Override
                        public void addLikeInCommentFailure(Exception e) {
                            like.setClickable(true);//can click the button "like" again if add like is failed
                        }
                    });
                }
            });

            // when click "reply" button
            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentFragment.CommentHelper.commentParent = comment;//set comment parent in class CommentHelper
                    EditText textComment = fragmentView.findViewById(R.id.addTextComment);//initialize text view
                    textComment.setText(comment.getUserCreateComment().getName() + " ");//send username that was reply to

                    //show keyboard
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(textComment, InputMethodManager.SHOW_IMPLICIT);
                }
            });
        }
    }
}