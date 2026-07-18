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

public class EditArtifact extends Fragment {

    private EditText EditName, EditLotnum, EditDesc;

    private Spinner EditPeriod, EditMat, EditCat;
    private Button submit, back;

    private FirebaseDatabase db;

    private DatabaseReference artifactref;


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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.artifact_periods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        EditPeriod.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.artifact_materials, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        EditMat.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getContext(),
                R.array.artifact_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        EditCat.setAdapter(adapter3);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditArtifact();
            }
        });
        back.setOnClickListener(v -> getParentFragmentManager().popBackStack());
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
        artifactref.child("name").setValue(Name);
        artifactref.child("description").setValue(Description);
        artifactref.child("category").setValue(Category);
        artifactref.child("material").setValue(Material);
        artifactref.child("period").setValue(Period);
    }
}