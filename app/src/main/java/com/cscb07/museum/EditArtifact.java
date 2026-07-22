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

public class EditArtifact extends Fragment {

    private EditText EditName, EditLotnum, EditDesc;

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



        EditName = view.findViewById(R.id.editTextArtiName);
        EditLotnum = view.findViewById(R.id.editTextArtiLotnum);
        EditPeriod = view.findViewById(R.id.EditPeriodspin);
        EditDesc = view.findViewById(R.id.editTextArtiDesc);
        EditMat = view.findViewById(R.id.spinner3);
        EditCat = view.findViewById(R.id.spinner4);
        submit = view.findViewById(R.id.buttonSubmit);
        back = view.findViewById(R.id.backButtonE);
        uploadImageButton = view.findViewById(R.id.buttonUploadImage);


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

    private void EditArtifact(){

        String Name = EditName.getText().toString().toLowerCase().trim();;
        String Lotnum = EditLotnum.getText().toString().trim();;
        String Period = EditPeriod.getSelectedItem().toString().toLowerCase().trim();;
        String Material = EditMat.getSelectedItem().toString().toLowerCase().trim();;
        String Category = EditCat.getSelectedItem().toString().toLowerCase().trim();
        String Description = EditDesc.getText().toString().toLowerCase().trim();;

        artifactref = db.getReference("artifacts").child(Lotnum);

        if (Name.isEmpty() || Description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUrl != null) {
            artifactref.child("image").setValue(imageUrl);
        }
        artifactref.child("name").setValue(Name);
        artifactref.child("description").setValue(Description);
        artifactref.child("category").setValue(Category);
        artifactref.child("material").setValue(Material);
        artifactref.child("period").setValue(Period);

        Toast.makeText(
                getContext(),
                "Artifact updated successfully",
                Toast.LENGTH_SHORT
        ).show();
    }
}