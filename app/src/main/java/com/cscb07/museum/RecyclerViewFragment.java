/**
 * File: RecyclerViewFragment.java
 *
 * Version History:
 * v1.0: Initial implementation
 * v1.1: Added search artifacts functionality
 * v1.2: Added expanded view of a single artifact functionality
 *
 * Date: July 23, 2026
 *
 */

package com.cscb07.museum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;

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
import java.util.List;
import java.util.Locale;

/**
 * Description: This is the recycler view that displays all the artifacts in the database
 * also allowing for searching artifacts and viewing their expanded info
 * @version 1.2 03 Aug 2026
 */
public class RecyclerViewFragment extends Fragment implements RecyclerExpandedViewInterface, LikeClick{

    private static final String PREF_NAME = "ArtifactPrefs";
    private static final String KEY_PAGE_SIZE = "page_size";
    private static final int DEFAULT_PAGE_SIZE = 12;

    private Spinner spinnerPagination;
    private TextView tvPageInfo;
    private Button btnPrevPage, btnNextPage;

    private int pageSize = DEFAULT_PAGE_SIZE;
    private int currentPage = 0;
    private int totalPages = 0;
    private SharedPreferences sharedPreferences;
    private String selectedCategory = "All";

    private RecyclerView recyclerView;
    private ArtifactAdapter artifactAdapter;
    private List<Artifact> artifactList;
    private List<Artifact> allArtifacts;
    private List<String> artifactIDs;
    private List<String> allArtifactIDs;
    private List<Artifact> filteredArtifacts;

    private ArrayList<String> savedArtifactIDs;

    private Spinner spinnerCategory;
    private EditText searchEditText;
    private FirebaseDatabase db;
    private FirebaseAuth auth;

