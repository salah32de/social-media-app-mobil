package com.example.socialmedia.UI.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.socialmedia.Controller.CommentManager;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.CommentRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Comment;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.RecyclerView.CommentAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int PICK_IMAGE_REQUEST = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Post post;

    public CommentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommentFragment newInstance(String param1, String param2) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private static Context c;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        c = getContext();
    }

    View view;
    private List<Comment> list = new ArrayList<>();
    private User user;
    private static CommentAdapter commentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        user = SharedPreferencesHelper.getUser(getContext());
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_comment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.commentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // إضافة هذه السطر

        recyclerView.setVisibility(View.VISIBLE);
        if (getArguments() != null) {
            post = (Post) getArguments().getSerializable("post");
            CommentManager commentManager = new CommentManager();
            commentManager.getCommentPost(post, "", new CommentRepository.GetCommentPostCallback() {
                @Override
                public void getCommentPostSuccess(List<Comment> commentList) {
                    list.clear();
                    list.addAll(commentList);
                    commentAdapter = new CommentAdapter(getContext(), post, list, view);
                    recyclerView.setAdapter(commentAdapter);
                }

                @Override
                public void getCommentPostFailure(Exception e) {

                }
            });
        }


        ImageView image, addPhoto, send;
        EditText addTextComment;
        image = view.findViewById(R.id.imageAddComment);
        addPhoto = view.findViewById(R.id.addPhoto);
        addTextComment = view.findViewById(R.id.addTextComment);
        send = view.findViewById(R.id.send);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send.setClickable(false);
                String text = addTextComment.getText().toString();
                CommentHelper.addComment(getContext(), user, post, text, image, list, addTextComment);

            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();
            }
        });


        return view;
    }

    //user choose photo from his phone
    public void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //get the result of user choose
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();//get the uri of image user choose
            if (requestCode == PICK_IMAGE_REQUEST) {//if user choose image

                ImageView imageView = view.findViewById(R.id.imageAddComment);//get the image view from fragment
                Glide.with(getContext())
                        .load(uri) // تحميل الصورة من URI
                        .override(Target.SIZE_ORIGINAL) // الحفاظ على الحجم الأصلي
                        .into(imageView); // show image in ImageView

                imageView.setVisibility(View.VISIBLE);
            }
            ImageView imageView = view.findViewById(R.id.imageAddComment);
            Glide.with(getContext()).load(uri).into(imageView);
            imageView.setTag(uri);

        }

    }

    public static class CommentHelper {
        public static Comment commentParent = null;

        public static void addComment(Context context, User user, Post post, String text, ImageView image, List<Comment> list, EditText addTextComment) {
            if (!text.isEmpty() || image.getVisibility() != View.GONE) {
                Uri uri = null;
                if (image.getVisibility() != View.GONE) {
                    uri = (Uri) image.getTag();
                }
                String idParent = "";
                if (commentParent != null && text.startsWith(commentParent.getUserCreateComment().getName())) {
                    idParent = commentParent.getIdParent() + "/" + commentParent.getIdComment() + "reply";
                }
                Comment comment = new Comment(user.getId(), post.getIdPost(), System.currentTimeMillis(), text, idParent);
                CommentManager commentManager = new CommentManager();
                commentManager.AddComment(post, context, comment, uri, new CommentRepository.AddCommentCallback() {
                    @Override
                    public void addCommentSuccess() {
                        Toast.makeText(context, "Send comment successful", Toast.LENGTH_SHORT).show();
                        comment.setUserCreateComment(SharedPreferencesHelper.getUser(context));

                        if (commentParent == null) {
                            list.add(0, comment);
                            commentAdapter.notifyItemRangeInserted(2, 1);
                        } else {
                            commentParent.setNmbReply(commentParent.getNmbReply() + 1);
                            commentAdapter.notifyItemChanged(list.indexOf(commentParent) + 2);

                        }
                        image.setVisibility(View.GONE);
                        addTextComment.setText("");
                        addTextComment.clearFocus();


                        post.setNumComments(post.getNumComments() + 1);
                        commentAdapter.notifyItemChanged(1);

                        commentParent = null;
                        ((AppCompatActivity) context).findViewById(R.id.send).setClickable(true);

                    }

                    @Override
                    public void addCommentFailure(Exception e) {
                        commentParent = null;
                        Toast.makeText(context, "Send comment failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            ((AppCompatActivity) getContext()).findViewById(R.id.CommentFragment).setVisibility(View.GONE);
        } catch (Exception e) {

        }
        try {
            View fragment = ((AppCompatActivity) getContext()).findViewById(R.id.showPostNotificationActivity);
            fragment.setVisibility(View.GONE);
        } catch (Exception e) {

        }
        getParentFragmentManager().popBackStack();

    }
}