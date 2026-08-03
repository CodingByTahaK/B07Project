package com.cscb07.museum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.widget.ImageButton;
import android.widget.Toast;

import org.jetbrains.annotations.UnknownNullability;

public class ArtifactAdapter extends RecyclerView.Adapter<ArtifactAdapter.ArtifactViewHolder> {
    private List<Artifact> artifactList;
    private LikeClick likeClickListener;
    private static final long CLICK_THRESHOLD = 500; // ms

    public interface LikeClick {
        void onLikeClick(Artifact artifact, int position);
    }

    public ArtifactAdapter(List<Artifact> artifactList) {
        this.artifactList = artifactList;
    }

    public ArtifactAdapter(List<Artifact> artifactList, LikeClick likeClickListener){
        this.artifactList = artifactList;
        this.likeClickListener = likeClickListener;
    }

    @NonNull
    @Override
    public ArtifactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_adapater, parent, false);
        return new ArtifactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtifactViewHolder holder, int position) {
        Artifact artifact = artifactList.get(position);
        holder.textViewName.setText(artifact.getName());
        holder.textViewDescription.setText(artifact.getDescription());
        holder.textViewCategory.setText(artifact.getCategory());
        holder.textViewMaterial.setText(artifact.getMaterial());
        holder.textViewPeriod.setText(artifact.getPeriod());

        // Listen for when the isLiked status is loaded from Firebase
        artifact.setOnStatusLoadedListener(() -> {
            // Verify holder is still bound to this artifact
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION && 
                artifactList.get(holder.getAdapterPosition()) == artifact) {
                updateLikeUI(holder, artifact);
            }
        });

        updateLikeUI(holder, artifact);

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // avoid being clicked to frequently
                long currentTime = System.currentTimeMillis();
                if (currentTime - holder.lastClickTime < CLICK_THRESHOLD) {
                    return;
                }
                holder.lastClickTime = currentTime;
                artifact.toggleLike(
                    errorMessage -> {
                        Toast.makeText(v.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        updateLikeUI(holder, artifact); // Revert UI
                    },
                    () -> {
                        if (likeClickListener != null) {
                            likeClickListener.onLikeClick(artifact, holder.getAdapterPosition());
                        }
                    }
                );
                // Update UI instantly
                updateLikeUI(holder, artifact);
            }
        });
    }

    private void updateLikeUI(ArtifactViewHolder holder, @UnknownNullability Artifact item) {
        if (item.getIsLiked()) {
            holder.btnLike.setImageResource(R.drawable.ic_heart_filled);
        } else {
            holder.btnLike.setImageResource(R.drawable.ic_heart_outline);
        }
        holder.tvLikeCount.setText(String.valueOf(item.getLikeCount()));
    }

    @Override
    public int getItemCount() {
        return artifactList.size();
    }

    public static class ArtifactViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDescription, textViewCategory, textViewMaterial, textViewPeriod;
        ImageButton btnLike;
        TextView tvLikeCount;
        long lastClickTime = 0;

        public ArtifactViewHolder(@NonNull View artifactView) {
            super(artifactView);
            textViewName = artifactView.findViewById(R.id.textViewName);
            textViewDescription = artifactView.findViewById(R.id.textViewCategory);
            textViewCategory = artifactView.findViewById(R.id.textViewMaterial);
            textViewMaterial = artifactView.findViewById(R.id.textViewDescription);
            textViewPeriod = artifactView.findViewById(R.id.textViewPeriod);
            btnLike = itemView.findViewById(R.id.btnLike);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
        }
    }
}