/*
 * RecyclerViewFragment
 * Version 1.0
 * July 23, 2026
 */

package com.cscb07.museum;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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

public class RecyclerViewFragment extends Fragment {

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
    private List<Artifact> filteredArtifacts;

    private Spinner spinnerCategory;
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

        spinnerCategory = view.findViewById(R.id.spinnerCategory);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        getContext(),
                        R.array.categories_array,
                        android.R.layout.simple_spinner_item
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerCategory.setAdapter(adapter);

        searchEditText = view.findViewById(R.id.searchEditText);
        spinnerPagination = view.findViewById(R.id.spinnerPagination);
        tvPageInfo = view.findViewById(R.id.tvPageInfo);
        btnPrevPage = view.findViewById(R.id.btnPrevPage);
        btnNextPage = view.findViewById(R.id.btnNextPage);

        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, 0);

        Button savedArtifactsButton =
                view.findViewById(R.id.savedArtifactsButton);

        savedArtifactsButton.setOnClickListener(clickedView -> {
            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.openSavedArtifacts();
        });

        artifactList = new ArrayList<>();
        allArtifacts = new ArrayList<>();
        filteredArtifacts = new ArrayList<>();

        setupPaginationSpinner();

        btnPrevPage.setOnClickListener(v -> prevPage());
        btnNextPage.setOnClickListener(v -> nextPage());

        artifactAdapter = new ArtifactAdapter(artifactList);
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

        artifactsRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(
                    @NonNull DataSnapshot dataSnapshot) {

                allArtifacts.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Artifact artifact = snapshot.getValue(Artifact.class);
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
        filteredArtifacts.clear();

        for (int i = 0; i < allArtifacts.size(); i++) {

            Artifact artifact = allArtifacts.get(i);

            if (query.isEmpty()) {
                artifactList.add(artifact);
            } else if (artifactMatchesSearch(artifact, query)) {
                artifactList.add(artifact);
            }

            boolean categoryMatch = false;
            if (selectedCategory.equals("All")) {

                categoryMatch = true;
            } else {
                String artifactCategory = artifact.getCategory();
                if (artifactCategory != null && artifactCategory.equals(selectedCategory)) {

                    categoryMatch = true;
                }
            }

            boolean searchMatch = false;
            if (query.isEmpty()) {
                searchMatch = true;
            } else if (artifactMatchesSearch(artifact, query)) {
                searchMatch = true;
            }

            if (categoryMatch && searchMatch) {
                filteredArtifacts.add(artifact);
            }
        }


        artifactAdapter.notifyDataSetChanged();
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
}