package com.cscb07.museum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;
import android.widget.ImageButton;

import org.jetbrains.annotations.UnknownNullability;

/**
 * Displays artifact information in a RecyclerView and manages
 * user interactions such as liking, saving, and opening artifacts.
 */
public class ArtifactAdapter extends RecyclerView.Adapter<ArtifactAdapter.ArtifactViewHolder> {
    private List<Artifact> artifactList;
    Context context;
    private final RecyclerExpandedViewInterface recyclerExpandedViewInterface;

    private LikeClick likeClickListener;
    private SaveClick saveClickListener;

    private List<String> savedArtifactIDs = new ArrayList<>();

    private static final long CLICK_THRESHOLD = 500;

//    public interface LikeClick {
//        void onLikeClick(Artifact artifact, int position);
//    }

    /**
     * Callback interface used when a user clicks an artifact's
     * Save/Unsave bookmark button.
     */
    public interface SaveClick {
        void onSaveClick(Artifact artifact);
    }

    public ArtifactAdapter(List<Artifact> artifactList, Context context,
                           RecyclerExpandedViewInterface recyclerExpandedViewInterface,
                           LikeClick likeClickListener) {
        this.context = context;
        this.artifactList = artifactList;
        this.recyclerExpandedViewInterface = recyclerExpandedViewInterface;
        this.likeClickListener = likeClickListener;
    }


    @NonNull
    @Override
    public ArtifactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_adapater, parent, false);
        return new ArtifactViewHolder(view, recyclerExpandedViewInterface);
    }

    // Display a filled bookmark when the artifact is saved,
    // or an outlined bookmark when it is not saved.
    @Override
    public void onBindViewHolder(@NonNull ArtifactViewHolder holder, int position) {
        Artifact artifact = artifactList.get(position);

        holder.textViewName.setText(artifact.getName());
        holder.textViewCategory.setText(artifact.getCategory());
        holder.textViewMaterial.setText(artifact.getMaterial());
        holder.textViewPeriod.setText(artifact.getPeriod());
        Picasso.get().load(artifact.getImage()).into(holder.imageViewPic);

        // Update the like button after Firebase finishes loading.
        artifact.setOnStatusLoadedListener(() -> {

            int adapterPosition = holder.getAdapterPosition();

            if (adapterPosition != RecyclerView.NO_POSITION
                    && adapterPosition < artifactList.size()
                    && artifactList.get(adapterPosition) == artifact) {

                updateLikeUI(holder, artifact);
                updateSaveUI(holder, artifact);
            }
        });

        updateLikeUI(holder, artifact);

        // Notify the fragment when the user clicks Save or Unsave.
        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long currentTime = System.currentTimeMillis();

                if (currentTime - holder.lastClickTime < CLICK_THRESHOLD) {
                    return;
                }

                holder.lastClickTime = currentTime;

                artifact.toggleLike(
                        errorMessage -> {
                            Toast.makeText(
                                    view.getContext(),
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                            ).show();

                            updateLikeUI(holder, artifact);
                        },
                        () -> {
                            if (likeClickListener != null) {

                                int adapterPosition =
                                        holder.getAdapterPosition();

                                if (adapterPosition
                                        != RecyclerView.NO_POSITION) {

                                    likeClickListener.onLikeClick(
                                            artifact,
                                            adapterPosition
                                    );
                                }
                            }
                        }
                );

                updateLikeUI(holder, artifact);
            }
        });

        // Display the correct bookmark icon.
        if (artifact.getIsSaved() || savedArtifactIDs.contains(artifact.getLotNum())) {

            holder.btnSave.setImageResource(
                    R.drawable.ic_bookmark_filled
            );

            holder.btnSave.setContentDescription(
                    "Unsave artifact"
            );

        } else {

            holder.btnSave.setImageResource(
                    R.drawable.ic_bookmark_outline
            );

            holder.btnSave.setContentDescription(
                    "Save artifact"
            );
        }

        holder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (saveClickListener != null) {
                    saveClickListener.onSaveClick(artifact);
                }
            }
        });
    }

    private void updateLikeUI(
            ArtifactViewHolder holder,
            Artifact artifact) {

        if (artifact.getIsLiked()) {
            holder.btnLike.setImageResource(
                    R.drawable.ic_heart_filled
            );
        } else {
            holder.btnLike.setImageResource(
                    R.drawable.ic_heart_outline
            );
        }

        holder.tvLikeCount.setText(
                String.valueOf(artifact.getLikeCount())
        );
    }

    private void updateSaveUI(ArtifactViewHolder holder, Artifact artifact) {
        if (artifact.getIsSaved()) {
            holder.btnSave.setImageResource(R.drawable.ic_bookmark_filled);
        } else {
            holder.btnSave.setImageResource(R.drawable.ic_bookmark_outline);
        }
    }

    @Override
    public int getItemCount() {
        return artifactList.size();
    }

    /**
     * Sets the listener that handles Save/Unsave button clicks.
     *
     * @param saveClickListener the Save/Unsave click listener
     */
    public void setSaveClickListener(
            SaveClick saveClickListener) {

        this.saveClickListener = saveClickListener;
    }

    /**
     * Updates the list of saved artifact lot numbers and refreshes
     * the RecyclerView so the correct bookmark icons are displayed.
     *
     * @param savedArtifactIDs the lot numbers of the user's saved artifacts
     */
    public void setSavedArtifactIDs(
            List<String> savedArtifactIDs) {

        this.savedArtifactIDs = savedArtifactIDs;
        notifyDataSetChanged();
    }

    public static class ArtifactViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewCategory, textViewMaterial, textViewPeriod, tvLikeCount;;
        ImageView imageViewPic;
        ImageButton btnLike;
        ImageButton btnSave;

        long lastClickTime = 0;

        public ArtifactViewHolder(@NonNull View artifactView, RecyclerExpandedViewInterface recyclerExpandedViewInterface) {
            super(artifactView);
            textViewName = artifactView.findViewById(R.id.textViewName);
            textViewCategory = artifactView.findViewById(R.id.textViewCategory);
            textViewMaterial = artifactView.findViewById(R.id.textViewMaterial);
            textViewPeriod = artifactView.findViewById(R.id.textViewPeriod);
            imageViewPic = artifactView.findViewById(R.id.imageView);
            btnLike = artifactView.findViewById(R.id.btnLike);
            btnSave = artifactView.findViewById(R.id.btnSave);
            tvLikeCount = artifactView.findViewById(R.id.tvLikeCount);

            //attaching onClick listener to each artifact
            artifactView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerExpandedViewInterface != null){
                        int position  = getAdapterPosition();

                        if(position != RecyclerView.NO_POSITION){
                            recyclerExpandedViewInterface.onArtifactClick(position);
                        }
                    }
                }
            });

        }
    }
}