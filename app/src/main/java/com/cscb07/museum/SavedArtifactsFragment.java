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

public class SavedArtifactsFragment extends Fragment implements LikeClick{
    private RecyclerView recView;
    private ArrayList<Artifact> savedArtifacts;
    private ArtifactAdapter adapter;
    private FirebaseAuth auth;
    private DatabaseReference data;

    public SavedArtifactsFragment(){
    }

    // Uses Recycler View, initiates the backing list and adapter for saved artifacts
    // Finds the user's saved artifacts and adds them to the list
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
            adapter = new ArtifactAdapter(savedArtifacts, requireContext(), null, this);
            recView.setAdapter(adapter);

            auth = FirebaseAuth.getInstance();
            data = FirebaseDatabase.getInstance().getReference();

            loadSavedArtifacts();

            return view;
    }

    // Reads the list of saved artifact lot numbers if a user is logged in, else nothing to load
    // Finds the full artifact details for each saved lot number
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
                        ArrayList<String> artifactIDs = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String lotNum = child.getValue(String.class);

                            if (lotNum != null) {
                                artifactIDs.add(lotNum);
                            }
                        }

                        if (artifactIDs.isEmpty()) {
                            return;
                        }

                        for (String lotNum : artifactIDs) {
                            getArtifact(lotNum);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    // Looks up artifact by lot number and adds them to the list, then refresh Recycler View
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
                            // Loads the user's like + save status in real time before displaying artifact
                            artifact.loadUserStatus();
                            savedArtifacts.add(artifact);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    // Implements logic to like/unlike artifacts from the saved artifacts page to sync everything
    @Override
    public void onLikeClick(Artifact artifact, int position) {
    }
}