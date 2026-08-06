package com.cscb07.museum;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class Artifact implements Parcelable {

    //Mandatory Fields
    private String lotNum;
    private String name;
    private String description;
    private String category;
    private String material;
    private String period;
    private String culturalOrigin;
    private String dimensions;
    private String conditionReport;
    private String location;
    private String acqMethod;
    private String provenance;
    private String accNum;
    private String notes;
    private String image;
    private int likeCount = 0;
    private boolean isLiked = false;
    private boolean isSaved = false;
    private FirebaseUser user;

    // Listener for UI to react when Firebase like data is loaded
    private Runnable onStatusLoaded;
    private boolean isStatusLoaded = false;

    public Artifact(){
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }


    public Artifact(String lotNum, String name, String description, String category, String material, String period, String culturalOrigin, String dimensions, String conditionReport, String location, String acqMethod, String provenance, String accNum, String notes, String image) {
        this();
        this.lotNum = lotNum;
        this.name = name;
        this.description = description;
        this.category = category;
        this.material = material;
        this.period = period;
        this.culturalOrigin = culturalOrigin;
        this.dimensions = dimensions;
        this.conditionReport = conditionReport;
        this.location = location;
        this.acqMethod = acqMethod;
        this.provenance = provenance;
        this.accNum = accNum;
        this.notes = notes;
        this.image = image;
        loadUserStatus();
    }

    public void setOnStatusLoadedListener(Runnable listener) {
        this.onStatusLoaded = listener;
        if (isStatusLoaded && onStatusLoaded != null) {
            onStatusLoaded.run();
        }
    }


    // Load current user's status: liked and saved artifacts, and artifact's like count
    public void loadUserStatus() {
        if (lotNum == null) return;
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User's information regarding likes and saves
            DatabaseReference userArtifactRef = FirebaseDatabase.getInstance().getReference("/users/" + user.getUid() + "/likedAndSavedArtifacts/" + lotNum);
            // Get whether the user has liked or saved the artfiact
            userArtifactRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Boolean liked = snapshot.child("liked").getValue(Boolean.class);
                        if (liked != null) {
                            isLiked = liked;
                        }
                        Boolean saved = snapshot.child("saved").getValue(Boolean.class);
                        if (saved != null) {
                            isSaved = saved;
                        }
                    }

                    // Artifact's total like count
                    DatabaseReference likeCountReference = FirebaseDatabase.getInstance()
                        .getReference("artifacts")
                        .child(lotNum)
                        .child("likeCount");

                    // Load the number of likes
                    likeCountReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot countSnapshot) {
                            Integer count = countSnapshot.getValue(Integer.class);
                            if (count != null) {
                                likeCount = count;
                            }

                            // Update the heart icon + count info. by notifying adapter about data loading
                            isStatusLoaded = true;
                            if (onStatusLoaded != null) {
                                onStatusLoaded.run();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    protected Artifact(Parcel in) {
        lotNum = in.readString();
        name = in.readString();
        description = in.readString();
        category = in.readString();
        material = in.readString();
        period = in.readString();
        culturalOrigin = in.readString();
        dimensions = in.readString();
        conditionReport = in.readString();
        location = in.readString();
        acqMethod = in.readString();
        provenance = in.readString();
        accNum = in.readString();
        notes = in.readString();
        image = in.readString();
    }

    public static final Creator<Artifact> CREATOR = new Creator<Artifact>() {
        @Override
        public Artifact createFromParcel(Parcel in) {
            return new Artifact(in);
        }

        @Override
        public Artifact[] newArray(int size) {
            return new Artifact[size];
        }
    };

    public String getLotNum() {
        return lotNum;
    }
    public void setLotNum(String lotNum) {
        this.lotNum = lotNum;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getMaterial() {
        return material;
    }
    public void setMaterial(String material) {
        this.material = material;
    }

    public String getPeriod() {
        return period;
    }
    public void setPeriod(String period) {
        this.period = period;
    }

    public String getCulturalOrigin() {
        return culturalOrigin;
    }
    public void setCulturalOrigin(String culturalOrigin) {
        this.culturalOrigin = culturalOrigin;
    }

    public String getDimensions() {
        return dimensions;
    }
    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getConditionReport() {
        return conditionReport;
    }
    public void setConditionReport(String conditionReport) {
        this.conditionReport = conditionReport;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getAcqMethod() {
        return acqMethod;
    }
    public void setAcqMethod(String acqMethod) {
        this.acqMethod = acqMethod;
    }

    public String getProvenance() {
        return provenance;
    }
    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getAccNum() {
        return accNum;
    }
    public void setAccNum(String accNum) {
        this.accNum = accNum;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(lotNum);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(category);
        parcel.writeString(material);
        parcel.writeString(period);
        parcel.writeString(culturalOrigin);
        parcel.writeString(dimensions);
        parcel.writeString(conditionReport);
        parcel.writeString(location);
        parcel.writeString(acqMethod);
        parcel.writeString(provenance);
        parcel.writeString(accNum);
        parcel.writeString(notes);
        parcel.writeString(image);

    }

    public boolean getIsLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean getIsSaved() {
        return isSaved;
    }


    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void toggleLike(Consumer<String> errorHandler, Runnable onSuccess) {
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            errorHandler.accept("Please login to like artifacts");
            return;
        }

        // Toggle locally
        isLiked = !isLiked;
        if (isLiked) {
            likeCount++;
        } else {
            likeCount--;
        }

        // Apply to database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> updates = new HashMap<>();
        updates.put("/artifacts/" + lotNum + "/likeCount", likeCount);
        updates.put("/users/" + user.getUid() + "/likedAndSavedArtifacts/" + lotNum + "/liked", isLiked);

        ref.updateChildren(updates, (error, ref1) -> {
            if (error != null) {
                // Revert locally on error
                isLiked = !isLiked;
                if (isLiked) {
                    likeCount++;
                } else {
                    likeCount--;
                }
                errorHandler.accept(error.getMessage());
            } else {
                onSuccess.run();
            }
        });
    }
}