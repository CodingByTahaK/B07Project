package com.cscb07.museum;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Delete fragment class is a subclass of fragment
 * It cantains elements like buttons, database reference, search text views, etc
 */
public class Delete_fragment extends Fragment {
    /**
     * Declaring all elements
     */
    private Button deletebutton, goback;
    private AutoCompleteTextView searchdelete;

    private FirebaseDatabase db;

    private String selectedFirebaseKey;

    private final List<SearchArtifactItem> allArtifacts = new ArrayList<>();

    private ArrayAdapter<SearchArtifactItem> searchAdapterdelete;

    /**
     * required empty constructor
     */
    public Delete_fragment() {
    }

    /**
     * this method is triggered whenever a new delete fragment is made
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_delete_fragment,
                container,
                false
        );

        db = FirebaseDatabase.getInstance(
                "https://b07-project-66023-default-rtdb.firebaseio.com/"
        );
        /**
         * Attaching declared variables and XML through method findViewByID and R class
         */
        searchdelete = view.findViewById(R.id.searchdelete);
        deletebutton = view.findViewById(R.id.buttondelete);
        goback = view.findViewById(R.id.goback1);

        searchAdapterdelete = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                allArtifacts
        );

        searchdelete.setAdapter(searchAdapterdelete);

        searchdelete.setThreshold(1);

        searchdelete.setOnClickListener(v ->
                searchdelete.showDropDown()
        );
        /**
         * setting the back button to delete the current delete fragment and return to parent fragment
         */
        goback.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        /**
         * assigning the search bar an on click listener for whenever an artifact is selected
         */
        searchdelete.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    /**
                     * On call the onItemClick method will get the artifact at the clicked position
                     * and store its associated firebase key so we know what artifact to delete
                     * @param parent
                     * @param view
                     * @param position
                     * @param id
                     */
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        SearchArtifactItem selectedItem =
                                (SearchArtifactItem)
                                        parent.getItemAtPosition(position);

                        if (selectedItem == null ||
                                selectedItem.getArtifact() == null) {

                            return;
                        }

                        selectedFirebaseKey =
                                selectedItem.getFirebaseKey();

                        Toast.makeText(
                                getContext(),
                                "Artifact selected: " +
                                        selectedItem.getArtifact().getName(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
        /**
         * method call to load in the artifacts into the search bar
         */
        loadArtifactsForSearch();
        /**
         * This on click method calls the delete method on the selected artifact
         * if a user clicks this button without selecting an artifact, it will fail
         * The method will prompt the user with a confirmation screen to avoid accidental deletion
         */
        deletebutton.setOnClickListener(v -> {

            if (selectedFirebaseKey == null ||
                    selectedFirebaseKey.trim().isEmpty()) {

                Toast.makeText(
                        getContext(),
                        "Please select an artifact from the search bar first",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Artifact")
                    .setMessage(
                            "Are you sure you want to delete this artifact? " +
                                    "This action cannot be undone."
                    )
                    .setPositiveButton(
                            "Delete",
                            (dialog, which) -> {
                                deleteArtifact();
                            }
                    )
                    .setNegativeButton(
                            "Cancel",
                            (dialog, which) -> {
                                dialog.dismiss();
                            }
                    )
                    .show();
        });

        return view;
    }

    /**
     * this is the method used to load in the artifacts into the search bar
     * It is the same as in Edit Artifact and uses a for loop to iterate through the database
     * and fetches artifact and bundles it together with its key using SearchArtifactItem class
     */
    private void loadArtifactsForSearch() {

        DatabaseReference artifactsRef =
                db.getReference("artifacts");

        artifactsRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        allArtifacts.clear();

                        for (DataSnapshot artifactSnapshot :
                                snapshot.getChildren()) {

                            Artifact artifact =
                                    artifactSnapshot.getValue(
                                            Artifact.class
                                    );

                            if (artifact != null) {

                                String firebaseKey =
                                        artifactSnapshot.getKey();

                                if (firebaseKey != null) {

                                    allArtifacts.add(
                                            new SearchArtifactItem(
                                                    firebaseKey,
                                                    artifact
                                            )
                                    );
                                }
                            }
                        }

                        searchAdapterdelete.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                getContext(),
                                "Error loading artifacts: " +
                                        error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    /**
     * this is the actual method that deletes the artifact
     * it Uses the stored firebase key to find the artifact in the database and erase it
     */
    private void deleteArtifact() {

        DatabaseReference artifactRef =
                db.getReference("artifacts")
                        .child(selectedFirebaseKey);

        artifactRef.removeValue()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            getContext(),
                            "Artifact deleted successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    for (int i = 0;
                         i < allArtifacts.size();
                         i++) {

                        if (allArtifacts.get(i)
                                .getFirebaseKey()
                                .equals(selectedFirebaseKey)) {

                            allArtifacts.remove(i);

                            break;
                        }
                    }

                    searchAdapterdelete.notifyDataSetChanged();

                    selectedFirebaseKey = null;

                    searchdelete.setText("");
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            getContext(),
                            "Failed to delete artifact: " +
                                    e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    /**
     * A helper class used to bundle the artifact and its firebase key together
     */
    private static class SearchArtifactItem {

        private final String firebaseKey;
        private final Artifact artifact;

        SearchArtifactItem(
                String firebaseKey,
                Artifact artifact) {

            this.firebaseKey = firebaseKey;
            this.artifact = artifact;
        }

        public String getFirebaseKey() {

            return firebaseKey;
        }

        public Artifact getArtifact() {

            return artifact;
        }

        /**
         * Overrides to string so that information mainly name and Lotnum are displayed correctly
         * @return String
         */
        @Override
        public String toString() {

            String name =
                    artifact != null
                            ? artifact.getName()
                            : "";

            String lotNum =
                    artifact != null
                            ? artifact.getLotNum()
                            : "";

            if (name == null) {
                name = "";
            }

            if (lotNum == null) {
                lotNum = "";
            }

            if (!name.isEmpty() &&
                    !lotNum.isEmpty()) {

                return name +
                        " (" +
                        lotNum +
                        ")";
            }

            else if (!name.isEmpty()) {

                return name;
            }

            else {

                return lotNum.isEmpty()
                        ? firebaseKey
                        : lotNum;
            }
        }
    }
}