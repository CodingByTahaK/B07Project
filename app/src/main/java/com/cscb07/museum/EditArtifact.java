package com.cscb07.museum;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.net.Uri;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class EditArtifact extends Fragment {

    private EditText EditName, EditLotnum, EditDesc, culturalOrigin, dimensions, conditionReport, currentLocation, accMethod, provenance, accNum, notes;

    private Spinner EditPeriod, EditMat, EditCat;
    private Button submit, back, uploadImageButton;;

    private FirebaseDatabase db;

    private DatabaseReference artifactref;


    private SupabaseImageUploader imageUploader;

    private Uri imageUri;

    private String imageUrl;

    public EditArtifact() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        db = FirebaseDatabase.getInstance("https://b07-project-66023-default-rtdb.firebaseio.com/");


        //Attaching values to text boxes and buttons
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

        //setting up spinners with adapters
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.artifact_periods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        EditPeriod.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.artifact_materials, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        EditMat.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getContext(),
                R.array.artifact_categories, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        EditCat.setAdapter(adapter3);

        //supabase setup
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(
                        new ActivityResultContracts.PickVisualMedia(),
                        uri -> {

                            if (uri != null) {

                                Log.d("PhotoPicker", "Selected URI: " + uri);

                                imageUri = uri;

                                imageUploader =
                                        new SupabaseImageUploader(requireContext());

                                String lotNum =
                                        EditLotnum.getText().toString().trim();

                                if (lotNum.isEmpty()) {
                                    Toast.makeText(
                                            getContext(),
                                            "Please enter a lot number first",
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

                                                Log.d(
                                                        "Supabase",
                                                        "Image uploaded: " + publicUrl
                                                );

                                                Toast.makeText(
                                                        getContext(),
                                                        "Image uploaded successfully",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }

                                            @Override
                                            public void onError(String message) {

                                                Log.e(
                                                        "Supabase",
                                                        "Upload failed: " + message
                                                );

                                                Toast.makeText(
                                                        getContext(),
                                                        "Image upload failed",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                        }
                                );

                            } else {

                                Log.d(
                                        "PhotoPicker",
                                        "No image selected"
                                );
                            }
                        }
                );

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditArtifact();
            }
        });
        back.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        //setting upload image button on clicks
        uploadImageButton.setOnClickListener(v -> {

            pickMedia.launch(
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(
                                    ActivityResultContracts
                                            .PickVisualMedia
                                            .ImageOnly.INSTANCE
                            )
                            .build()
            );

        });

        return view;
    }

    private void EditArtifact() {

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

        // find artifact using lotnum
        artifactref = db.getReference("artifacts").child(Lotnum);

        // check if the artifact exists
        artifactref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // do this if artifact exists
                if (snapshot.exists()) {

                    // only change the image if a new image was uploaded
                    if (imageUrl != null) {
                        artifactref.child("image").setValue(imageUrl);
                    }

                    // send data to firebase
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

                    // Tell user the artifact was successfully updated
                    Toast.makeText(
                            getContext(),
                            "Artifact updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                } else {

                    // Artifact does not exist so tell user
                    Toast.makeText(
                            getContext(),
                            "No artifact found with that Lot Number",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(
                        getContext(),
                        "Database error: " + error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}