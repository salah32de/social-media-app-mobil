package com.example.socialmedia.Controller;

import android.content.Context;

import com.example.socialmedia.Database.LocalDatabase.AppDatabase.AppDatabase;
import com.example.socialmedia.Database.LocalDatabase.Dao.ChatDao;
import com.example.socialmedia.Database.LocalDatabase.Dao.PostsSavedDao;
import com.example.socialmedia.Database.LocalDatabase.Dao.UserDao;
import com.example.socialmedia.Database.LocalDatabase.Entity.ChatEntity;
import com.example.socialmedia.Database.LocalDatabase.Entity.PostsSavedEntity;
import com.example.socialmedia.Database.LocalDatabase.Entity.UserEntity;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Chat;
import com.example.socialmedia.Database.RemoteDatabase.Entity.Post;
import com.example.socialmedia.SharedPreferencesHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RoomDatabaseManager {
    private final AppDatabase appDatabase;
    private final UserDao userDao;
    private final ChatDao chatDao;
    private final PostsSavedDao savesPostsDao;
    private Context c;

    public RoomDatabaseManager(Context context) {
        appDatabase = AppDatabase.getDatabase(context);
        userDao = appDatabase.userDao();
        chatDao = appDatabase.chatDao();
        savesPostsDao = appDatabase.savesPostsDao();
        this.c=context;
    }

    public void insertUser(UserEntity userEntity) {
        userDao.insert(userEntity);
    }

    public UserEntity getUser(String id) {
        return userDao.getUserById(id);
    }

    public void deleteUser(String id) {
        userDao.deleteUserById(id);
    }

    public void insertChat(ChatEntity chatEntity) {
        chatDao.insert(chatEntity);
    }

    public ChatEntity getChat(String id) {
        return chatDao.getChatById(id);
    }

    public List<ChatEntity> getAllChatsByIdUser(String userId) {
        return chatDao.getAllChatsByIdUser(userId);
    }

    public void deleteChat(String id) {
        chatDao.deleteChatById(id);
    }

    public void deleteAllChat(String idUser) {
        chatDao.deleteAllChatByIdUser(idUser);
    }

    public void insertPost(PostsSavedEntity savesPostsEntity) {
        savesPostsDao.insert(savesPostsEntity);
    }

    public List<PostsSavedEntity> getPostsSavesUser(String idUserSavedPost) {
        return savesPostsDao.getPost(idUserSavedPost);
    }

    public void deletePost(String postId) {
        savesPostsDao.delete(postId);
    }

    public void SavePostInLocalDB(Post post) {
        new Thread(() -> {
            String path = "";
            if (!post.getLink().isEmpty()) {
                try {
                    URL url = new URL(post.getLink());

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    String fileName;
                    if(post.getLink().contains("jpg")){
                          fileName = "media_" + System.currentTimeMillis() + ".jpg";  // when the link is image
                    }else{
                          fileName = "media_" + System.currentTimeMillis() + ".mp4";  // when the link is video

                    }




                    File file = new File(c.getFilesDir(), fileName);


                    InputStream input = connection.getInputStream();
                    FileOutputStream output = new FileOutputStream(file);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }

                    output.close();
                    input.close();

                    path = file.getAbsolutePath(); //link the file to the path

                } catch (Exception e) {
                    e.printStackTrace();
                }
                insertPost(new PostsSavedEntity(post.getIdPost(), post.getDate(), post.getIdUser(), SharedPreferencesHelper.getUser(c).getId(), path, post.getText(),post.getLikes(),post.getNumComments()));

            }else{

                insertPost(new PostsSavedEntity(post.getIdPost(), post.getDate(), post.getIdUser(), SharedPreferencesHelper.getUser(c).getId(),"", post.getText(),post.getLikes(),post.getNumComments()));
            }

        }).start();

    }

    public void UpdateChatInRoomDB(List<Chat> chats){
        List<ChatEntity> chatEntities=new ArrayList<>();
        List<UserEntity> userEntities=new ArrayList<>();

        for(Chat chat:chats){
            ChatEntity chatEntity=new ChatEntity(chat.getIdChat(),chat.getIdUser1(),chat.getIdUser2(),chat.getLastMessage());
            chatEntities.add(chatEntity);
            userEntities.add(new UserEntity(chat.getUserReceiver().getId(),chat.getUserReceiver().getName()));
        }
        new Thread(()->{
            for(UserEntity userEntity:userEntities){
                userDao.insert(userEntity);
            }
            for(ChatEntity chatEntity:chatEntities){
                chatDao.insert(chatEntity);
            }

        }).start();
    }

}
