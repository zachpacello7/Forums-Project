package com.example.homework5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.MapValue;

import java.util.Date;
import java.util.HashMap;

public class NewForumFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    
    private String userName;

    public NewForumFragment() {

    }
    
    public static NewForumFragment newInstance(String param1) {
        NewForumFragment fragment = new NewForumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_forum, container, false);
        getActivity().setTitle("New Forum");
        EditText title = view.findViewById(R.id.editTextForumTitle);
        EditText description = view.findViewById(R.id.editTextForumDescription);

        view.findViewById(R.id.buttonSubmitForum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String forumTitle = title.getText().toString();
                String forumDescription = description.getText().toString();
                if(forumTitle.matches("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Error")
                            .setMessage("Enter title")
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
                else if(forumDescription.matches("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Error")
                            .setMessage("Enter Description")
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
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("forumDescription", forumDescription);
                    hashMap.put("forumTitle", forumTitle);
                    hashMap.put("forumName", userName);
                    hashMap.put("forumId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Date date = new Date();
                    Timestamp timestamp = new Timestamp(date);
                    hashMap.put("forumTime", timestamp);
                    HashMap<String, Object> userLikes = new HashMap<>();
                    hashMap.put("userLikes", userLikes);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Forums")
                            .add(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()){
                                        getParentFragmentManager().popBackStack();
                                    }
                                    else{
                                        Log.d("TAG", "onComplete: "+task.getException().getMessage());
                                    }
                                }
                            });
                }

            }
        });
        view.findViewById(R.id.buttonCancelForum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        return view;
    }
}