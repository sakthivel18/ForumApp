package com.example.group09_hw05;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
/*
*   Assignment #: HW05
*   File name: MainActivity.java
*   Full names: 1.) Sakthivel Ravichandran
*               2.) Rudhra Moorthy Baskar
* */
public class MainActivity extends AppCompatActivity implements ForumsFragment.IForumsFragment {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String TAG = "demo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if(currentUser == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerView, new LoginFragment())
                    .commit();
        } else {
            String email = currentUser.getEmail();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerView, new ForumsFragment())
                    .commit();
        }

    }

    void addUserInfoToSharedPref(String uid) {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences("userInformation", Context.MODE_PRIVATE);
        db.collection("users")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            String name = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                name = (String) document.getData().get("name");
                            }
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("uid",uid);
                            editor.putString("name", name);
                            editor.apply();
                        }
                    }
                });
    }

    @Override
    public void gotoSingleFragment(Forum forum) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerView, SingleForumFragment.newInstance(forum))
                .addToBackStack(null)
                .commit();
    }
}