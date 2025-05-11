package com.example.socialmedia.UI.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.socialmedia.Controller.PostManager;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.PostRepository;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;

import android.widget.MediaController;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreatePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePostFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreatePostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreatePostFragment newInstance(String param1, String param2) {
        CreatePostFragment fragment = new CreatePostFragment();
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

    private View view;
    private Button buttonPost;
    private String typePost = "";//image or video and empty is text
    private Uri uriPost;// url of image or video
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_post, container, false);

        //initialize the views
        progressBar = view.findViewById(R.id.progressBar);
        buttonPost = view.findViewById(R.id.buttonPost);
        ImageView addPhoto = view.findViewById(R.id.addPhoto);
        ImageView addVideo = view.findViewById(R.id.addVideo);
        ImageView back = view.findViewById(R.id.back);
        TextView textPost = view.findViewById(R.id.textPost);

        //set the click listener for the views
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.addPhoto) {
                    pickImage();
                } else if (v.getId() == R.id.addVideo) {
                    pickVideo();
                } else if (v.getId() == R.id.buttonPost) {
                    String text = textPost.getText().toString();//get text the post

                    if (text.isEmpty() && typePost.isEmpty()) {
                        Toast.makeText(getContext(), "please enter text", Toast.LENGTH_SHORT).show();//if user not enter text and photo and video show error
                    } else {
                        //show the progress bar and hide the button post
                        progressBar.setVisibility(View.VISIBLE);
                        buttonPost.setVisibility(View.GONE);

                        view.setClickable(false);
                        //get user information from shared preferences
                        User user = SharedPreferencesHelper.getUser(getContext());

                        //create post
                        Post post = new Post(System.currentTimeMillis(), user.getId(), "", text);
                        PostManager postManager = new PostManager();

                        //create post in realtime database and storage
                        postManager.CreatePost(post, typePost, uriPost, new PostRepository.CreatePostCallback() {
                            @Override
                            public void createPostSuccess() {
                                Toast.makeText(getContext(), "post successful", Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().popBackStack();
                            }

                            @Override
                            public void createPostFailure() {
                                Toast.makeText(getContext(), "post successful", Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().popBackStack();

                            }
                        });
                    }
                } else if (v.getId() == R.id.back) {
                    getParentFragmentManager().popBackStack();//back to activity main
                }
            }
        };

        //
        addPhoto.setOnClickListener(clickListener);
        addVideo.setOnClickListener(clickListener);
        buttonPost.setOnClickListener(clickListener);
        back.setOnClickListener(clickListener);

        return view;
    }

    //user choose image from his phone
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //user choose video from his phone
    public void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    //get the result of user choose
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();//get the uri of image or video user choose
            if (requestCode == PICK_IMAGE_REQUEST) {//if user choose image

                ImageView imageView = view.findViewById(R.id.photo);//get the image view from fragment
                Glide.with(getContext())
                        .load(uri) // تحميل الصورة من URI
                        .override(Target.SIZE_ORIGINAL) // الحفاظ على الحجم الأصلي
                        .into(imageView); // show image in ImageView

                imageView.setVisibility(View.VISIBLE);
                typePost = "image";
                uriPost = uri;
            } else if (requestCode == PICK_VIDEO_REQUEST) {//if user choose video
                VideoView videoView = view.findViewById(R.id.video);//get the video view from fragment
                MediaController mediaController = new MediaController(getActivity());//add the setting of video pause play and skip
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(uri);
                videoView.setVisibility(View.VISIBLE);
                typePost = "video";
                uriPost = uri;
            }

            LinearLayout addPhotoLinearLayout = view.findViewById(R.id.addPhotoLinearLayout);
            addPhotoLinearLayout.setVisibility(View.GONE);
            LinearLayout addVideoLinearLayout = view.findViewById(R.id.addVideoLinearLayout);
            addVideoLinearLayout.setVisibility(View.GONE);


        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        ((AppCompatActivity) getContext()).findViewById(R.id.createPostFragment).setVisibility(View.GONE);
        getParentFragmentManager().popBackStack();

    }


}