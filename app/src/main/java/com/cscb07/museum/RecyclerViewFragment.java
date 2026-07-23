/*
 * RecyclerViewFragment
 * Version 1.0
 * July 23, 2026
 */

package com.cscb07.museum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArtifactAdapter artifactAdapter;
    private List<Artifact> artifactList;
    private Spinner spinnerCategory;

    private FirebaseDatabase db;
    private DatabaseReference artifactsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        artifactList = new ArrayList<>();
        artifactAdapter = new ArtifactAdapter(artifactList);
        recyclerView.setAdapter(artifactAdapter);

        db = FirebaseDatabase.getInstance("https://b07-project-66023-default-rtdb.firebaseio.com/");
        artifactsRef = db.getReference("artifacts");

        //Load in all artifacts from Database
        fetchArtifactsFromDatabase();

        return view;
    }

    private void fetchArtifactsFromDatabase() {
        artifactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                artifactList.clear();

                //Iterate through children of the artifacts folder
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Artifact artifact = snapshot.getValue(Artifact.class);
                    artifactList.add(artifact);
                }
                artifactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
