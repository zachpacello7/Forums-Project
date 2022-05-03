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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
public class CreateAccountFragment extends Fragment {
    private FirebaseAuth mAuth;
    private String TAG = "Demo";

    public CreateAccountFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_create_account, container, false);
        getActivity().setTitle("Create Account");
        EditText email = view.findViewById(R.id.editTextUserEmail);
        EditText name = view.findViewById(R.id.editTextName);
        EditText password = view.findViewById(R.id.editTextUserPassword);
        view.findViewById(R.id.buttonSubmitAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();
                String userName = name.getText().toString();
                if(userEmail.matches("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Error")
                            .setMessage("Enter Email")
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
                else if(userPassword.matches("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Error")
                            .setMessage("Enter Password")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.create().show();
                }
                else if(userName.matches("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Error")
                            .setMessage("Enter Name")
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
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(userEmail,userPassword)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Log.d(TAG, "onComplete: complete");
                                        Log.d(TAG, "onComplete: "+task.getResult().getUser().getUid());
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("name", userName);
                                        hashMap.put("email", userEmail);
                                        String documentId = task.getResult().getUser().getUid();
                                        db.collection("Users")
                                                .document(documentId)
                                                .set(hashMap)
                                                .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d(TAG, "onSuccess: ");
                                                        getParentFragmentManager().beginTransaction()
                                                                .replace(R.id.mainLayout, new ForumsFragment())
                                                                .commit();
                                                    }
                                                })
                                                .addOnFailureListener(getActivity(), new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "onFailure: ");
                                                    }
                                                });
                                    }
                                    else{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle("Error")
                                                .setMessage(task.getException().getMessage())
                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                })
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                });
                                        builder.create().show();
                                        Log.d(TAG, "onComplete: "+task.getException().getMessage());
                                    }
                                }
                            });
                }
            }
        });
        view.findViewById(R.id.buttonCancelAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.mainLayout, new LoginFragment())
                        .commit();
            }
        });
        return view;
    }
}