package com.cscb07.museum;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
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
 * EditArtifact fragment is a subclass of Fragment which is used for editing existing artifacts
 * in the database. The artifact must already exist to be edited.
 */
public class EditArtifact extends Fragment {
    /**
     * below are just defining the fields to attach to the xml, firebase and supabase
     */
    private EditText EditName, EditLotnum, EditDesc, culturalOrigin, dimensions,
            conditionReport, currentLocation, accMethod, provenance, accNum, notes;

    private Spinner EditPeriod, EditMat, EditCat;
    private Button submit, back, uploadImageButton;
    private AutoCompleteTextView searchArtifactView;

    private FirebaseDatabase db;
    private DatabaseReference artifactref;

    private SupabaseImageUploader imageUploader;
    private Uri imageUri;
    private String imageUrl;

    private String selectedFirebaseKey;

    private final List<SearchArtifactItem> allArtifacts = new ArrayList<>();
    private ArrayAdapter<SearchArtifactItem> searchAdapter;

    /**
     * this is an empty constructor which is required by firebase and Android
     */
    public EditArtifact() {
        // Required empty public constructor
    }

    /**
     * This method is triggered whenever a new EditArtifact fragment is requested
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the edit fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        // assign a variable to be a firebase reference
        db = FirebaseDatabase.getInstance("https://b07-project-66023-default-rtdb.firebaseio.com/");

        /**
         * below is where variables are connected to the actual XML using the
         * findViewById and the R class
         * the method takes in a valid ID of an element in the associated XML file
         */
        searchArtifactView = view.findViewById(R.id.searchArtifactView);

        EditName = view.findViewById(R.id.editTextArtiName);
        EditLotnum = view.findViewById(R.id.editTextArtiLotnum);
        EditPeriod = view.findViewById(R.id.EditPeriodspin);
        EditDesc = view.findViewById(R.id.editTextArtiDesc);
        EditMat = view.findViewById(R.id.spinner3);
        EditCat = view.findViewById(R.id.spinner4);
        submit = view.findViewById(R.id.buttonSubmit);
        back = view.findViewById(R.id.backButtonE);
        uploadImageButton = view.findViewById(R.id.buttonUploadImage);

        culturalOrigin = view.findViewById(R.id.editCulturalOrigin);
        dimensions = view.findViewById(R.id.editDimensions);
        conditionReport = view.findViewById(R.id.editConditionReport);
        currentLocation = view.findViewById(R.id.editCurrentLocation);
        accMethod = view.findViewById(R.id.editAccMethod);
        provenance = view.findViewById(R.id.editProvenance);
        accNum = view.findViewById(R.id.editAccNum);
        notes = view.findViewById(R.id.editNotes);

