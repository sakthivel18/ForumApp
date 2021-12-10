package com.example.group09_hw05;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForumsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForumsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForumsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForumsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForumsFragment newInstance(String param1, String param2) {
        ForumsFragment fragment = new ForumsFragment();
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
    Context context;
    SharedPreferences sharedPref;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    Button forumsLogoutButton;
    Button forumsNewForumButton;
    RecyclerView forumListRecyclerView;
    ForumsRecyclerViewAdapter adapter;
    LinearLayoutManager layoutManager;

    ArrayList<Forum> forums = new ArrayList<>();
    IForumsFragment mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forums, container, false);
        getActivity().setTitle("Forums");
        context = getActivity().getApplicationContext();
        sharedPref = context.getSharedPreferences("userInformation", Context.MODE_PRIVATE);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String uid = mAuth.getCurrentUser().getEmail();

        forumsLogoutButton = view.findViewById(R.id.forumsLogoutButton);
        forumsNewForumButton = view.findViewById(R.id.forumsNewForumButton);
        forumListRecyclerView = view.findViewById(R.id.forumListRecyclerView);

        adapter = new ForumsRecyclerViewAdapter(forums, uid, mListener);
        layoutManager = new LinearLayoutManager(getContext());
        forumListRecyclerView.setAdapter(adapter);
        forumListRecyclerView.setLayoutManager(layoutManager);
        getForums();

        forumsLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear().apply();
                mAuth.signOut();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerView, new LoginFragment())
                        .commit();
            }
        });

        forumsNewForumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerView, new CreateNewForumFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    void getForums() {

        db.collection("forums")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        forums.clear();
                        for(QueryDocumentSnapshot document : value) {
                            Map<String, Object> data = document.getData();
                            Log.d("demo", "onComplete: "+ data);

                            String forumId = document.getId();
                            String title = (String) data.get("title");
                            String description = (String) data.get("description");
                            String createdByName = (String) data.get("createdByName");
                            String createdByUid = (String) data.get("createdByUid");
                            String createdByDate = (String) data.get("createdDate");
                            String likes = (String) data.get("likes");
                            Map<String,Object> likedBy = (Map<String, Object>) data.get("likedBy");
                            forums.add(new Forum(forumId,title,description,createdByUid,createdByName,createdByDate,likes,likedBy));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

    }

    static class ForumsRecyclerViewAdapter extends RecyclerView.Adapter<ForumsRecyclerViewAdapter.ViewHolder> {
        ArrayList<Forum> forums;
        String uid;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        IForumsFragment mListener;

        public ForumsRecyclerViewAdapter(ArrayList<Forum> forums, String uid, IForumsFragment mListener) {
            this.forums = forums;
            this.uid = uid;
            this.mListener = mListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Forum forum = forums.get(position);
            holder.textViewForumTitle.setText(forum.getTitle());
            holder.textViewUserName.setText(forum.getCreatedByName());
            holder.textViewForumDesc.setText(forum.getDesc());
            holder.textViewLikes.setText(forum.getLikedBy().keySet().size() +" likes |");
            holder.textViewCreatedDate.setText(forum.getCreatedDate());

            if(forum.getCreatedByUid() != null && uid != null && forum.getCreatedByUid().equals(uid)) {
                holder.imageViewDelete.setVisibility(View.VISIBLE);
            } else {
                holder.imageViewDelete.setVisibility(View.INVISIBLE);
            }

            holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("forums").document(forum.getForumId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("demo", "onSuccess: delete " + forum.getForumId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("demo", "onFailure: delete");
                                }
                            });
                }
            });

            if(forum.getLikedBy().keySet().contains(uid)) {
                holder.imageViewLike.setImageResource(R.drawable.like_favorite);
            } else {
                holder.imageViewLike.setImageResource(R.drawable.like_not_favorite);
            }

            holder.imageViewLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int imgSrc;
                    Map<String,Object> data = new HashMap<>();
                    data.put("createdByName", forum.getCreatedByName());
                    data.put("createdByUid", forum.getCreatedByUid());
                    data.put("createdDate", forum.getCreatedDate());
                    data.put("description", forum.getDesc());
                    data.put("title", forum.getTitle());
                    if(forum.getLikedBy() != null && forum.getLikedBy().keySet().contains(uid)) {
                        // remove like
                        forum.getLikedBy().remove(uid);
                        imgSrc = R.drawable.like_not_favorite;
                     } else {
                        // add like
                        forum.getLikedBy().put(uid,"");
                        imgSrc = R.drawable.like_favorite;
                    }
                    data.put("likedBy", forum.getLikedBy());
                    db.collection("forums").document(forum.getForumId())
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    holder.imageViewLike.setImageResource(imgSrc);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            });

            holder.formCards.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("demo", "onClick: card clicked");
                    mListener.gotoSingleFragment(forum);
                }
            });
        }

        @Override
        public int getItemCount() {
            return forums.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            CardView formCards;
            TextView textViewForumTitle;
            TextView textViewUserName;
            TextView textViewForumDesc;
            TextView textViewLikes;
            TextView textViewCreatedDate;
            ImageView imageViewDelete;
            ImageView imageViewLike;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                formCards = itemView.findViewById(R.id.formCards);
                textViewForumTitle = itemView.findViewById(R.id.textViewForumTitle);
                textViewForumDesc = itemView.findViewById(R.id.textViewForumDesc);
                textViewUserName = itemView.findViewById(R.id.textViewUserName);
                textViewLikes = itemView.findViewById(R.id.textViewLikes);
                textViewCreatedDate = itemView.findViewById(R.id.textViewCreatedDate);
                imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
                imageViewLike = itemView.findViewById(R.id.imageViewLike);

            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof IForumsFragment) {
            mListener = (IForumsFragment) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    interface IForumsFragment {
        void gotoSingleFragment(Forum forum);
    }
}