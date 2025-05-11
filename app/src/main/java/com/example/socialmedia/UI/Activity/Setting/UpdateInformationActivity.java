package com.example.socialmedia.UI.Activity.Setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.socialmedia.SharedPreferencesHelper;
import com.example.socialmedia.Controller.StorageManager;
import com.example.socialmedia.Controller.UserManager;
import com.example.socialmedia.Database.RemoteDatabase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Database.RemoteDatabase.StorageDatabase.StorageFirebase;
import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.example.socialmedia.R;

import java.util.concurrent.atomic.AtomicInteger;

public class UpdateInformationActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImageProfile;
    private AtomicInteger countEditeImageProfile = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_information);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());

        User user = SharedPreferencesHelper.getUser(getBaseContext());

        ImageView imageProfile = findViewById(R.id.imageProfile);
        TextView nameUser = findViewById(R.id.editName);
        TextView textAboutMyself = findViewById(R.id.editBio);

        Button saveButton = findViewById(R.id.saveButton);

        nameUser.setText(user.getName());
        textAboutMyself.setText(user.getBio());
        if(!user.getPhotoProfile().isEmpty()){
            Glide.with(getBaseContext()).load(user.getPhotoProfile()).circleCrop().into(imageProfile);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nameUser.getText().toString().isEmpty()){
                    user.setName(nameUser.getText().toString());
                }
                user.setBio(textAboutMyself.getText().toString());

                if(uriImageProfile != null){
                    countEditeImageProfile.incrementAndGet();
                    StorageManager storageManager=new StorageManager();
                    storageManager.uploadImage(uriImageProfile, new StorageFirebase.UploadCallback() {
                        @Override
                        public void onSuccess(String downloadUrl) {
                            user.setPhotoProfile(downloadUrl);
                            countEditeImageProfile.decrementAndGet();

                            UserManager userManager=new UserManager();
                            userManager.UpdateUser(user, new UserRepository.UserCallBack<Void>() {
                                @Override
                                public void onSuccess(Void value) {

                                    SharedPreferencesHelper.saveUser(user, getBaseContext());
                                    Toast.makeText(UpdateInformationActivity.this, "edit information successful", Toast.LENGTH_SHORT).show();
                                    finish();

                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(UpdateInformationActivity.this, "error in edit information .try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(UpdateInformationActivity.this, "error in edit image profile", Toast.LENGTH_SHORT).show();
                            countEditeImageProfile.decrementAndGet();
                        }
                    });
                }else{

                    UserManager userManager=new UserManager();
                    userManager.UpdateUser(user, new UserRepository.UserCallBack<Void>() {
                        @Override
                        public void onSuccess(Void value) {

                            SharedPreferencesHelper.saveUser(user, getBaseContext());
                            Toast.makeText(UpdateInformationActivity.this, "edit information successful", Toast.LENGTH_SHORT).show();
                            finish();

                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(UpdateInformationActivity.this, "error in edit information .try again", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });



        ImageView editProfileImage=findViewById(R.id.editProfileImage);
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });


    }
    //user choose image from his phone
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //get the result of user choose
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();//get the uri of image or video user choose
            if (requestCode == PICK_IMAGE_REQUEST) {//if user choose image

                ImageView imageView = findViewById(R.id.imageProfile);//get the image view from fragment
                Glide.with(getBaseContext())
                        .load(uri) // تحميل الصورة من URI
                        .override(Target.SIZE_ORIGINAL) // الحفاظ على الحجم الأصلي
                        .circleCrop()
                        .into(imageView); // show image in ImageView

                imageView.setVisibility(View.VISIBLE);
                uriImageProfile = uri;
            }
        }

    }
}