package com.cscb07.museum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItemFragment extends Fragment {
    private EditText editTextName, editTextDescription;
    private Spinner spinnerCategory, spinnerCategory1, spinnerMaterial, spinnerPeriod;
    private Button buttonAdd;

    private FirebaseDatabase db;
    private DatabaseReference artifactsRef;

    private Artifact artifact;

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
        buttonAdd = view.findViewById(R.id.buttonAdd);

        db = FirebaseDatabase.getInstance("https://b07-project-66023-default-rtdb.firebaseio.com/");

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

        String category = spinnerCategory.getSelectedItem().toString().toLowerCase();

        if (name.isEmpty() || description.isEmpty() || category1.isEmpty() || material.isEmpty() || period.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        artifactsRef = db.getReference("artifacts");
        String lotNum = artifactsRef.push().getKey();
        Artifact artifact = new Artifact(lotNum, name, description, category1, material, period);

        artifactsRef.child(lotNum).setValue(artifact).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
