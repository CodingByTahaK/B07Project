/*
 * RecyclerViewFragment
 * Version 1.0
 * July 23, 2026
 */

package com.cscb07.museum;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

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
import java.util.Locale;

public class RecyclerViewFragment extends Fragment implements RecyclerExpandedViewInterface{


    private RecyclerView recyclerView;
    private ArtifactAdapter artifactAdapter;
    private List<Artifact> artifactList;
    private List<Artifact> allArtifacts;
    private EditText searchEditText;
    private FirebaseDatabase db;
    private DatabaseReference artifactsRef;


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_recycler_view,
                container,
                false
        );

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        getContext(),
                        R.array.categories_array,
                        android.R.layout.simple_spinner_item
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        searchEditText = view.findViewById(R.id.searchEditText);

        artifactList = new ArrayList<>();
        allArtifacts = new ArrayList<>();

        artifactAdapter = new ArtifactAdapter(artifactList, getContext());
        recyclerView.setAdapter(artifactAdapter);

        db = FirebaseDatabase.getInstance(
                "https://b07-project-66023-default-rtdb.firebaseio.com/"
        );

        artifactsRef = db.getReference("artifacts");
        fetchArtifactsFromDatabase();
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence text,
                    int start,
                    int count,
                    int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(
                    CharSequence text,
                    int start,
                    int before,
                    int count) {

                filterArtifacts(text.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }
        });

        return view;
    }

    private void fetchArtifactsFromDatabase() {

        artifactsRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(
                    @NonNull DataSnapshot dataSnapshot) {

                allArtifacts.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Artifact artifact =
                            snapshot.getValue(Artifact.class);

                    if (artifact != null) {
                        allArtifacts.add(artifact);
                    }
                }

                filterArtifacts(
                        searchEditText.getText().toString()
                );
            }

            @Override
            public void onCancelled(
                    @NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void filterArtifacts(String searchText) {

        String query =
                searchText.trim().toLowerCase(Locale.ROOT);

        artifactList.clear();

        for (int i = 0; i < allArtifacts.size(); i++) {

            Artifact artifact = allArtifacts.get(i);

            if (query.isEmpty()) {
                artifactList.add(artifact);
            } else if (artifactMatchesSearch(artifact, query)) {
                artifactList.add(artifact);
            }
        }

        artifactAdapter.notifyDataSetChanged();
    }

    private boolean artifactMatchesSearch(
            Artifact artifact,
            String query) {

        String searchableText =
                safe(artifact.getLotNum()) + " " +
                        safe(artifact.getName()) + " " +
                        safe(artifact.getDescription()) + " " +
                        safe(artifact.getCategory()) + " " +
                        safe(artifact.getMaterial()) + " " +
                        safe(artifact.getPeriod()) + " " +
                        safe(artifact.getCulturalOrigin()) + " " +
                        safe(artifact.getDimensions()) + " " +
                        safe(artifact.getConditionReport()) + " " +
                        safe(artifact.getLocation()) + " " +
                        safe(artifact.getAcqMethod()) + " " +
                        safe(artifact.getProvenance()) + " " +
                        safe(artifact.getAccNum()) + " " +
                        safe(artifact.getNotes()) + " " +
                        safe(artifact.getImage());

        searchableText =
                searchableText.toLowerCase(Locale.ROOT);

        if (searchableText.contains(query)) {
            return true;
        } else {
            return false;
        }
    }

    private String safe(String value) {

        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    //on clicking an artifact, a new screen is opened sent via intents, sending artifact's info with bundle
    @Override
    public void onArtifactClick(int position) {
        Intent send = new Intent(getContext(), ExpandedView.class);
        //Probably where the issue originates
        send.putExtra("select_artifact", artifactList.get(position));
        startActivity(send);
    }
}