        // setting up spinners with adapters
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.artifact_periods,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        EditPeriod.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                getContext(),
                R.array.artifact_materials,
                android.R.layout.simple_spinner_item
        );
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        EditMat.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(
                getContext(),
                R.array.artifact_categories,
                android.R.layout.simple_spinner_item
        );
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        EditCat.setAdapter(adapter3);

        // search dropdown setup
        searchAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                allArtifacts
        );
        searchArtifactView.setAdapter(searchAdapter);
        searchArtifactView.setThreshold(1);
        searchArtifactView.setOnClickListener(v -> searchArtifactView.showDropDown());
        /**
         * this method is activated when an artifact from the drop down menu is clicked
         * @param is the adapter view which is basically just the list of artifacts
         * It gets the position of the clicked artifact and gets it's associated firebase key
         * so the EditArtifact method knows what artifact in the database to change.
         */
        searchArtifactView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchArtifactItem selectedItem = (SearchArtifactItem) parent.getItemAtPosition(position);
                //if no object is chosen, return
                if (selectedItem == null || selectedItem.getArtifact() == null) {
                    return;
                }

                selectedFirebaseKey = selectedItem.getFirebaseKey();
                fillEditFields(selectedItem.getArtifact());
                //toast message to notify user of successful selection
                Toast.makeText(
                        getContext(),
                        "Artifact selected",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        /**
         * this method loads in the artifacts into the search spinner
         */
        loadArtifactsForSearch();

        /**
         * This is the supabase setup, it basically triggers a gallery activity
         *  and allows the user to choose a photo from their gallery and upload it to
         *  our supabase bucket
         */
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(
                        new ActivityResultContracts.PickVisualMedia(),
                        uri -> {

                            if (uri != null) {

                                Log.d("PhotoPicker", "Selected URI: " + uri);

                                imageUri = uri;
                                imageUploader = new SupabaseImageUploader(requireContext());

                                String lotNum = selectedFirebaseKey;
                                if (lotNum == null || lotNum.trim().isEmpty()) {
                                    lotNum = EditLotnum.getText().toString().trim();
                                }

                                if (lotNum.isEmpty()) {
                                    Toast.makeText(
                                            getContext(),
                                            "Please select an artifact first",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    return;
                                }

                                imageUploader.uploadImage(
                                        uri,
                                        lotNum,
                                        new SupabaseImageUploader.UploadCallback() {
                                            @Override
                                            public void onSuccess(String publicUrl) {
                                                imageUrl = publicUrl;

                                                Log.d("Supabase", "Image uploaded: " + publicUrl);
                                                //notification to tell user of successful upload
                                                Toast.makeText(
                                                        getContext(),
                                                        "Image uploaded successfully",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }

                                            @Override
                                            public void onError(String message) {
                                                Log.e("Supabase", "Upload failed: " + message);

                                                Toast.makeText(
                                                        getContext(),
                                                        "Image upload failed",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                        }
                                );

                            } else {
                                Log.d("PhotoPicker", "No image selected");
                            }
                        }
                );
        /**
         * assigning the submit button to trigger the EditArtifact and existLotNum methods on click
         */
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredLotNum = EditLotnum.getText().toString().trim();
                existLotNum(enteredLotNum);
            }
        });

        back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        uploadImageButton.setOnClickListener(v -> {
            pickMedia.launch(
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE
                            )
                            .build()
            );
        });

        return view;
    }
    //get the database key from chosen artifact

    /**
     * This method iterates through the firebase database to show the adapter used in the search bar
     * what artifacts are currently in the database
     * This method makes use of the helper class SearchArtifactItem in order to keep the firebase key and artifact associated
     *
     */
    private void loadArtifactsForSearch() {
        DatabaseReference artifactsRef = db.getReference("artifacts");

        artifactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allArtifacts.clear();
                /**This is the important loop that iterates through the database
                the statement snapshot.getChildren() returns every artifact in the database
                Hence the for loop says "for each artifact in the database, bundle the artifact with its key
                 and "
                 */
                for (DataSnapshot artifactSnapshot : snapshot.getChildren()) {
                    Artifact artifact = artifactSnapshot.getValue(Artifact.class);

                    if (artifact != null) {
                        String firebaseKey = artifactSnapshot.getKey();
                        if (firebaseKey != null) {
                            allArtifacts.add(new SearchArtifactItem(firebaseKey, artifact));
                        }
                    }
                }
                //update adapter
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        getContext(),
                        "Error loading artifacts: " + error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    /**
     * When an artifact gets chosen from the search bar, it loads in all its information into
     * the textfields/spinners to make it easier for the user
     * @param artifact
     *
     */
    private void fillEditFields(Artifact artifact) {
        //set texts
        EditName.setText(safe(artifact.getName()));
        EditLotnum.setText(safe(artifact.getLotNum()));
        EditDesc.setText(safe(artifact.getDescription()));

        culturalOrigin.setText(safe(artifact.getCulturalOrigin()));
        dimensions.setText(safe(artifact.getDimensions()));
        conditionReport.setText(safe(artifact.getConditionReport()));
        currentLocation.setText(safe(artifact.getLocation()));
        accMethod.setText(safe(artifact.getAcqMethod()));
        provenance.setText(safe(artifact.getProvenance()));
        accNum.setText(safe(artifact.getAccNum()));
        notes.setText(safe(artifact.getNotes()));

        setSpinnerSelection(EditPeriod, safe(artifact.getPeriod()));
        setSpinnerSelection(EditMat, safe(artifact.getMaterial()));
        setSpinnerSelection(EditCat, safe(artifact.getCategory()));
    }

    /**
     * this method is used by fillEditFields to set the spinners to the right values
     * @param spinner
     * @param value
     */
    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter == null) {
            return;
        }

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i) != null &&
                    adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    /**
     * Since the value Lotnum must be unique, this method checks whether the new Lotnum already exist
     * Of course, it skips the check against itself as that would mean you must chagne the lotnum every time
     * If the check if passed, it calls EditArtifact to actually instantiate the changes wanted
     * @param selectedLotNum
     */
    public void existLotNum(String selectedLotNum) {
        DatabaseReference ref = db.getReference("artifacts");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot artifactSnapshot : snapshot.getChildren()) {
                    //here i want to skip the check against the already chosen artifacts own Lotnum
                    if (selectedFirebaseKey != null && selectedFirebaseKey.equals(artifactSnapshot.getKey())) {
                        continue;
                    }

                    Artifact artifact = artifactSnapshot.getValue(Artifact.class);

                    if (artifact != null && artifact.getLotNum() != null) {
                        if (artifact.getLotNum().trim().equalsIgnoreCase(selectedLotNum.trim())) {
                            Toast.makeText(
                                    getContext(),
                                    "Lot Number already exists, choose another",
                                    Toast.LENGTH_LONG
                            ).show();
                            EditLotnum.setText("");
                            return;
                        }
                    }
                }
                //call to the actual edit function
                EditArtifact();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        getContext(),
                        "Error loading artifacts: " + error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    /**
     * this it the main function for editing
     * this method takes the data from all the fields and changes the values in firebase
     * To avoid issues, the name and Lotnum can never be empty but since they are autofilled
     * This should pose no problems to the user
     */
    private void EditArtifact() {

        if (selectedFirebaseKey == null || selectedFirebaseKey.trim().isEmpty()) {
            Toast.makeText(
                    getContext(),
                    "Please select an artifact from the search bar first",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // obtaining values from textboxes
        String Name = EditName.getText().toString().toLowerCase().trim();
        String Lotnum = EditLotnum.getText().toString().trim();
        String Period = EditPeriod.getSelectedItem().toString().toLowerCase().trim();
        String Material = EditMat.getSelectedItem().toString().toLowerCase().trim();
        String Category = EditCat.getSelectedItem().toString().toLowerCase().trim();
        String Description = EditDesc.getText().toString().toLowerCase().trim();

        String culturalOriginText = culturalOrigin.getText().toString();
        String dimensionsText = dimensions.getText().toString();
        String conditionReportText = conditionReport.getText().toString();
        String currentLocationText = currentLocation.getText().toString();
        String accMethodText = accMethod.getText().toString();
        String provenanceText = provenance.getText().toString();
        String accNumText = accNum.getText().toString();
        String notesText = notes.getText().toString();

        // name, description, and lot number can't be empty
        if (Name.isEmpty() || Description.isEmpty() || Lotnum.isEmpty()) {
            Toast.makeText(
                    getContext(),
                    "Please fill out all required fields",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // use the selected Firebase key, not the typed lot number
        artifactref = db.getReference("artifacts").child(selectedFirebaseKey);

        // update artifact
        if (imageUrl != null) {
            artifactref.child("image").setValue(imageUrl);
        }

        artifactref.child("name").setValue(Name);
        artifactref.child("description").setValue(Description);
        artifactref.child("category").setValue(Category);
        artifactref.child("material").setValue(Material);
        artifactref.child("period").setValue(Period);
        artifactref.child("culturalOrigin").setValue(culturalOriginText);
        artifactref.child("dimensions").setValue(dimensionsText);
        artifactref.child("conditionReport").setValue(conditionReportText);
        artifactref.child("currentLocation").setValue(currentLocationText);
        artifactref.child("accMethod").setValue(accMethodText);
        artifactref.child("provenance").setValue(provenanceText);
        artifactref.child("accNum").setValue(accNumText);
        artifactref.child("notes").setValue(notesText);
        artifactref.child("lotNum").setValue(Lotnum);

        Toast.makeText(
                getContext(),
                "Artifact updated successfully",
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * this helper function is used when auto filling artifact details so if a field is null
     * it simply returns the empty string
     * @param value
     * @return String
     */

    private String safe(String value) {
        return value == null ? "" : value;
    }

    /**
     * this is a helper class made to bundle an Artifact and the firebase key
     * associated with it together
     */
    private static class SearchArtifactItem {
        private final String firebaseKey;
        private final Artifact artifact;

        SearchArtifactItem(String firebaseKey, Artifact artifact) {
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
         * Overriding toString so that information from the SearchArtifact object is
         * presented properly
         * @return String
         */
        @Override
        public String toString() {
            String name = artifact != null ? artifact.getName() : "";
            String lotNum = artifact != null ? artifact.getLotNum() : "";

            if (name == null) {
                name = "";
            }
            if (lotNum == null) {
                lotNum = "";
            }

            if (!name.isEmpty() && !lotNum.isEmpty()) {
                return name + " (" + lotNum + ")";
            } else if (!name.isEmpty()) {
                return name;
            } else {
                return lotNum.isEmpty() ? firebaseKey : lotNum;
            }
        }
    }
}