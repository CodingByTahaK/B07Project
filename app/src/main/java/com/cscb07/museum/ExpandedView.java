package com.cscb07.museum;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ExpandedView extends AppCompatActivity {

    TextView textName, textLotNum, textDescription, textCategory, textMaterial, textPeriod, textCulturalOrigin, textDimensions, textConditionReport, textLocation, textAcqMethod, textProvenance, textAccNum, textNotes;
    ImageView imagePic;

    Artifact selectedArtifact;

    @Override
    protected void onCreate(Bundle expandedInstance){
        super.onCreate(expandedInstance);
        setContentView(R.layout.expanded_view_activity_item_adapter);

        if (getIntent().hasExtra("selected_artifact")){
            Log.d("passed if", "onCreate: got here");
            selectedArtifact = getIntent().getParcelableExtra("selected_artifact");
        }

        Log.d("passed the if", "onCreate: got here");
        textName = findViewById(R.id.textViewName);
        textLotNum = findViewById(R.id.textViewLotNum);
        textDescription = findViewById(R.id.textViewDescription);
        textCategory = findViewById(R.id.textViewCategory);
        textMaterial = findViewById(R.id.textViewMaterial);
        textPeriod= findViewById(R.id.textViewPeriod);
        textCulturalOrigin = findViewById(R.id.textViewCulturalOrigin);
        textDimensions = findViewById(R.id.textViewDimensions);
        textConditionReport = findViewById(R.id.textViewConditionReport);
        textLocation = findViewById(R.id.textViewLocation);
        textAcqMethod = findViewById(R.id.textViewAcqMethod);
        textProvenance = findViewById(R.id.textViewProvenance);
        textAccNum = findViewById(R.id.textViewAccNum);
        textNotes = findViewById(R.id.textViewNotes);

        imagePic = findViewById(R.id.imageViewPic);

        selectedArtifact = new Artifact();
        //Issue with this line, keep getting null object
        selectedArtifact = getIntent().getParcelableExtra("selected_artifact");

        //going to have to handle what happens if a field is null, or else app crashes, prob checking + default values
        textName.setText(selectedArtifact.getName());
        textLotNum.setText(selectedArtifact.getLotNum());
        textDescription.setText(selectedArtifact.getDescription());
        textCategory.setText(selectedArtifact.getCategory());
        textMaterial.setText(selectedArtifact.getMaterial());
        textPeriod.setText(selectedArtifact.getPeriod());
        textCulturalOrigin.setText(selectedArtifact.getCulturalOrigin());
        textDimensions.setText(selectedArtifact.getDimensions());
        textConditionReport.setText(selectedArtifact.getConditionReport());
        textLocation.setText(selectedArtifact.getLocation());
        textAcqMethod.setText(selectedArtifact.getAcqMethod());
        textProvenance.setText(selectedArtifact.getProvenance());
        textAccNum.setText(selectedArtifact.getAccNum());
        textNotes.setText(selectedArtifact.getNotes());
        Picasso.get().load(selectedArtifact.getImage()).into(imagePic);


    }
}
