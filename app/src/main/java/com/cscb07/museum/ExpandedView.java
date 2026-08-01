package com.cscb07.museum;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ExpandedView extends AppCompatActivity {

    TextView textName, textLotNum, textDescription, textCategory, textMaterial, textPeriod, textCulturalOrigin, textDimensions, textConditionReport, textLocation, textAcqMethod, textProvenance, textAccNum, textNotes;
    TextView textLotNumLabel, textDescriptionLabel, textCategoryLabel, textMaterialLabel, textPeriodLabel, textCulturalOriginLabel, textDimensionsLabel, textConditionReportLabel, textLocationLabel, textAcqMethodLabel, textProvenanceLabel, textAccNumLabel, textNotesLabel;
    ImageView imagePic;

    Artifact selectedArtifact;

    RecyclerView recyclerComments;
    EditText editTextComment;
    Button buttonAddComment;

    ArrayList<Comment> commentList;
    CommentAdapter commentAdapter;

    DatabaseReference commentsRef;
    FirebaseAuth auth;
    String artifactID;
    

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

        recyclerComments = findViewById(R.id.recyclerComments);
        editTextComment = findViewById(R.id.editTextComment);
        buttonAddComment = findViewById(R.id.buttonAddComment);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerComments.setAdapter(commentAdapter);
        auth = FirebaseAuth.getInstance();
        artifactID = getIntent().getStringExtra("artifactID");
        commentsRef = FirebaseDatabase.getInstance()
            .getReference("artifacts")
            .child(artifactID)
            .child("comments");
        loadComments();

        buttonAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editTextComment.getText().toString();
                FirebaseUser user = auth.getCurrentUser();
                if (user != null && !text.isEmpty()) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(user.getUid());
                    userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            String username = snapshot.getValue(String.class);
                            String commentID = commentsRef.push().getKey();

                            Comment comment = new Comment(commentID, user.getUid(), username, text);
                            commentsRef.child(commentID).setValue(comment);
                            editTextComment.setText("");
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
                }
            }
        });
    

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

        //setting static textViews
        textLotNumLabel = findViewById(R.id.textViewLotNumLabel);
        textDescriptionLabel = findViewById(R.id.textViewDescriptionLabel);
        textCategoryLabel = findViewById(R.id.textViewCategoryLabel);
        textMaterialLabel = findViewById(R.id.textViewMaterialLabel);
        textPeriodLabel = findViewById(R.id.textViewPeriodLabel);
        textCulturalOriginLabel = findViewById(R.id.textViewCulturalOriginLabel);
        textDimensionsLabel = findViewById(R.id.textViewDimensionsLabel);
        textConditionReportLabel = findViewById(R.id.textViewConditionReportLabel);
        textLocationLabel = findViewById(R.id.textViewLocationLabel);
        textAcqMethodLabel = findViewById(R.id.textViewAcqMethodLabel);
        textProvenanceLabel = findViewById(R.id.textViewProvenanceLabel);
        textAccNumLabel = findViewById(R.id.textViewAccNumLabel);
        textNotesLabel = findViewById(R.id.textViewNotesLabel);


        //Add Labels' text
        textLotNumLabel.setText("Lot Number");
        textDescriptionLabel.setText("Description");
        textCategoryLabel.setText("Category");
        textMaterialLabel.setText("Material");
        textPeriodLabel.setText("Period");
        textCulturalOriginLabel.setText("Cultural Origin");
        textDimensionsLabel.setText("Dimensions");
        textConditionReportLabel.setText("Condition Report");
        textLocationLabel.setText("Location");
        textAcqMethodLabel.setText("Acquisition Method");
        textProvenanceLabel.setText("Provenance");
        textAccNumLabel.setText("Accession Number");
        textNotesLabel.setText("Notes");




    }


    private void loadComments() {
        commentsRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot){
                commentList.clear();
                for (DataSnapshot commentSnapshot : snapshot.getChildren()){
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                    }
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error){
            }
        });
    }
}
