package com.example.homework5;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ForumsFragment extends Fragment {
    private final String TAG = "demo";
    RecyclerView recyclerView;
    Forums forums;
    RecyclerViewAdapter adapter;
    ArrayList<Forums> usersForums = new ArrayList<>();
    public ForumsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_forums, container, false);
        getActivity().setTitle("Forums");
        recyclerView = view.findViewById(R.id.recyclerViewPost);

        view.findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Log.d(TAG, "onClick: "+FirebaseAuth.getInstance().getCurrentUser());
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.mainLayout, new LoginFragment())
                        .commit();
            }
        });
        view.findViewById(R.id.buttonNewForum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            String userName = (String) task.getResult().get("name");
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.mainLayout, NewForumFragment.newInstance(userName))
                                    .addToBackStack(null)
                                    .commit();
                        }
                        else{
                            Log.d(TAG, "onComplete: error getting user");
                        }
                    }
                });

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
    }
    public void getData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Forums").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                usersForums.clear();
                for (QueryDocumentSnapshot document: value) {
                    String documentId = document.getId();
                    String forumDescription = (String) document.get("forumDescription");
                    String forumName = (String) document.get("forumName");
                    HashMap<String, Object> userLikes = new HashMap<>();
                    userLikes = (HashMap)document.get("userLikes");
                    Log.d(TAG, "onEvent: userLikes "+userLikes);
                    String forumId = (String) document.get("forumId");
                    String forumTitle = (String) document.get("forumTitle");
                    Timestamp timestamp = (Timestamp) document.getTimestamp("forumTime");
                    Date date = timestamp.toDate();
                    forums = new Forums(forumDescription,forumName,forumTitle,forumId, documentId, date, userLikes);
                    usersForums.add(forums);
                }
                Context context = getActivity();
                adapter = new RecyclerViewAdapter(usersForums, context);
                adapter.notifyDataSetChanged();
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
                Log.d(TAG, "onEvent: "+usersForums);
            }
        });
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}
class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    ArrayList<Forums> forums = new ArrayList<>();
    int counter = 0;
    Context context;
    public RecyclerViewAdapter(ArrayList<Forums> forum, Context userContext){
        forums = forum;
        context = userContext;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_forums_recyclerview_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Forums forum = forums.get(position);
        Log.d("TAG", "onBindViewHolder: "+forum.userLikes);
        holder.forumDate.setText(forum.forumTime+"");
        holder.forumTitle.setText(forum.forumTitle+"");
        holder.forumDescription.setText(forum.forumDescription+"");
        holder.forumName.setText(forum.forumName+"");
        holder.forumLikes.setText(forum.userLikes.size()+" likes");
        holder.forumDate.setText(forum.forumTime+"");
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals( forum.forumId)){
            holder.imageViewDelete.setImageResource(R.drawable.rubbish_bin);
            holder.imageViewDelete.setVisibility(View.VISIBLE);
        }
        else{
            holder.imageViewDelete.setVisibility(View.GONE);
        }
        if(forum.userLikes.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.imageViewLike.setImageResource(R.drawable.like_favorite);
        }
        else{
            holder.imageViewLike.setImageResource(R.drawable.like_not_favorite);
        }
        holder.itemView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            String userName = (String) task.getResult().get("name");
                            FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
                            manager.beginTransaction().replace(R.id.mainLayout, ForumFragment.newInstance(forum, userName)).addToBackStack(null).commit();
                        }
                        else{
                            Log.d("TAG", "onComplete: error getting user");
                        }
                    }
                });
            }
        });
        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Forums")
                .document(forum.documentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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
        });
        holder.imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!forum.userLikes.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    holder.imageViewLike.setImageResource(R.drawable.like_favorite);
                    HashMap<String, Object> userLikes = new HashMap<>();
                    userLikes.putAll(forum.userLikes);
                    userLikes.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), "id");
                    Log.d("TAG", "onClick: "+forum.userLikes);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Forums")
                            .document(forum.documentId)
                            //.update(hashMap)
                            .update("userLikes", userLikes)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("TAG", "onComplete: ");
                                    }
                                    else{
                                        Log.d("TAG", "onComplete: "+task.getException().getMessage());
                                    }
                                }
                            });
                }
                else{
                    Log.d("TAG", "onClick: "+"else");
                    holder.imageViewLike.setImageResource(R.drawable.like_not_favorite);
                    HashMap<String, Object> userLikes = new HashMap<>();
                    userLikes.putAll(forum.userLikes);
                    userLikes.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Forums")
                            .document(forum.documentId)
                            //.update(hashMap)
                            .update("userLikes", userLikes)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("TAG", "onComplete: ");
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
        return forums.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView forumTitle;
        TextView forumName;
        TextView forumDescription;
        TextView forumLikes;
        TextView forumDate;
        ImageView imageViewDelete;
        ImageView imageViewLike;
        View itemView1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView1 = itemView;
            forumTitle = itemView.findViewById(R.id.forumTitle);
            forumName = itemView.findViewById(R.id.forumName);
            forumDescription = itemView.findViewById(R.id.forumDescription);
            forumLikes = itemView.findViewById(R.id.forumLikes);
            forumDate = itemView.findViewById(R.id.forumDate);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
            imageViewLike = itemView.findViewById(R.id.imageViewLike);
        }
    }

}