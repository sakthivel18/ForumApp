package com.example.group09_hw05;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleForumFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private Forum mForum;

    public SingleForumFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SingleForumFragment newInstance(Forum forum) {
        SingleForumFragment fragment = new SingleForumFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, forum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mForum = (Forum) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_forum, container, false);
        getActivity().setTitle("Forum");
        return view;
    }
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    TextView textViewSingleForumTitle;
    TextView textViewForumCreatedBy;
    TextView textViewSingleForumDesc;
    TextView textViewComments;
    EditText editTextWriteComment;
    Button buttonPostComment;

    ArrayList<Comment> comments = new ArrayList<>();
    RecyclerView recyclerViewComments;
    CommentsAdapter adapter;
    LinearLayoutManager layoutManager;

    Context context;
    SharedPreferences sharedPref;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        context = getActivity().getApplicationContext();
        sharedPref = context.getSharedPreferences("userInformation", Context.MODE_PRIVATE);

        String uid = mAuth.getCurrentUser().getEmail();
        String name = sharedPref.getString("name", uid);


        recyclerViewComments = view.findViewById(R.id.recyclerViewComments);
        textViewSingleForumTitle = view.findViewById(R.id.textViewSingleForumTitle);
        textViewForumCreatedBy = view.findViewById(R.id.textViewForumCreatedBy);
        textViewSingleForumDesc = view.findViewById(R.id.textViewSingleForumDesc);
        textViewComments = view.findViewById(R.id.textViewComments);
        editTextWriteComment = view.findViewById(R.id.editTextWriteComment);
        buttonPostComment = view.findViewById(R.id.buttonPostComment);

        adapter = new CommentsAdapter(comments, uid);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerViewComments.setAdapter(adapter);
        recyclerViewComments.setLayoutManager(layoutManager);

        db.collection("comments")
                .whereEqualTo("forumId", mForum.getForumId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        comments.clear();
                        for(QueryDocumentSnapshot document : value) {
                            Map<String, Object> data = document.getData();
                            Log.d("demo", "onComplete: "+ data);

                            String forumId = (String) document.get("forumId");
                            String commentId = (String) document.getId();
                            String commentCreator = (String) document.get("commentCreator");
                            String commentText = (String) document.get("commentText");
                            String commentCreatorUid = (String) document.get("commentCreatorUid");
                            Timestamp commentCreatedAt = (Timestamp) document.get("commentCreatedAt");

                            comments.add(
                              new Comment(forumId, commentId, commentCreator,commentText, commentCreatorUid, commentCreatedAt)
                            );

                        }
                        textViewComments.setText(comments.size() + " Comments");
                        adapter.notifyDataSetChanged();
                    }
                });




        textViewSingleForumTitle.setText(mForum.getTitle());
        textViewForumCreatedBy.setText(mForum.getCreatedByName());
        textViewSingleForumDesc.setText(mForum.getDesc());


        buttonPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newCommentText = editTextWriteComment.getText().toString();
                if(!newCommentText.isEmpty()) {
                    Map<String,Object> data = new HashMap<>();
                    data.put("forumId", mForum.getForumId());
                    data.put("commentCreatedAt", FieldValue.serverTimestamp());
                    data.put("commentCreator", name);
                    data.put("commentCreatorUid", uid);
                    data.put("commentText", newCommentText);

                    db.collection("comments")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    editTextWriteComment.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            }
        });

    }

    static class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {
        ArrayList<Comment> comments;
        String uid;

        public CommentsAdapter(ArrayList<Comment> comments, String uid) {
            this.comments = comments;
            this.uid = uid;
        }

        @NonNull
        @Override
        public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
            return new CommentsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
            Comment comment = comments.get(position);
            holder.textViewCommentCreator.setText(comment.getCommentCreator());
            holder.textViewCommentText.setText(comment.getCommentText());

            if(comment.getCommentCreatedAt() != null) {
                Date commentCreatedDate = comment.getCommentCreatedAt().toDate();
                String commentCreatedAt = new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(commentCreatedDate);
                holder.textViewCommentCreatedAt.setText(commentCreatedAt);
            }


            if(comment.getCommentCreatorUid()!= null && uid != null && comment.getCommentCreatorUid().equals(uid)) {
                holder.imageViewDeleteComment.setVisibility(View.VISIBLE);
            } else {
                holder.imageViewDeleteComment.setVisibility(View.INVISIBLE);
            }

            holder.imageViewDeleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseFirestore.getInstance().collection("comments").document(comment.getCommentId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("demo", "onSuccess: "+" comment deletion ");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("demo", "onFailure: "+" comment deletion ");
                                }
                            });

                }
            });
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        static class CommentsViewHolder extends RecyclerView.ViewHolder {
            TextView textViewCommentCreator;
            TextView textViewCommentText;
            ImageView imageViewDeleteComment;
            TextView textViewCommentCreatedAt;

            public CommentsViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewCommentCreator = itemView.findViewById(R.id.textViewCommentCreator);
                textViewCommentText = itemView.findViewById(R.id.textViewCommentText);
                textViewCommentCreatedAt = itemView.findViewById(R.id.textViewCommentCreatedAt);
                imageViewDeleteComment = itemView.findViewById(R.id.imageViewDeleteComment);

            }
        }
    }
}