    private DatabaseReference artifactsRef;
    private DatabaseReference savedArtifactsRef;





    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return a View
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        //inflate with recycler layout XML
        View view = inflater.inflate(
                R.layout.fragment_recycler_view,
                container,
                false
        );

        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        //will check later if this needs to be deleted
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        searchEditText = view.findViewById(R.id.searchEditText);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        getContext(),
                        R.array.categories_array,
                        android.R.layout.simple_spinner_item
                );

        //as well as this
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        searchEditText = view.findViewById(R.id.searchEditText);
        spinnerCategory.setAdapter(adapter);

        spinnerPagination = view.findViewById(R.id.spinnerPagination);
        tvPageInfo = view.findViewById(R.id.tvPageInfo);
        btnPrevPage = view.findViewById(R.id.btnPrevPage);
        btnNextPage = view.findViewById(R.id.btnNextPage);

        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, 0);

        Button savedArtifactsButton =
                view.findViewById(R.id.savedArtifactsButton);

        savedArtifactsButton.setOnClickListener(clickedView -> {
            MainActivity mainActivity =
                    (MainActivity) requireActivity();

            mainActivity.openSavedArtifacts();
        });

        artifactList = new ArrayList<>();
        allArtifacts = new ArrayList<>();
        artifactIDs = new ArrayList<>();
        allArtifactIDs = new ArrayList<>();
        filteredArtifacts = new ArrayList<>();
        savedArtifactIDs = new ArrayList<>();

        setupPaginationSpinner();

        btnPrevPage.setOnClickListener(v -> prevPage());
        btnNextPage.setOnClickListener(v -> nextPage());

        artifactAdapter = new ArtifactAdapter(artifactList, getContext(), this, this);
        recyclerView.setAdapter(artifactAdapter);

        artifactAdapter.setSaveClickListener(
                new ArtifactAdapter.SaveClick() {

                    @Override
                    public void onSaveClick(Artifact artifact) {
                        toggleSavedArtifact(artifact);
                    }
                }
        );

        db = FirebaseDatabase.getInstance(
                "https://b07-project-66023-default-rtdb.firebaseio.com/"
        );

        auth = FirebaseAuth.getInstance();

        artifactsRef = db.getReference("artifacts");

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            savedArtifactsRef = db
                    .getReference("users")
                    .child(user.getUid())
                    .child("savedArtifacts");

            loadSavedArtifactIDs();
        }

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

    private void loadSavedArtifactIDs() {

        if (savedArtifactsRef == null) {
            return;
        }

        savedArtifactsRef.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        savedArtifactIDs.clear();

                        for (DataSnapshot child : snapshot.getChildren()) {

                            String lotNum =
                                    child.getValue(String.class);

                            if (lotNum != null) {
                                savedArtifactIDs.add(lotNum);
                            }
                        }

                        artifactAdapter.setSavedArtifactIDs(
                                new ArrayList<>(savedArtifactIDs)
                        );
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {
                        // Handle possible errors
                    }
                }
        );
    }

    private void toggleSavedArtifact(Artifact artifact) {

        if (savedArtifactsRef == null) {
            return;
        }

        String lotNum = artifact.getLotNum();

        if (lotNum == null || lotNum.trim().isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "This artifact has no lot number.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (savedArtifactIDs.contains(lotNum)) {
            savedArtifactIDs.remove(lotNum);
        } else {
            savedArtifactIDs.add(lotNum);
        }

        savedArtifactsRef.setValue(savedArtifactIDs);

        artifactAdapter.setSavedArtifactIDs(
                new ArrayList<>(savedArtifactIDs)
        );
    }

    private void fetchArtifactsFromDatabase() {

        artifactsRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(
                    @NonNull DataSnapshot dataSnapshot) {

                allArtifacts.clear();
                allArtifactIDs.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Artifact artifact = snapshot.getValue(Artifact.class);

                    if (artifact != null) {
                        allArtifacts.add(artifact);
                        allArtifactIDs.add(snapshot.getKey());
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
        artifactIDs.clear();
        filteredArtifacts.clear();

        for (int i = 0; i < allArtifacts.size(); i++) {

            Artifact artifact = allArtifacts.get(i);

            boolean categoryMatch;

            if (selectedCategory.equals("All")) {
                categoryMatch = true;
            } else {
                String artifactCategory = artifact.getCategory();

                categoryMatch =
                        artifactCategory != null
                                && artifactCategory.equals(selectedCategory);
            }

            boolean searchMatch;

            if (query.isEmpty()) {
                artifactList.add(artifact);
                artifactIDs.add(allArtifactIDs.get(i));
            } else if (artifactMatchesSearch(artifact, query)) {
                artifactList.add(artifact);
                artifactIDs.add(allArtifactIDs.get(i));
                searchMatch = true;
            } else {
                searchMatch = artifactMatchesSearch(artifact, query);
            }

            if (categoryMatch && searchMatch) {
                filteredArtifacts.add(artifact);
            }
        }

        applyPagination();
    }

    private void applyPagination() {
        int totalItems = filteredArtifacts.size();

        if (totalItems == 0) {
            artifactList.clear();
            tvPageInfo.setText("Page 0 / 0  Total: 0");
            btnPrevPage.setEnabled(false);
            btnNextPage.setEnabled(false);
            artifactAdapter.notifyDataSetChanged();
            return;
        }


        if (pageSize == Integer.MAX_VALUE) {

            totalPages = 1;
            currentPage = 0;
            artifactList.clear();
            artifactList.addAll(filteredArtifacts);
        } else {
            totalPages = (int) Math.ceil((double) totalItems / pageSize);

            if (currentPage >= totalPages) {
                currentPage = totalPages - 1;
            }
            if (currentPage < 0) {
                currentPage = 0;
            }

            int start = currentPage * pageSize;
            int end = Math.min(start + pageSize, totalItems);

            artifactList.clear();
            artifactList.addAll(filteredArtifacts.subList(start, end));
        }

        updatePaginationUI(totalItems);
        artifactAdapter.notifyDataSetChanged();
    }

    private void updatePaginationUI(int totalItems) {
        if (pageSize == Integer.MAX_VALUE) {
            tvPageInfo.setText("All " + totalItems + " items");
            btnPrevPage.setEnabled(false);
            btnNextPage.setEnabled(false);
        } else {
            String pageText = "Page " + (currentPage + 1) + " / " + totalPages + "  Total: " + totalItems;
            tvPageInfo.setText(pageText);

            btnPrevPage.setEnabled(currentPage > 0);
            btnNextPage.setEnabled(currentPage < totalPages - 1);
        }
    }


    private void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            applyPagination();
        }
    }

    private void nextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            applyPagination();
        }
    }

    private void setupPaginationSpinner() {
        String[] pageOptions = {"12", "24", "All"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                pageOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPagination.setAdapter(adapter);


        int savedPageSize = sharedPreferences.getInt(KEY_PAGE_SIZE, DEFAULT_PAGE_SIZE);

        int position = 0;
        if (savedPageSize == 12) {
            position = 0;
        } else if (savedPageSize == 24) {
            position = 1;
        } else {
            position = 2;
        }
        spinnerPagination.setSelection(position);
        pageSize = savedPageSize;

        spinnerPagination.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent,
                                               View view,
                                               int position,
                                               long id) {
                        String selected = parent.getItemAtPosition(position).toString();
                        if (selected.equals("12")) {
                            pageSize = 12;
                        } else if (selected.equals("24")) {
                            pageSize = 24;
                        } else {
                            pageSize = Integer.MAX_VALUE;
                        }

                        sharedPreferences.edit().putInt(KEY_PAGE_SIZE, pageSize).apply();

                        currentPage = 0;
                        filterArtifacts(searchEditText.getText().toString());
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    }
                }
        );
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

    /**
     * On clicking an artifact, a new screen is opened sent via intents, sending artifact's info
     * @param position of the artifact in the artifactList
     */
    @Override
    public void onArtifactClick(int position) {
        Intent send = new Intent(getContext(), ExpandedView.class);
        send.putExtra("selected_artifact", artifactList.get(position));
        send.putExtra("artifactID", artifactIDs.get(position));
        startActivity(send);
    }

    @Override
    public void onLikeClick(Artifact artifact, int position) {

    }
}