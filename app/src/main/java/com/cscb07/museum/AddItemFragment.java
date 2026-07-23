//will delete this class in the future, as it is leftover from the starter code
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;

public class AddItemFragment extends Fragment {
    private EditText editTextName, editTextDescription, editTextCulturalOrigin, editTextDimensions, editTextConditionReport, editTextCurrentLocation, editTextAccMethod, editTextProvenance, editTextAccNum, editTextNotes;
    private Spinner spinnerCategory, spinnerCategory1, spinnerMaterial, spinnerPeriod;
    private Button buttonAdd, buttonUploadImg;

    private FirebaseDatabase db;
    private DatabaseReference artifactsRef;

    private SupabaseImageUploader imageUploader;
    private String imgURL;
    private Uri imgURI;
    private Artifact artifact;
    private String lotNum;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
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
        lotNum = artifactsRef.push().getKey();

        // Set up the spinner with categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

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


        //code snippet from android docs, credit: https://developer.android.com/training/data-storage/shared/photo-picker#java
        // Registers a photo picker activity launcher in single-select mode.
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                imgURI = uri;

                //upload image to supabase bucket
                imageUploader = new SupabaseImageUploader(requireContext());
                //later try to find a way to put an acutal lotnumber, problem: i upload image before saving lotnum to database,

                imageUploader.uploadImage(uri, "lotNum", new
                        SupabaseImageUploader.UploadCallback() {
                            @Override
                            public void onSuccess(String publicUrl) {
                                imgURL = publicUrl;
                                Log.d("Supabase", "success");

                            }
                            @Override
                            public void onError(String message) {
                                Log.d("Supabase", "failed");
                                Log.d("Supabase error messaage", message.codePoints() + "uri: " + uri);

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
                addItem();
            }
        });

        return view;
    }

    private void addItem() {
        String name = editTextName.getText().toString().trim();
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

        String category = spinnerCategory.getSelectedItem().toString().toLowerCase();

        if (name.isEmpty() || description.isEmpty() || category1.isEmpty() || material.isEmpty() || period.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

//        artifactsRef = db.getReference("artifacts");
//        String lotNum = artifactsRef.push().getKey();
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
