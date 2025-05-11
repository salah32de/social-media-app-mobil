package com.example.socialmedia;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.socialmedia.Database.RemoteDatabase.Entity.User;
import com.google.gson.Gson;

public class SharedPreferencesHelper {
    private static final String TAG = "TAG: SharedPreferencesHelper";

    public static final String USER_KEY = "user";    //the key of shared preferences
    public static final String DARK_MODE = "darkMode"; //the of the type mode in shared preference
    public static final String EMAIL = "email";
    public static final String ID = "id";
    public static final String LOG_IN = "logIn";

    public SharedPreferencesHelper(Context context, String key) {

    }

    //save user in shared preferences by gson
    public static void saveUser(User user, Context context) {
        SharedPreferences sp;
        SharedPreferences.Editor editor;
        Gson gson;
        sp = context.getSharedPreferences(USER_KEY, Context.MODE_PRIVATE);
        editor = sp.edit();
        gson = new Gson();
        String json = gson.toJson(user);        // Convert the User object to a JSON string
        editor.putString(USER_KEY, json);
        editor.apply();
    }

    //get user from shared preferences by gson

    public static User getUser(Context context) {
        SharedPreferences sp;
        Gson gson;
        sp = context.getSharedPreferences(USER_KEY, Context.MODE_PRIVATE);
        gson = new Gson();
        String jsonUser = sp.getString(USER_KEY, "");
        if (jsonUser.isEmpty()) return null;//shared preferences is empty
        User user = gson.fromJson(jsonUser, User.class);//convert string json to object User
        return user;
    }

    public static boolean getTypeMode(Context context) {
        SharedPreferences sp;
        sp = context.getSharedPreferences(DARK_MODE, Context.MODE_PRIVATE);

        return sp.getBoolean(DARK_MODE, false);

    }

    public static void setTypeMode(Context context, boolean typeMode) {
        SharedPreferences sp;
        SharedPreferences.Editor editor;
        sp = context.getSharedPreferences(DARK_MODE, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putBoolean(DARK_MODE, typeMode);
        editor.apply();
    }

    public static void LogOut(Context context) {
        SharedPreferences sp;

        sp = context.getSharedPreferences(USER_KEY, Context.MODE_PRIVATE);
        sp.edit().clear().apply(); // 🔹 تأكيد الحذف

        sp = context.getSharedPreferences(LOG_IN, Context.MODE_PRIVATE);
        sp.edit().clear().apply(); // 🔹 تأكيد الحذف
    }


    public static void LogIn(Context context, String id, String email, User user) {
        SharedPreferences sp = context.getSharedPreferences(LOG_IN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ID, id);
        editor.putString(EMAIL, email);
        editor.apply(); // 🔹 تأكيد حفظ البيانات في SharedPreferences

        saveUser(user, context); // 🔹 حفظ بيانات المستخدم
    }

    public static boolean isLogIn(Context context){
        SharedPreferences sp = context.getSharedPreferences(LOG_IN, Context.MODE_PRIVATE);
        String email = sp.getString(EMAIL, "");
        String id = sp.getString(ID, "");
        Log.d(TAG, "isLogIn: "+(!email.isEmpty() && !id.isEmpty()));
        return !email.isEmpty() && !id.isEmpty(); // 🔹 التأكد من أن كلاهما موجود
    }

}
