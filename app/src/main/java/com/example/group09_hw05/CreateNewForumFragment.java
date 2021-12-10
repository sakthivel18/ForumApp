package com.example.group09_hw05;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateNewForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateNewForumFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateNewForumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateNewForumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateNewForumFragment newInstance(String param1, String param2) {
        CreateNewForumFragment fragment = new CreateNewForumFragment();
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
    FirebaseFirestore db;

    EditText editTextForumTitle;
    EditText editTextForumDescription;
    Button createForumSubmitButton;
    Button createForumCancelButton;

    Context context;
    SharedPreferences sharedPref;
    String uid = "";
    String name = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_new_forum, container, false);
        getActivity().setTitle("New Forum");
        context = getActivity().getApplicationContext();
        sharedPref = context.getSharedPreferences("userInformation", Context.MODE_PRIVATE);
        uid = sharedPref.getString("uid", null);
        name = sharedPref.getString("name", null);

        db = FirebaseFirestore.getInstance();

        editTextForumTitle = view.findViewById(R.id.editTextForumTitle);
        editTextForumDescription = view.findViewById(R.id.editTextForumDescription);
        createForumSubmitButton = view.findViewById(R.id.createForumSubmitButton);
        createForumCancelButton = view.findViewById(R.id.createForumCancelButton);

        createForumCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity()
                        .getSupportFragmentManager()
                        .popBackStack();
            }
        });

        createForumSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String forumTitle = editTextForumTitle.getText().toString();
                String forumDesc = editTextForumDescription.getText().toString();
                if(forumTitle.length() > 0 && forumDesc.length() > 0) {
                    Map<String,Object> forum = new HashMap<>();

                    forum.put("title", forumTitle);
                    forum.put("description", forumDesc);
                    forum.put("createdByUid", uid);
                    forum.put("createdByName", name);

                    Map<String,Object> likedBy = new HashMap<>();
                    forum.put("likedBy", likedBy);

                    Date date = Calendar.getInstance().getTime();
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    forum.put("createdDate", dateFormat.format(date));

                    addForumToCollection(forum);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Please enter both forum title and description to create a forum")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.create().show();
                }
            }
        });

        return view;
    }

    void addForumToCollection(Map<String,Object> forum) {
        db.collection("forums")
                .add(forum)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.containerView, new ForumsFragment())
                                .commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.containerView, new ForumsFragment())
                                .commit();
                    }
                });
    }
}