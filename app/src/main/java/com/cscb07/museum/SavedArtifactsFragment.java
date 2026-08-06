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

/**
 * Fragment displaying the saved artfiacts of the current user
 * Gets saved artifact IDs from the Firebase and loads the
 * artifact information accordingly, displaying them
 * in a RecyclerView format
 */
public class SavedArtifactsFragment extends Fragment implements LikeClick{
    private RecyclerView recView;
    private ArrayList<Artifact> savedArtifacts;
    private ArtifactAdapter adapter;
    private FirebaseAuth auth;
    private DatabaseReference data;

    /**
     * Creates a new SavedArtifactsFragment
     */
    public SavedArtifactsFragment(){
    }

    /**
     * Creates the fragment view and sets up the RecyclerView
     * Then initializes the artifact list and adapter, connecting
     * it to the firebase and loading the saved artifacts
     * @param inflater - LayoutInflater used to inflate the fragment layout
     * @param container - the parent ViewGroup that contains the fragment
     * @param savedInstanceState - the last saved state of the fragment if it exists
     * @return the created fragment view
     */
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

    /**
     * Gets the saved artifact IDs from the Firebase for the current user
     * Loads the artifact's details and adds them to the RecyclerView
     * Nothing is loaded if no valid user is logged in
     */
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
                        
                        adapter.setSavedArtifactIDs(new ArrayList<>(artifactIDs));

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

    /**
     * Gets an artifact from the Firebase based on the lot number
     * Adds this artifact to the list of saved artifacts
     * and then refreshes the RecyclerView
     * @param lotNum - the lot number of the artifact
     */
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


    /**
     * Handles like / unlike actions on artifacts
     * Maintains the synchronization with the interface of LikeClick
     * @param artifact - the artifact that was liked or unliked
     * @param position - the position of the artifact in the RecyclerView
     */
    @Override
    public void onLikeClick(Artifact artifact, int position) {
    }
}