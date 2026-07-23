package com.cscb07.museum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedArtifactsFragment extends Fragment {
    private RecyclerView recView;
    private ArrayList<Artifact> savedArtifacts;
    private ArtifactAdapter adapter;
    private FirebaseAuth auth;
    private DatabaseReference data;

    public SavedArtifactsFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                            @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_saved_artifacts, container, false);
            recView = view.findViewById(R.id.recyclerSavedArtifacts);
            recView.setLayoutManager(new LinearLayoutManager(getContext())
            );
            
            savedArtifacts = new ArrayList<>();
            adapter = new ArtifactAdapter(savedArtifacts);
            recView.setAdapter(adapter);

            auth = FirebaseAuth.getInstance();
            data = FirebaseDatabase.getInstance().getReference();

            loadSavedArtifacts();

            return view;
    }


    public void loadSavedArtifacts() {
        FirebaseUser user = auth.getCurrentUser();
        
        if (user == null) {
            return;
        }

        String userID = user.getUid();

        data.child("users")
                .child(userID)
                .child("savedArtifacts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        savedArtifacts.clear();
                        ArrayList<String> artifactIDs = (ArrayList<String>) snapshot.getValue();

                        if (artifactIDs == null) {
                            return;
                        }

                        for (int i = 0; i < artifactIDs.size(); i++) {
                            String lotNum = artifactIDs.get(i);
                            getArtifact(lotNum);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    private void getArtifact(String lotNum) {
        data.child("artifacts")
                .orderByChild("lotNum")
                .equalTo(lotNum)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Artifact artifact = null;

                        if (snapshot.exists()) {
                            DataSnapshot artifactSnapshot = snapshot.getChildren().iterator().next();
                            artifact = artifactSnapshot.getValue(Artifact.class);
                        }
                        else {
                            return;
                        }
                        if (artifact != null) {
                            savedArtifacts.add(artifact);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}