/**
 * File: AddItemFragment.java
 *
 * Version History:
 * v1.0: Initial implementation
 * v1.1: Added image field capability
 * v1.2: Added null checking
 * v1.3: Refactored lot number field & readability of code
 *
 * Date: Aug 03, 2026
 *
 */

package com.cscb07.museum;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Description: This is the AddItemFragment that allows users to add new Artifact items to the realtime database
 * @version 1.3 03 Aug 2026
 */

public class AddItemFragment extends Fragment {
    private EditText editTextName, editTextLotNum, editTextDescription, editTextCulturalOrigin, editTextDimensions, editTextConditionReport, editTextCurrentLocation, editTextAccMethod, editTextProvenance, editTextAccNum, editTextNotes;
    private Spinner spinnerCategory1, spinnerMaterial, spinnerPeriod;
    private Button buttonAdd, buttonUploadImg;
    private FirebaseDatabase db;
    private DatabaseReference artifactsRef, lotNumRef;
    private SupabaseImageUploader imageUploader;
    private String imgURL;
    private Uri imgURI;
    private String lotNumCurrent;


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
     * @return
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        editTextLotNum = view.findViewById(R.id.editTextLotnum);
        editTextName = view.findViewById(R.id.editTextName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        spinnerCategory1 = view.findViewById(R.id.spinnerCategory1);
        spinnerMaterial = view.findViewById(R.id.spinnerMaterial);
        spinnerPeriod = view.findViewById(R.id.spinnerPeriod);

        editTextCulturalOrigin = view.findViewById(R.id.editCulturalOrigin);
        editTextDimensions = view.findViewById(R.id.editDimensions);
        editTextConditionReport = view.findViewById(R.id.editConditionReport);
        editTextCurrentLocation = view.findViewById(R.id.editCurrentLocation);
        editTextAccMethod = view.findViewById(R.id.editAccMethod);
        editTextProvenance = view.findViewById(R.id.editProvenance);
        editTextAccNum = view.findViewById(R.id.editAccNum);
        editTextNotes = view.findViewById(R.id.editNotes);

        buttonAdd = view.findViewById(R.id.buttonAdd);
        buttonUploadImg = view.findViewById(R.id.buttonUploadImg);

        db = FirebaseDatabase.getInstance("https://b07-project-66023-default-rtdb.firebaseio.com/");
        artifactsRef = db.getReference("artifacts");
        lotNumRef = db.getReference("artifacts");

        // Set up the spinner with categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);

        // Set up the spinner with artifact categories
        ArrayAdapter<CharSequence> adapterCategories = ArrayAdapter.createFromResource(getContext(),
                R.array.artifact_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory1.setAdapter(adapterCategories);

        // Set up the spinner with artifact materials
        ArrayAdapter<CharSequence> adapterMaterials = ArrayAdapter.createFromResource(getContext(),
                R.array.artifact_materials, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterial.setAdapter(adapterMaterials);

        // Set up the spinner with artifact periods
        ArrayAdapter<CharSequence> adapterPeriods = ArrayAdapter.createFromResource(getContext(),
                R.array.artifact_periods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriod.setAdapter(adapterPeriods);


        // Registers a photo picker activity launcher in single-select mode.
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {

            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                imgURI = uri;

                //upload image to predetermined Supabase bucket
                imageUploader = new SupabaseImageUploader(requireContext());

                imageUploader.uploadImage(uri, "lotNum", new
                        SupabaseImageUploader.UploadCallback() {
                            @Override
                            public void onSuccess(String publicUrl) {
                                imgURL = publicUrl;
                                Toast.makeText(
                                        getContext(),
                                        "Image Uploaded Successfully",
                                        Toast.LENGTH_LONG
                                ).show();

                            }
                            @Override
                            public void onError(String message) {
                                Toast.makeText(
                                        getContext(),
                                        "Error uploading image: " + message,
                                        Toast.LENGTH_LONG
                                ).show();

                            }
                        });

            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        //button triggers photo activity picker
        buttonUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());

            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get plain lot number user selected to compare later
                lotNumCurrent = editTextLotNum.getText().toString().trim();
                existLotNum(lotNumCurrent);
            }
        });

        return view;
    }

    /**
     * Checks if the selected lot number exists in the database
     * @param selectedLotNum The lot Number to check
     */
    public void existLotNum(String selectedLotNum) {

        lotNumRef.addListenerForSingleValueEvent(new ValueEventListener() {

            /**
             *
             * @param snapshot The current data at the location
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot artifactSnapshot : snapshot.getChildren()) {
                    Artifact artifact = artifactSnapshot.getValue(Artifact.class);

                    if (artifact.getLotNum() != null) {

                        //Comparing use selected lot num with database
                        if ((artifact.getLotNum().trim()).equals(selectedLotNum)) {
                            Toast.makeText(
                                    getContext(),
                                    "Lot Number already exists, choose another",
                                    Toast.LENGTH_LONG
                            ).show();
                            editTextLotNum.setText("");
                            return;
                        }
                    }
                }
                //success, lot number does not exist
                addItem();
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
     * Adds the Artifact with the user selected fields to the Database
     */

    private void addItem() {
        String name = editTextName.getText().toString().trim();
        String lotNum = editTextLotNum.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String category1 = spinnerCategory1.getSelectedItem().toString().toLowerCase();
        String material = spinnerMaterial.getSelectedItem().toString().toLowerCase();
        String period = spinnerPeriod.getSelectedItem().toString().toLowerCase();

        String culturalOrigin = editTextCulturalOrigin.getText().toString().trim();
        String dimensions = editTextDimensions.getText().toString().trim();
        String conditionReport = editTextConditionReport.getText().toString().trim();
        String currentLocation = editTextCurrentLocation.getText().toString().trim();
        String accMethod = editTextAccMethod.getText().toString().trim();
        String provenance = editTextProvenance.getText().toString().trim();
        String accNum = editTextAccNum.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();
        String image = imgURL;

        if (name.isEmpty() || lotNum.isEmpty() || description.isEmpty() || category1.isEmpty() || material.isEmpty() || period.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Artifact artifact = new Artifact(lotNum, name, description, category1, material, period, culturalOrigin, dimensions, conditionReport, currentLocation, accMethod, provenance, accNum, notes, image);

        artifactsRef.child(lotNum).setValue(artifact).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
