package com.cscb07.museum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

public class ArtifactAdapter
        extends RecyclerView.Adapter<ArtifactAdapter.ArtifactViewHolder> {

    private List<Artifact> artifactList;

    private LikeClick likeClickListener;
    private SaveClick saveClickListener;

    private List<String> savedArtifactIDs = new ArrayList<>();

    public interface LikeClick {
        void onLikeClick(Artifact artifact, int position);
    }

    public interface SaveClick {
        void onSaveClick(Artifact artifact);
    }

    public ArtifactAdapter(List<Artifact> artifactList) {
        this.artifactList = artifactList;
    }

    public ArtifactAdapter(
            List<Artifact> artifactList,
            LikeClick likeClickListener) {

        this.artifactList = artifactList;
        this.likeClickListener = likeClickListener;
    }

    @NonNull
    @Override
    public ArtifactViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.activity_item_adapater,
                        parent,
                        false
                );

        return new ArtifactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ArtifactViewHolder holder,
            int position) {

        Artifact artifact = artifactList.get(position);

        holder.textViewName.setText(artifact.getName());
        holder.textViewDescription.setText(artifact.getDescription());
        holder.textViewCategory.setText(artifact.getCategory());
        holder.textViewMaterial.setText(artifact.getMaterial());
        holder.textViewPeriod.setText(artifact.getPeriod());

        // Display the current like state
        likebutton(holder, artifact);

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean newLikeState = !artifact.isLiked();
                artifact.setLiked(newLikeState);

                int newCount;

                if (newLikeState) {
                    newCount = artifact.getLikeCount() + 1;
                } else {
                    newCount = Math.max(0, artifact.getLikeCount() - 1);
                }

                artifact.setLikeCount(newCount);
                likebutton(holder, artifact);

                if (likeClickListener != null) {
                    int adapterPosition = holder.getAdapterPosition();

                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        likeClickListener.onLikeClick(artifact, adapterPosition);
                    }
                }
            }
        });

        // Display the current saved state
        if (savedArtifactIDs.contains(artifact.getLotNum())) {
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

    private void likebutton(
            ArtifactViewHolder holder,
            @UnknownNullability Artifact artifact) {

        if (artifact.isLiked()) {
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

    @Override
    public int getItemCount() {
        return artifactList.size();
    }

    public void setSaveClickListener(
            SaveClick saveClickListener) {

        this.saveClickListener = saveClickListener;
    }

    public void setSavedArtifactIDs(
            List<String> savedArtifactIDs) {

        this.savedArtifactIDs = savedArtifactIDs;
        notifyDataSetChanged();
    }

    public static class ArtifactViewHolder
            extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewDescription;
        TextView textViewCategory;
        TextView textViewMaterial;
        TextView textViewPeriod;
        TextView tvLikeCount;

        ImageButton btnLike;
        ImageButton btnSave;

        public ArtifactViewHolder(
                @NonNull View artifactView) {

            super(artifactView);

            textViewName =
                    artifactView.findViewById(R.id.textViewName);

            textViewDescription =
                    artifactView.findViewById(R.id.textViewDescription);

            textViewCategory =
                    artifactView.findViewById(R.id.textViewCategory);

            textViewMaterial =
                    artifactView.findViewById(R.id.textViewMaterial);

            textViewPeriod =
                    artifactView.findViewById(R.id.textViewPeriod);

            btnLike =
                    artifactView.findViewById(R.id.btnLike);

            btnSave =
                    artifactView.findViewById(R.id.btnSave);

            tvLikeCount =
                    artifactView.findViewById(R.id.tvLikeCount);
        }
    }
}