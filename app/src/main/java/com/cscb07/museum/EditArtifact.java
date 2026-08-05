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

public class EditArtifact extends Fragment {

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

    public EditArtifact() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        db = FirebaseDatabase.getInstance("https://b07-project-66023-default-rtdb.firebaseio.com/");

        // attach variables to XML
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

        searchArtifactView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchArtifactItem selectedItem = (SearchArtifactItem) parent.getItemAtPosition(position);

                if (selectedItem == null || selectedItem.getArtifact() == null) {
                    return;
                }

                selectedFirebaseKey = selectedItem.getFirebaseKey();
                fillEditFields(selectedItem.getArtifact());

                Toast.makeText(
                        getContext(),
                        "Artifact selected",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        loadArtifactsForSearch();

        // Supabase setup
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
    private void loadArtifactsForSearch() {
        DatabaseReference artifactsRef = db.getReference("artifacts");

        artifactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allArtifacts.clear();

                for (DataSnapshot artifactSnapshot : snapshot.getChildren()) {
                    Artifact artifact = artifactSnapshot.getValue(Artifact.class);

                    if (artifact != null) {
                        String firebaseKey = artifactSnapshot.getKey();
                        if (firebaseKey != null) {
                            allArtifacts.add(new SearchArtifactItem(firebaseKey, artifact));
                        }
                    }
                }

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
    //Automatically fill in fields from chosen artifact
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

    private String safe(String value) {
        return value == null ? "" : value;
    }

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
        //override toString to adapt for firebase key
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