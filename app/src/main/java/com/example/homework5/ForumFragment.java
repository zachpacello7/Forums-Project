package com.example.homework5;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ForumFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Forums forum;
    private String name;
    RecyclerView recyclerViewComments;
    RecyclerViewComments adapter1;
    RecyclerView recyclerViewPost;
    public ForumFragment() {
        // Required empty public constructor
    }
    public static ForumFragment newInstance(Forums forum, String userName) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, forum);
        args.putString(ARG_PARAM2, userName);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            forum = (Forums)getArguments().getSerializable(ARG_PARAM1);
            name = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        getActivity().setTitle("Forum");
        recyclerViewPost = view.findViewById(R.id.recyclerViewComments);

        recyclerViewPost.setHasFixedSize(true);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        recyclerViewPost.setLayoutManager(layoutManager1);
        recyclerViewPost.setAdapter(adapter1);
        recyclerViewComments = view.findViewById(R.id.recyclerViewPost);
        recyclerViewComments.setHasFixedSize(true);
        GetComments();
        return view;
    }
    String commentUserName, commentDescription, commentUserId, documentId;
    Timestamp timestamp;
    Date date;
    ArrayList<String> userDocumentId = new ArrayList<>();
    ArrayList<Comments>userComments = new ArrayList<>();
    int numberOfComments;
    public void GetComments(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Forums")
                .document(forum.documentId)
                .collection("Comments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        userComments.clear();
                        if(value!= null){
                            for (QueryDocumentSnapshot document: value) {
                                commentDescription = (String) document.get("commentDescription");
                                commentUserId = (String) document.get("commentUserId");
                                Log.d("TAG", "onComplete: "+commentDescription);
                                commentUserName = (String) document.get("commentUserName");
                                documentId = (String) document.get("documentId");
                                timestamp= document.getTimestamp("commentTime");
                                date = timestamp.toDate();
                                userDocumentId.add(document.getId());
                                Comments comments = new Comments(commentUserName,commentDescription,commentUserId,documentId,date);
                                userComments.add(comments);
                            }
                            numberOfComments = userComments.size();
                            adapter1 = new RecyclerViewComments(forum, name, numberOfComments);
                            recyclerViewPost.setAdapter(adapter1);
                            adapter1.notifyDataSetChanged();

                            RecyclerViewPost adapter = new RecyclerViewPost(forum, userComments, userDocumentId);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            recyclerViewComments.setLayoutManager(layoutManager);
                            recyclerViewComments.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            Log.d("TAG", "onEvent: "+error.getMessage());
                        }
                    }
                });
    }

}
    class RecyclerViewPost extends RecyclerView.Adapter<RecyclerViewPost.ViewHolder>{
        Forums forums;
        ArrayList<Comments>userComments = new ArrayList<>();
        ArrayList<String>userId = new ArrayList<>();
        public RecyclerViewPost(Forums forum, ArrayList<Comments> comments, ArrayList<String> id) {
            forums = forum;
            userComments.addAll(comments);
            userId.addAll(id);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Comments comment = userComments.get(position);
            Log.d("TAG", "onBindViewHolder: "+comment.commentDescription);
            holder.textViewDate.setText(comment.commentTime+"");
            holder.textViewName.setText(comment.commentUserName+"");
            holder.textViewDescription.setText(comment.commentDescription+"");
            if(comment.commentUserId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                holder.imageView.setVisibility(View.VISIBLE);
            }
            else{
                holder.imageView.setVisibility(View.GONE);
            }
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TAG", "onClick: "+comment.documentId);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Forums").document(forums.documentId).collection("Comments").document(comment.documentId).delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("TAG", "onComplete: success");
                                    }
                                    else{
                                        Log.d("TAG", "onComplete: "+task.getException().getMessage());
                                    }
                                }
                            });
                }
            });
        }

        @Override
        public int getItemCount() {
            return userComments.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView textViewName;
            TextView textViewDescription;
            TextView textViewDate;
            ImageView imageView;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textViewUserName);
                textViewDescription = itemView.findViewById(R.id.textViewComment);
                textViewDate = itemView.findViewById(R.id.textViewDate);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }

    class RecyclerViewComments extends RecyclerView.Adapter<RecyclerViewComments.ViewHolder>{
     Forums forums;
     String userName;
     int numberOfComments;
     public RecyclerViewComments(Forums forum, String name, int comments){
         forums = forum;
         userName = name;
         numberOfComments = comments;
     }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.title.setText(forums.forumTitle);
            holder.comments.setText(numberOfComments+"");
            holder.description.setText(forums.forumDescription);
            holder.name.setText(forums.forumName);
            holder.postComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userComment = holder.writeComment.getText().toString();
                    if(userComment.matches("")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Error")
                                .setMessage("Enter comment")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        builder.create().show();
                    }
                    else{
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("commentDescription", userComment);
                        hashMap.put("commentUserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("commentUserName", userName);
                        Date date = new Date();
                        Timestamp timestamp = new Timestamp(date);
                        hashMap.put("commentTime", timestamp);
                        String id = db.collection("Forums").document().getId();
                        hashMap.put("documentId", id);
                        db.collection("Forums")
                                .document(forums.documentId)
                                .collection("Comments")
                                .document(id)
                                .set(hashMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                        }
                                        else{
                                            Log.d("TAG", "onComplete: "+task.getException().getMessage());
                                        }
                                    }
                                });
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView description;
        TextView title;
        TextView comments;
        EditText writeComment;
        Button postComment;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.textViewName);
                description = itemView.findViewById(R.id.textViewDescription);
                title = itemView.findViewById(R.id.textViewTitle);
                comments = itemView.findViewById(R.id.textViewComments);
                writeComment = itemView.findViewById(R.id.editTextWriteComment);
                postComment = itemView.findViewById(R.id.buttonPostComment);
            }
        }
    }